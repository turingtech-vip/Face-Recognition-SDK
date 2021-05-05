package com.turingtech.idverify;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.turingtech.face.FaceMethod;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;

import qrcode.QrCodeActivity;

public class ActivationActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ActivationActivity";

    private EditText mEditHWID;
    private String mHWID;
    private File mLastFile;
    private Context mContext;
    private FaceMethod mFaceMethod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        mContext = this;
        ((Button) findViewById(R.id.btnHWID)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnLicense)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnScan)).setOnClickListener(this);


        mFaceMethod = new FaceMethod(this);
        mHWID = mFaceMethod.getCurrentHWID();
        mEditHWID = (EditText) findViewById(R.id.editHWID);

        mEditHWID.setText(mHWID);

        updateQRCode();
                    }

    @Override
    public void onResume() {
        super.onResume();

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
                }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                case 1: {
                    mHWID = mFaceMethod.getCurrentHWID();
                    mEditHWID = (EditText) findViewById(R.id.editHWID);
                    mEditHWID.setText(mHWID);

                    updateQRCode();
                    break;
            }
        }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHWID: {
                FileChooser fileChooser = new FileChooser(ActivationActivity.this, "Select folder", FileChooser.DialogType.SELECT_DIRECTORY, mLastFile);
                FileChooser.FileSelectionCallback callback = new FileChooser.FileSelectionCallback() {

                    @Override
                    public void onSelect(File file) {
                        //Do something with the selected file
                        Log.e(TAG, "file path: " + file.getPath());
                        mLastFile = file;

                        try {
                            Base.saveStringToFile(mContext, file.getPath() + "/hwid.txt", mHWID);
                            Toast.makeText(getBaseContext(), "File saved successfully!",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                fileChooser.show(callback);

                break;
            }
            case R.id.btnLicense: {
                FileChooser fileChooser = new FileChooser(ActivationActivity.this, "Select license file", FileChooser.DialogType.SELECT_FILE, mLastFile);
                FileChooser.FileSelectionCallback callback = new FileChooser.FileSelectionCallback() {

                    @Override
                    public void onSelect(File file) {
                        //Do something with the selected file
                        Log.e(TAG, "file path: " + file.getPath());
                        mLastFile = file;

                        try {
                            String licenseStr = Base.getStringFromFile(file.getPath());
                            Log.e(TAG, "licenseStr: " + licenseStr);

                            int activated = mFaceMethod.setActivation(licenseStr);
                            Log.e(TAG, "setActivation: " + activated);

                            if (activated != FaceMethod.FACE_SDK_SUCCESS) {
                                Base.showMessage(mContext, activated);
                            } else {
                                Base.saveStringToFile(mContext, Base.getAppDir(mContext) + "/license.txt", licenseStr);
                                Intent intent = new Intent();
                                intent.putExtra("Result", activated);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                fileChooser.show(callback);

                break;
            }
            case R.id.btnScan: {
                if(askForPermission(Manifest.permission.CAMERA, 1) == 1) {
                Intent intent = new Intent(this, QrCodeActivity.class);
                startActivityForResult(intent, 0);
                }
                break;
            }
        }
    }

    private int askForPermission(String permission, Integer requestCode) {

        if (ContextCompat.checkSelfPermission(ActivationActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivationActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ActivationActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ActivationActivity.this, new String[]{permission}, requestCode);
            }
            return 0;
        } else {
            return 1;
        }
    }

    public void updateQRCode() {
        if(mHWID == null)
            return;

        com.google.zxing.Writer writer = new QRCodeWriter();
        try {
            BitMatrix bm = writer.encode(mHWID, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap qrcodeBmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < 400; i++) {
                for (int j = 0; j < 400; j++) {
                    qrcodeBmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            ((ImageView) findViewById(R.id.hwid_qrcode_view)).setImageBitmap(qrcodeBmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "requestCode = " + requestCode + " resultCode = " + resultCode);
        switch (requestCode) {
            case 0: {
                if (resultCode == RESULT_OK) {
                    try {
                        String licenseStr = data.getExtras().getString("Result");
                        int activated = mFaceMethod.setActivation(licenseStr);
                        Log.e(TAG, "setActivation: " + activated);

                        if (activated != FaceMethod.FACE_SDK_SUCCESS) {
                            Base.showMessage(mContext, activated);
                        } else {
                            Base.saveStringToFile(mContext, Base.getAppDir(mContext) + "/license.txt", licenseStr);
                            Intent intent = new Intent();
                            intent.putExtra("Result", activated);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

}
