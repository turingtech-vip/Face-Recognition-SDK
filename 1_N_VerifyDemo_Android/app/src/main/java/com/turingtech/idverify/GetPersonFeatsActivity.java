package com.turingtech.idverify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.turingtech.face.FaceMethod;

import java.io.File;

public class GetPersonFeatsActivity extends Activity {

    Context mContext;
    EditText mPersonIDEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_person_feats);

        mContext = this;
        mPersonIDEdit = (EditText) findViewById(R.id.person_id_edit);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileChooser fileChooser = new FileChooser(GetPersonFeatsActivity.this, "Select folder", FileChooser.DialogType.SELECT_DIRECTORY, new File(Base.mLastPath));
                FileChooser.FileSelectionCallback callback = new FileChooser.FileSelectionCallback() {

                    @Override
                    public void onSelect(File file) {
                        //Do something with the selected file
                        Base.mLastPath = file.getAbsolutePath();

                        try {
                            String personIDStr = mPersonIDEdit.getText().toString();
                            int personID = Integer.parseInt(personIDStr);

                            byte[] feats = FaceMethod.getPersonFeats(personID);
                            if(feats == null) {
                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                                alertBuilder.setTitle("Warning").setMessage("null!").setPositiveButton(android.R.string.ok, null).show();
                            } else {
                                Log.e("GetPersonFeatsActivity", "feat size = " + feats.length);

                                String featPath = Base.mLastPath + "/feats.bin";
                                Base.saveDataToFile(mContext, featPath, feats);

                                Toast.makeText(getBaseContext(), "File saved successfully!\n" + featPath,
                                        Toast.LENGTH_SHORT).show();
                            }

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
