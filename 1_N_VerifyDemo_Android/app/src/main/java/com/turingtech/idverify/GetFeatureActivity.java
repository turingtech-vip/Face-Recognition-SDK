package com.turingtech.idverify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.turingtech.face.FaceMethod;

import java.io.File;

public class GetFeatureActivity extends Activity {

    private Context mContext;

    ImageView mViewImg;
    ResultView mResultView;
    EditText mFaceResultsEdit;
    EditText mFeatPathEdit;

    int mCallIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_feature);

        mContext = this;

        mViewImg = (ImageView) findViewById(R.id.image_view);
        mResultView = (ResultView) findViewById(R.id.result_view);
        mFaceResultsEdit = (EditText) findViewById(R.id.face_results_edit);
        mFeatPathEdit = (EditText) findViewById(R.id.feat_path_edit);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(GetFeatureActivity.this, "Select image file", FileChooser.DialogType.SELECT_FILE, new File(Base.mLastPath));
                FileChooser.FileSelectionCallback callback = new FileChooser.FileSelectionCallback() {

                    @Override
                    public void onSelect(File file) {
                        //Do something with the selected file
                        Base.mLastPath = file.getAbsolutePath();

                        try {
                            BitmapFactory.Options op = new BitmapFactory.Options();
                            op.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap sceneImg = BitmapFactory.decodeFile(file.getPath(), op);
                            Bitmap input_img = Base.prepareInputImage(sceneImg);

                            mViewImg.setImageBitmap(input_img);

                            int[] faceResults = new int[4];
                            byte[] feats = null;

                            if((mCallIdx % 2) == 0) {
                                feats = FaceMethod.getFeature(input_img, faceResults);
                            } else {
                                float[] qualities = new float[1];
                                FaceMethod.detectFace(input_img, faceResults, qualities);
                                feats = FaceMethod.getFeature(null, faceResults);
                            }

                            if(feats == null)
                                Base.showMessage(mContext, 10000);
                            else {
                                String str = "";
                                for(int i = 0; i < 4; i ++) {
                                    str += faceResults[i] + ", ";
                                }
                                mFaceResultsEdit.setText(str);

                                try {
                                    String featPath = mFeatPathEdit.getText().toString() + "/feats.bin";
                                    Base.saveDataToFile(mContext, featPath, feats);

                                    Toast.makeText(getBaseContext(), "File saved successfully!\n" + featPath,
                                            Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                                    alertBuilder.setTitle("Warning").setMessage("null!").setPositiveButton(android.R.string.ok, null).show();

                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mViewImg.setImageResource(android.R.color.darker_gray);
                        }
                        mCallIdx ++;
                    }
                };
                fileChooser.show(callback);
            }
        });

        ((Button)findViewById(R.id.open_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser fileChooser = new FileChooser(GetFeatureActivity.this, "Select folder", FileChooser.DialogType.SELECT_DIRECTORY, new File(Base.mLastPath));
                FileChooser.FileSelectionCallback callback = new FileChooser.FileSelectionCallback() {

                    @Override
                    public void onSelect(File file) {
                        //Do something with the selected file
                        Base.mLastPath = file.getAbsolutePath();

                        try {
                            mFeatPathEdit.setText(Base.mLastPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                fileChooser.show(callback);
            }
        });
    }
}
