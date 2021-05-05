package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.turingtech.face.FaceMethod;

import java.io.File;

public class EnrollActivity extends Activity {

    private Context mContext;

    ImageView mViewImg;
    EditText mGroupIDEdit;
    EditText mPersonIDEdit;

    int mCallIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        mContext = this;
        mViewImg = (ImageView) findViewById(R.id.image_view);
        mGroupIDEdit = (EditText) findViewById(R.id.group_id_edit);
        mPersonIDEdit = (EditText) findViewById(R.id.person_id_edit);


        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(EnrollActivity.this, "Select image file", FileChooser.DialogType.SELECT_FILE, new File(Base.mLastPath));
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

                            String personIDStr = mPersonIDEdit.getText().toString();
                            int personID = Integer.parseInt(personIDStr);
                            int ret = 0;
                            if((mCallIdx % 2) == 0) {
                                ret = FaceMethod.enroll(groupID, personID, input_img);
                                Base.showMessage(mContext, ret);
                            } else {
                                int[] faceResults = new int[4];
                                float[] qualities = new float[10];
                                ret = FaceMethod.detectFace(input_img, faceResults, qualities);
                                ret = FaceMethod.enroll(groupID, personID, null);
                                Base.showMessage(mContext, ret);
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
