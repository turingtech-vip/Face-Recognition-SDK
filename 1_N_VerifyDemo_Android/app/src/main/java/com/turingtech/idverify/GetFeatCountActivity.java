package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.turingtech.face.FaceMethod;

public class GetFeatCountActivity extends Activity {

    Context mContext;
    EditText mPersonIDEdit;
    EditText mFeatCountEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_feat_count);

        mContext = this;
        mPersonIDEdit = (EditText) findViewById(R.id.person_id_edit);
        mFeatCountEdit = (EditText) findViewById(R.id.feat_count_edit);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String personIDStr = mPersonIDEdit.getText().toString();
                    int personID = Integer.parseInt(personIDStr);
                    int ret = FaceMethod.getFeatCount(personID);
                    mFeatCountEdit.setText("" + ret);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
