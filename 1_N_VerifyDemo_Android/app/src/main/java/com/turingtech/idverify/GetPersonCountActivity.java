package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.turingtech.face.FaceMethod;

public class GetPersonCountActivity extends Activity {

    private Context mContext;

    EditText mGroupIDEdit;
    EditText mPersonCountEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_person_count);

        mContext = this;
        mGroupIDEdit = (EditText) findViewById(R.id.group_id_edit);
        mPersonCountEdit = (EditText) findViewById(R.id.perosn_count_edit);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String groupIDStr = mGroupIDEdit.getText().toString();
                    int groupID = Integer.parseInt(groupIDStr);

                    int ret = FaceMethod.getPersonCount(groupID);
                    mPersonCountEdit.setText("" + ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
