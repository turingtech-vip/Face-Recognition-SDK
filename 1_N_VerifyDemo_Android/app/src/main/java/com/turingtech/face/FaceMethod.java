package com.turingtech.face;

import android.graphics.Bitmap;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by demid on 2/23/2017.
 */

public class FaceMethod extends ContextWrapper{

    public static final int FACE_SDK_SUCCESS = 0;
    public static final int FACE_SDK_VERIFY_SUCCESS = 0;
    public static final int FACE_SDK_ACTIVATION_SERIAL_UNKNOWN = 1;
    public static final int FACE_SDK_ACTIVATION_EXPIRED = 2;
    public static final int FACE_SDK_NOT_INITIALIZED = 3;
    public static final int FACE_SDK_ALREADY_INITIALIZED = 4;
    public static final int FACE_SDK_NO_RES_FILE = 5;
    public static final int FACE_SDK_NOT_FACE_DETECTED = 6;
    public static final int FACE_SDK_NOT_ENROLLED = 7;
    public static final int FACE_SDK_BAD_PARAMETER = 8;
    public static final int FACE_SDK_IMG_ERROR = 9;
    public static final int FACE_SDK_VERIFY_FAILED = 10;
    public static final int FACE_SDK_GROUP_ERROR = 11;
    public static final int FACE_SDK_DATABASE_ERROR = 12;
    public static final int FACE_SDK_PERSON_ERROR = 13;
    public static final int FACE_SDK_HWID_ERROR = 19;


    public FaceMethod(Context context) {
        super(context);
    }

    public native String        getCurrentHWID();
    public native int           setActivation(String license);

    public static native int    initializeSDK(String dictPath, String dbPath);
    public static native int    getSDKParam(float[] version, float[] defaultThr);
    public static native int    finalizeSDK();
    public static native int    addGroup(int groupID);
    public static native int    deleteGroup(int groupID);
    public static native int    deletePerson(int personID);
    public static native int    enroll(int groupID, int personID, Bitmap sceneImg);
    public static native int    identify(int groupID, float threshold, Bitmap sceneImg, int candNum, int[] findIds, float[] scores, int[] resultCount, int[] otherResults);
    public static native int    verify(int personID, float threshold, Bitmap sceneImg, float[] score, int[] otherResults);
    public static native int    detectFace(Bitmap sceneImg, int[] faceResults, float[] qualities);
    public static native int    detectMultiFace(Bitmap sceneImg, int iMaxCount, int[] faceResults, int[] FaceCount);
    public static native int    detectFaceWithQuality(Bitmap sceneImg, float[] faceResults, int[] FaceState);
    public static native byte[] getFeature(Bitmap sceneImg, int[] faceResults);
    public static native int    getGroupCount();
    public static native int    getPersonCount(int groupID);
    public static native int    getFeatureSize();
    public static native int[]  getGroupIDs();
    public static native int[]  getPersonIDs(int groupID);
    public static native byte[] getPersonFeats(int personID);
    public static native int    enrollWithFeature(int groupID, int personID, byte[] feats);
    public static native int    getFeatCount(int personID);

    static {
        System.loadLibrary("face-jni");
    }
}
