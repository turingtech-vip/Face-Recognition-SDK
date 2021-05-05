package com.turingtech.idverify;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by demid on 2/28/2017.
 */

public class ResultView extends View {
    private static final String TAG = "ResultView";

    private Rect[] mRect = null;
    private int mnFaceNum = 0;
    private int mImgWidth = 100;
    private int mImgHeight = 100;
    private Paint mPaint;

    public ResultView(Context context) {
        super(context);
        init();
    }

    public ResultView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public ResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // create the Paint and set its color
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
    }

    public void setResult(int imgWidth, int imgHeight, Rect[] faceRect, int nFaceNum) {
        mImgWidth = imgWidth;
        mImgHeight = imgHeight;
        mRect = faceRect;
        mnFaceNum = nFaceNum;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mRect == null)
            return;

        try {
            if(null != mRect && mRect.length > 0 && mnFaceNum > 0){
                int nFaceIdx = 0;
                for (Rect currRect : mRect) {

                    Rect frameRect = getFrameRect(new Rect(0, 0, getWidth(), getHeight()), mImgWidth, mImgHeight, currRect);
                    canvas.drawRect(frameRect, mPaint);

                    if (++ nFaceIdx >= mnFaceNum)
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Rect getFrameRect(Rect boundingRect, int imageWidth, int imageHeight, Rect faceRect)
    {
        double imageScale = imageWidth / (double)imageHeight;
        double boudingScale = boundingRect.width() / (double)boundingRect.height();

        if (imageScale > boudingScale)
        {
            int top = boundingRect.top + (int)(boundingRect.height() - boundingRect.width() / imageScale) / 2;
            int left = boundingRect.left;

            int width = boundingRect.width();
            int height = (int)(boundingRect.width() / imageScale);

            double rectScaleX = width / (double)imageWidth;
            double rectScaleY = height / (double)imageHeight;

            return new Rect(left + (int)(faceRect.left * rectScaleX), top + (int)(faceRect.top * rectScaleY),
                    left + (int)(faceRect.left * rectScaleX) + (int)(faceRect.width() * rectScaleX),
                    top + (int)(faceRect.top * rectScaleY) + (int)(faceRect.height() * rectScaleY));
        }
        else
        {
            int top = boundingRect.top;
            int left = boundingRect.left + (int)(boundingRect.width() - boundingRect.height() * imageScale) / 2;

            int width = (int)(boundingRect.height() * imageScale);
            int height = boundingRect.height();

            double rectScaleX = width / (double)imageWidth;
            double rectScaleY = height / (double)imageHeight;

            return new Rect(left + (int)(faceRect.left * rectScaleX), top + (int)(faceRect.top * rectScaleY),
                    left + (int)(faceRect.left * rectScaleX) + (int)(faceRect.width() * rectScaleX),
                    top + (int)(faceRect.top * rectScaleY) + (int)(faceRect.height() * rectScaleY));
        }
    }
}
