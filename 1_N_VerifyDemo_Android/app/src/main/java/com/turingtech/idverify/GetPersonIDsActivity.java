package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.turingtech.face.FaceMethod;

public class GetPersonIDsActivity extends Activity
{
    Context mContext;
    EditText mGroupIdEdit;
    EditText mPersonIdsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_person_ids);

        mContext = this;
        mGroupIdEdit = (EditText) findViewById(R.id.group_id_edit);
        mPersonIdsEdit = (EditText) findViewById(R.id.person_ids_edit);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String groupIDStr = mGroupIdEdit.getText().toString();
                    int groupID = Integer.parseInt(groupIDStr);

                    int[] personIds = FaceMethod.getPersonIDs(groupID);
                    if(personIds == null)
                        mPersonIdsEdit.setText("");
                    else
                    {
                        String str = "";
                        for(int i = 0; i < personIds.length; i ++)
                            str += personIds[i] + ", ";

                        mPersonIdsEdit.setText(str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
