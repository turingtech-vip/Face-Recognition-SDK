package com.turingtech.idverify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.turingtech.face.FaceMethod;

public class AddGroupActivity extends Activity {

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        mContext = this;

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idEdit = (EditText) findViewById(R.id.editText);
                try {
                    String str = idEdit.getText().toString();
                    int groupID = Integer.parseInt(str);

                    int ret = FaceMethod.addGroup(groupID);
                    if(ret != FaceMethod.FACE_SDK_SUCCESS) {
                        Base.showMessage(mContext, ret);
                    } else {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                        alertBuilder.setTitle("Information").setMessage("Success!").setPositiveButton(android.R.string.ok, null).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
