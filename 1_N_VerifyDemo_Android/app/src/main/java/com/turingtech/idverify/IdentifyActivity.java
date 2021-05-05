package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.turingtech.face.FaceMethod;

import java.io.File;

public class IdentifyActivity extends Activity {

    private Context mContext;

    ImageView mViewImg;
    ResultView mResultView;
    EditText mGroupIDEdit;
    EditText mThresholdEdit;
    EditText mCandNumEdit;
    EditText mResultCount;
    EditText mFindIdsEdit;
    EditText mScoresEdit;
    EditText mOtherResultsEdit;
    int mCallIdx = 0;
    Rect[] m_detectedfaces = new Rect[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        mContext = this;
        mViewImg = (ImageView) findViewById(R.id.image_view);
        mResultView = (ResultView) findViewById(R.id.result_view);
        mGroupIDEdit = (EditText) findViewById(R.id.group_id_edit);
        mThresholdEdit = (EditText) findViewById(R.id.threshold_edit);
        mCandNumEdit = (EditText) findViewById(R.id.cand_num_edit);
        mResultCount = (EditText) findViewById(R.id.result_count_edit);
        mFindIdsEdit = (EditText) findViewById(R.id.find_ids_edit);
        mScoresEdit = (EditText) findViewById(R.id.scores_edit);
        mOtherResultsEdit = (EditText) findViewById(R.id.other_results_edit);

        float[] rVersion = new float[1];
        float[] rDefaultScore = new float[1];
        FaceMethod.getSDKParam(rVersion, rDefaultScore);
        mThresholdEdit.setText(String.format("%f", (float)rDefaultScore[0]));

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(IdentifyActivity.this, "Select image file", FileChooser.DialogType.SELECT_FILE, new File(Base.mLastPath));
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

                            String groupIDStr = mGroupIDEdit.getText().toString();
                            int groupID = Integer.parseInt(groupIDStr);

                            String thresholdStr = mThresholdEdit.getText().toString();
                            float threshold = Float.parseFloat(thresholdStr);

                            String candNumStr = mCandNumEdit.getText().toString();
                            int candNum = Integer.parseInt(candNumStr);

                            int[] resultCount = new int[1];
                            int[] findIds = new int[candNum];
                            float[] scores = new float[candNum];
                            int[] otherResults = new int[5];
                            int ret = FaceMethod.FACE_SDK_SUCCESS;

                            long oldTime = SystemClock.uptimeMillis();
                            if((mCallIdx % 2) == 0) {
                                ret = FaceMethod.identify(groupID, threshold, input_img, candNum, findIds, scores, resultCount, otherResults);
                            } else {
                                int[] faceResults = new int[4];
                                float[] qualities = new float[10];
                                ret = FaceMethod.detectFace(input_img, faceResults, qualities);
                                ret = FaceMethod.identify(groupID, threshold, null, candNum, findIds, scores, resultCount, otherResults);
                            }

                            Log.e("FaceSDK", "Verify Time: " + (SystemClock.uptimeMillis() - oldTime));

                            if(ret != FaceMethod.FACE_SDK_SUCCESS)
                            {
                                Base.showMessage(mContext, ret);
                                mFindIdsEdit.setText("");
                                mScoresEdit.setText("");
                                mOtherResultsEdit.setText("");
                                Rect faceRect = new Rect(0, 0, 0, 0);
                                m_detectedfaces[0] = faceRect;
                                mResultView.setResult(input_img.getWidth(), input_img.getHeight(), m_detectedfaces, 1);
                            }
                            else {

                                mResultCount.setText("" + resultCount[0]);

                                String str = "";
                                for(int i = 0; i < resultCount[0]; i ++)
                                    str += findIds[i] + ", ";

                                mFindIdsEdit.setText(str);
                                str = "";
                                for(int i = 0; i < resultCount[0]; i ++)
                                    str += scores[i] + ", ";
                                mScoresEdit.setText(str);

                                str = "";
                                for(int i = 0; i < 5; i ++) {
                                    str += otherResults[i] + ", ";
                                }
                                mOtherResultsEdit.setText(str);

                                Rect faceRect = new Rect((int)otherResults[1], (int)otherResults[2], (int)(otherResults[1] + otherResults[3]), (int)(otherResults[2] + otherResults[4]));
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
