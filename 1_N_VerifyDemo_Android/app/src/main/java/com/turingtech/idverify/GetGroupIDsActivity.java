package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.turingtech.face.FaceMethod;

public class GetGroupIDsActivity extends Activity {

    private Context mContext;

    EditText mGroupIDsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_group_ids);

        mContext = this;
        mGroupIDsEdit = (EditText) findViewById(R.id.group_ids_edit);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int[] groupIds = FaceMethod.getGroupIDs();
                    if(groupIds == null)
                        mGroupIDsEdit.setText("");
                    else
                    {
                        String str = "";
                        for(int i = 0; i < groupIds.length; i ++)
                            str += groupIds[i] + ", ";

                        mGroupIDsEdit.setText(str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
