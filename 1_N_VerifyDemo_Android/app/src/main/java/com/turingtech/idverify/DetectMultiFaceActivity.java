package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.turingtech.face.FaceMethod;

import java.io.File;

public class DetectMultiFaceActivity extends Activity {

    private Context mContext;

    ImageView mViewImg;
    ResultView mResultView;
    EditText mFaceResultsEdit;
    EditText mPanEdit;
    EditText mMaxFaceNumEdit;
    private final int MAX_FACE_NUM = 20;
    Rect[] m_detectedfaces = new Rect[MAX_FACE_NUM];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_multi_face);

        mContext = this;
        mViewImg = (ImageView) findViewById(R.id.image_view);
        mResultView = (ResultView) findViewById(R.id.result_view);
        mFaceResultsEdit = (EditText) findViewById(R.id.face_results_edit);
        mPanEdit = (EditText) findViewById(R.id.pan_edit);
        mMaxFaceNumEdit = (EditText) findViewById(R.id.max_face_num_edit);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(DetectMultiFaceActivity.this, "Select image file", FileChooser.DialogType.SELECT_FILE, new File(Base.mLastPath));
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

                            String maxFaceNumStr = mMaxFaceNumEdit.getText().toString();
                            int nMaxFaceNum = Integer.parseInt(maxFaceNumStr);

                            int[] faceResults = new int[4 * nMaxFaceNum];
                            int[] retFaceCount = new int[1];

                            long lastTime = System.currentTimeMillis();
                            int ret = FaceMethod.detectMultiFace(input_img, nMaxFaceNum, faceResults, retFaceCount);
                            Log.e("DetectMultiFaceActivity", "detectFace time: " + (System.currentTimeMillis() - lastTime));
                            if(ret != FaceMethod.FACE_SDK_SUCCESS) {
                                mFaceResultsEdit.setText("");
                                mPanEdit.setText("");
                                for (int i = 0 ; i < MAX_FACE_NUM ; i ++) {
                                    Rect faceRect = new Rect(0, 0, 0, 0);
                                    m_detectedfaces[i] = faceRect;
                                }
                                mResultView.setResult(input_img.getWidth(), input_img.getHeight(), m_detectedfaces, MAX_FACE_NUM);

                                Base.showMessage(mContext, ret);
                            } else {
                                int nRetFaceNum = retFaceCount[0];
                                Rect[] retFaceRect = new Rect[nRetFaceNum];
                                String str = "";
                                for(int i = 0; i < nRetFaceNum; i ++) {
                                    str += faceResults[i * 4] + ", " + faceResults[i * 4 + 1] + ", " + faceResults[i * 4 + 2] + ", " + faceResults[i * 4 + 3];
                                    if (i + 1 < nRetFaceNum)
                                        str += "\r\n";

                                    Rect faceRect = new Rect((int)faceResults[i * 4], (int)faceResults[i * 4 + 1],
                                                            (int)(faceResults[i * 4] + faceResults[i * 4 + 2]), (int)(faceResults[i * 4 + 1] + faceResults[i * 4 + 3]));
                                    m_detectedfaces[i] = faceRect;
                                }
                                mFaceResultsEdit.setText(str);

                                str = "" + nRetFaceNum;
                                mPanEdit.setText("" + str);

//                                Rect faceRect = new Rect((int)faceResults[0], (int)faceResults[1], (int)(faceResults[0] + faceResults[2]), (int)(faceResults[1] + faceResults[3]));
//                                mResultView.setResult(sceneImg.getWidth(), sceneImg.getHeight(), faceRect);
                                mResultView.setResult(input_img.getWidth(), input_img.getHeight(), m_detectedfaces, nRetFaceNum);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            mViewImg.setImageResource(android.R.color.darker_gray);
                        }
                    }
                };
                fileChooser.show(callback);
            }
        });
    }
}
