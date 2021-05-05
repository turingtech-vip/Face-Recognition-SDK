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

public class DetectFaceWithQuality extends Activity {

    private Context mContext;

    ImageView mViewImg;
    ResultView mResultView;
    EditText mFaceResultsEdit;
    EditText mPanEdit;
    EditText mStateEdit;
    Rect[] m_detectedfaces = new Rect[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_face_with_quality);

        mContext = this;
        mViewImg = (ImageView) findViewById(R.id.image_view);
        mResultView = (ResultView) findViewById(R.id.result_view);
        mFaceResultsEdit = (EditText) findViewById(R.id.face_results_edit);
        mPanEdit = (EditText) findViewById(R.id.pan_edit);
        mStateEdit = (EditText) findViewById(R.id.state_edit);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(DetectFaceWithQuality.this, "Select image file", FileChooser.DialogType.SELECT_FILE, new File(Base.mLastPath));
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

                            float[] faceResults = new float[10];
                            int[] faceState = new int[1];

                            long lastTime = System.currentTimeMillis();
                            int ret = FaceMethod.detectFaceWithQuality(input_img, faceResults, faceState);
                            Log.e("DetectFaceWithQuality", "detectFace time: " + (System.currentTimeMillis() - lastTime));
                            if(ret != FaceMethod.FACE_SDK_SUCCESS) {
                                mFaceResultsEdit.setText("");
                                mPanEdit.setText("");
                                mStateEdit.setText("");
                                Rect faceRect = new Rect(0,0,0,0);
                                m_detectedfaces[0] = faceRect;
                                mResultView.setResult(input_img.getWidth(), input_img.getHeight(), m_detectedfaces, 1);
                                Base.showMessage(mContext, ret);
                            } else {
                                String str = "";
                                for(int i = 0; i < 4; i ++) {
                                    str += (int)faceResults[i] + ", ";
                                }
                                mFaceResultsEdit.setText(str);

                                str = "";
                                for(int i = 4; i < 7; i ++) {
                                    str += faceResults[i] + ", ";
                                }
                                mPanEdit.setText("" + str);

                                str = "" + faceState[0];
                                mStateEdit.setText("" + str);

                                Rect faceRect = new Rect((int)faceResults[0], (int)faceResults[1], (int)(faceResults[0] + faceResults[2]), (int)(faceResults[1] + faceResults[3]));
                                m_detectedfaces[0] = faceRect;
                                mResultView.setResult(input_img.getWidth(), input_img.getHeight(), m_detectedfaces, 1);
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
