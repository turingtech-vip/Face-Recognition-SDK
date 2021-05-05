package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.turingtech.face.FaceMethod;

import java.io.File;

public class VerifyActivity extends Activity {

    private Context mContext;

    ImageView mViewImg;
    ResultView mResultView;
    EditText mPersonIDEdit;
    EditText mThresholdEdit;
    EditText mScoreEdit;
    EditText mOtherResultsEdit;
    int mCallIdx = 0;
    Rect[] m_detectedfaces = new Rect[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mContext = this;
        mViewImg = (ImageView) findViewById(R.id.image_view);
        mResultView = (ResultView) findViewById(R.id.result_view);
        mPersonIDEdit = (EditText) findViewById(R.id.person_id_edit);
        mThresholdEdit = (EditText) findViewById(R.id.threshold_edit);
        mScoreEdit = (EditText) findViewById(R.id.score_edit);
        mOtherResultsEdit = (EditText) findViewById(R.id.other_results_edit);

        float[] rVersion = new float[1];
        float[] rDefaultScore = new float[1];
        FaceMethod.getSDKParam(rVersion, rDefaultScore);
        mThresholdEdit.setText(String.format("%f", (float)rDefaultScore[0]));

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(VerifyActivity.this, "Select image file", FileChooser.DialogType.SELECT_FILE, new File(Base.mLastPath));
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

                            String personIDStr = mPersonIDEdit.getText().toString();
                            int personID = Integer.parseInt(personIDStr);

                            String thresholdStr = mThresholdEdit.getText().toString();
                            float threshold = Float.parseFloat(thresholdStr);

                            float[] score = new float[1];
                            int[] otherResults = new int[5];
                            int ret;

                            if((mCallIdx % 2) == 0) {
                                ret = FaceMethod.verify(personID, threshold, input_img, score, otherResults);
                            } else {
                                int[] faceResults = new int[4];
                                float[] qualities = new float[10];
                                ret = FaceMethod.detectFace(input_img, faceResults, qualities);
                                ret = FaceMethod.verify(personID, threshold, null, score, otherResults);
                            }

                            Base.showMessage(mContext, ret);
                            if(ret == FaceMethod.FACE_SDK_VERIFY_SUCCESS || ret == FaceMethod.FACE_SDK_VERIFY_FAILED) {
                                mScoreEdit.setText("" + score[0]);

                                String str = "";
                                for(int i = 0; i < 5; i ++) {
                                    str += otherResults[i] + ", ";
                                }
                                mOtherResultsEdit.setText(str);

                                Rect faceRect = new Rect((int)otherResults[1], (int)otherResults[2], (int)(otherResults[1] + otherResults[3]), (int)(otherResults[2] + otherResults[4]));
                                m_detectedfaces[0] = faceRect;
                                mResultView.setResult(input_img.getWidth(), input_img.getHeight(), m_detectedfaces, 1);
                            }
                            else
                            {
                                mScoreEdit.setText("");
                                mOtherResultsEdit.setText("");
                                Rect faceRect = new Rect(0, 0, 0, 0);
                                m_detectedfaces[0] = faceRect;
                                mResultView.setResult(input_img.getWidth(), input_img.getHeight(), m_detectedfaces, 1);
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
    }
}
