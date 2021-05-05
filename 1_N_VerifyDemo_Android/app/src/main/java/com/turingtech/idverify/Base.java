package com.turingtech.idverify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import com.turingtech.face.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by demid on 4/22/2017.
 */

public class Base {

    public static String mLastPath = "";
    public static final int SDK_MIN_IMAGE_WIDTH = 1080;
    public static final int SDK_MAX_IMAGE_HEIGHT = 1920;

    public static void showMessage(Context context, int messageID) {
        String message = "Unkown Error";
        if(messageID == FaceMethod.FACE_SDK_ACTIVATION_SERIAL_UNKNOWN) {
            message = "SDK_ACTIVATION_SERIAL_UNKNOWN";
        } else if(messageID == FaceMethod.FACE_SDK_ACTIVATION_EXPIRED) {
            message = "SDK_ACTIVATION_EXPIRED";
        } else if(messageID == FaceMethod.FACE_SDK_NOT_INITIALIZED) {
            message = "SDK_NOT_INITIALIZED";
        } else if(messageID == FaceMethod.FACE_SDK_ALREADY_INITIALIZED) {
            message = "SDK_NO_RES_FILE";
        } else if(messageID == FaceMethod.FACE_SDK_NO_RES_FILE) {
            message = "SDK_ALREADY_INITIALIZED";
        } else if(messageID == FaceMethod.FACE_SDK_NOT_FACE_DETECTED) {
            message = "SDK_NOT_FACE_DETECTED";
        } else if(messageID == FaceMethod.FACE_SDK_NOT_ENROLLED) {
            message = "SDK_NOT_ENROLLED";
        } else if(messageID == FaceMethod.FACE_SDK_BAD_PARAMETER) {
            message = "SDK_BAD_PARAMETER";
        } else if(messageID == FaceMethod.FACE_SDK_IMG_ERROR) {
            message = "SDK_IMG_ERROR";
        } else if(messageID == FaceMethod.FACE_SDK_VERIFY_FAILED) {
            message = "SDK_VERIFY_FAILED";
        } else if(messageID == FaceMethod.FACE_SDK_GROUP_ERROR) {
            message = "SDK_GROUP_ERROR";
        } else if(messageID == FaceMethod.FACE_SDK_DATABASE_ERROR) {
            message = "SDK_DATABASE_ERROR";
        } else if(messageID == FaceMethod.FACE_SDK_SUCCESS) {
            message = "SDK_SUCCESS";
        } else if(messageID == FaceMethod.FACE_SDK_PERSON_ERROR) {
            message = "SDK_PERSON_ERROR";
        } else if(messageID == FaceMethod.FACE_SDK_HWID_ERROR) {
            message = "FACE_SDK_HWID_ERROR";
        } else {
            message = "SDK_UNKNOWN_ERROR";
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Warning").setMessage(message).setPositiveButton(android.R.string.ok, null).show();
    }

    public static String getAppDir(Context context) {
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            return p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }

        return null;
    }

    public static void copyRes(Context context, String path, int resID, String dicFile) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resID);
            byte[] dst = new byte[inputStream.available()];
            inputStream.read(dst);
            inputStream.close();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + dicFile));
            bos.write(dst);
            bos.flush();
            bos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            //Make sure you close all streams.
            fin.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveStringToFile(Context context, String filePath, String str) throws Exception {
        try {
            File fl = new File(filePath);
            FileOutputStream fileout = new FileOutputStream(fl);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(str);
            outputWriter.close();

            new SingleMediaScanner(context, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveDataToFile(Context context, String filePath, byte[] data) throws Exception {
        File fl = new File(filePath);
        FileOutputStream fileout = new FileOutputStream(fl);
        BufferedOutputStream outputWriter=new BufferedOutputStream(fileout);
        outputWriter.write(data);
        outputWriter.close();

        new SingleMediaScanner(context, filePath);
    }

    public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }

    public static
    int max(int a, int b) {
        return (a < b) ? b : a;
    }

    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static Bitmap prepareInputImage(Bitmap bm) {
        int max_size = max(bm.getWidth(), bm.getHeight());
        int min_size = min(bm.getWidth(), bm.getHeight());

        if (max_size <= SDK_MAX_IMAGE_HEIGHT && min_size <= SDK_MIN_IMAGE_WIDTH)
            return bm;

        float max_rate = 1.0f * SDK_MAX_IMAGE_HEIGHT / max_size;
        float min_rate = 1.0f * SDK_MIN_IMAGE_WIDTH / min_size;
        float rate = max_rate < min_rate ? max_rate : min_rate;

        int new_width = (int)(rate * bm.getWidth());
        int new_height = (int)(rate * bm.getHeight());

        return getResizedBitmap(bm, new_width, new_height);
    }

    private static class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient
    {
        private MediaScannerConnection mMs;
        private String mPath;
        SingleMediaScanner(Context context, String f)
        {
            mPath = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }
        @Override
        public void onMediaScannerConnected()
        {
            mMs.scanFile(mPath, null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri)
        {
            mMs.disconnect();
        }
    }
}
