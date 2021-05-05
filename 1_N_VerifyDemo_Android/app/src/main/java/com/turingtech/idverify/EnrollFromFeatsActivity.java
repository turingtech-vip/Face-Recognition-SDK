package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.turingtech.face.FaceMethod;

import java.io.File;

public class EnrollFromFeatsActivity extends Activity {

    private Context mContext;

    EditText mGroupIDEdit;
    EditText mPersonIDEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_from_feats);

        mContext = this;
        mGroupIDEdit = (EditText) findViewById(R.id.group_id_edit);
        mPersonIDEdit = (EditText) findViewById(R.id.person_id_edit);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(EnrollFromFeatsActivity.this, "Select feat file", FileChooser.DialogType.SELECT_FILE, new File(Base.mLastPath));
                FileChooser.FileSelectionCallback callback = new FileChooser.FileSelectionCallback() {

                    @Override
                    public void onSelect(File file) {
                        //Do something with the selected file
                        Base.mLastPath = file.getAbsolutePath();

                        try {
                            String groupIDStr = mGroupIDEdit.getText().toString();
                            int groupID = Integer.parseInt(groupIDStr);

                            String personIDStr = mPersonIDEdit.getText().toString();
                            int personID = Integer.parseInt(personIDStr);

                            byte[] feats = Base.readBytesFromFile(file.getAbsolutePath());
                            int ret = FaceMethod.enrollWithFeature(groupID, personID, feats);
                            Base.showMessage(mContext, ret);

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
