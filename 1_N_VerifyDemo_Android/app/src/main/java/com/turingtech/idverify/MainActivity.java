package com.turingtech.idverify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.turingtech.face.FaceMethod;

//import junit.framework.Test;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

//    public static final float DEFAULT_THRESHOLD = 79.0f;
    private static final String[] TestTitles = new String[]{
            "addGroup",
            "deleteGroup",
            "enroll",
            "identify",
            "verify",
            "detectFace",
            "detectMultiFace",
            "detectFaceWithQuality",
            "getGroupCount",
            "getPersonCount",
            "getFeatureSize",
            "getGroupIDs",
            "getPersonIDs",
            "getPersonFeats",
            "enrollWithFeature",
            "getFeatCount",
            "deletePerson",
            "getFeature",
    };

    private ListView mTestListView;
    private TestItemAdapter mTestListAdapter;


    private int mActiviated = FaceMethod.FACE_SDK_ACTIVATION_SERIAL_UNKNOWN;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Log.e(TAG, "app dir: " + Base.getAppDir(mContext));

//        File resFile = new File(Base.getAppDir(mContext) + "/data.otg");
//        if (!resFile.exists())
            Base.copyRes(mContext, Base.getAppDir(mContext), R.raw.data, "/data.otg");
        Base.copyRes(mContext, Base.getAppDir(mContext), R.raw.ocu, "/ocu.mnn");
        Base.copyRes(mContext, Base.getAppDir(mContext), R.raw.rfbd, "/rfbd.mnn");
        Base.copyRes(mContext, Base.getAppDir(mContext), R.raw.vanl, "/vanl.mnn");

        FaceMethod faceMethod = new FaceMethod(this);
        String hwid = faceMethod.getCurrentHWID();
        Log.e(TAG, "hwid: " + hwid);

        try {
            String license = Base.getStringFromFile(Base.getAppDir(mContext) + "/license.txt");
            mActiviated = faceMethod.setActivation(license);
            Log.e(TAG, "setActivation: " + mActiviated);

            if (mActiviated == FaceMethod.FACE_SDK_SUCCESS) {
                int ret = FaceMethod.initializeSDK(Base.getAppDir(mContext), Base.getAppDir(mContext));
                Log.e(TAG, "initializeSDK: " + ret);

                float[] rVersion = new float[1];
                float[] rDefaultScore = new float[1];
                FaceMethod.getSDKParam(rVersion, rDefaultScore);
                Log.e(TAG, "sdk version = " + rVersion[0] + "   rDefault Score = " + rDefaultScore[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTestListView = (ListView) findViewById(R.id.test_view);

        mTestListAdapter = new TestItemAdapter(this, TestTitles);
        mTestListView.setAdapter(mTestListAdapter);

        mTestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(mContext, AddGroupActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(mContext, DeleteGroupActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(mContext, EnrollActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(mContext, IdentifyActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(mContext, VerifyActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(mContext, DetectFaceActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(mContext, DetectMultiFaceActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent = new Intent(mContext, DetectFaceWithQuality.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent = new Intent(mContext, GetPersonCountActivity.class);
                        startActivity(intent);
                        break;
                    case 11:
                        intent = new Intent(mContext, GetGroupIDsActivity.class);
                        startActivity(intent);
                        break;
                    case 12:
                        intent = new Intent(mContext, GetPersonIDsActivity.class);
                        startActivity(intent);
                        break;
                    case 13:
                        intent = new Intent(mContext, GetPersonFeatsActivity.class);
                        startActivity(intent);
                        break;
                    case 14:
                        intent = new Intent(mContext, EnrollFromFeatsActivity.class);
                        startActivity(intent);
                        break;
                    case 15:
                        intent = new Intent(mContext, GetFeatCountActivity.class);
                        startActivity(intent);
                        break;
                    case 16:
                        intent = new Intent(mContext, DeletePersonActivity.class);
                        startActivity(intent);
                        break;
                    case 17:
                        intent = new Intent(mContext, GetFeatureActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mActiviated != FaceMethod.FACE_SDK_SUCCESS) {
            Intent intent = new Intent(this, ActivationActivity.class);
            startActivityForResult(intent, 0);
        }

        mTestListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0: {
                if (resultCode == RESULT_OK) {
                    mActiviated = data.getExtras().getInt("Result");
                    if (mActiviated == FaceMethod.FACE_SDK_SUCCESS) {
                        int ret = FaceMethod.initializeSDK(Base.getAppDir(mContext), "/mnt/sdcard/");
                        Log.e(TAG, "initializeSDK: " + ret);

                        Intent intent = new Intent();
                        intent.putExtra("Result", ret);
                        setResult(RESULT_OK, intent);
                    }
                }
                break;
            }
        }
    }

    public class TestItemAdapter extends BaseAdapter {

        private Context context;
        private String[] titles;

        public TestItemAdapter(Context c, String[] titles) {
            this.context = c;
            this.titles = titles;
        }

        public int getCount() {
            return titles.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.main_row_lyt, null);
            }

            TextView titleTxt = (TextView) convertView.findViewById(R.id.title_txt);
            titleTxt.setText(titles[position]);

            if (position == 8) {
                TextView contentsView = (TextView) convertView.findViewById(R.id.contents_txt);
                contentsView.setText("" + FaceMethod.getGroupCount());
            } else if (position == 9) {
                TextView contentsView = (TextView) convertView.findViewById(R.id.contents_txt);
                contentsView.setText("" + FaceMethod.getPersonCount(-1));
            } else if(position == 10) {
                TextView contentsView = (TextView) convertView.findViewById(R.id.contents_txt);
                contentsView.setText(FaceMethod.getFeatureSize() + "byte");
            }
            return convertView;
        }
    }

}
