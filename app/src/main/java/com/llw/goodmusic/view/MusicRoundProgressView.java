package com.llw.goodmusic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.llw.goodmusic.R;

/**
 * 圆形进度条
 *
 * @author llw
 */
public class MusicRoundProgressView extends View {

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 画笔颜色
     */
    private int mPaintColor;

    /**
     * 半径
     */
    private float mRadius;

    /**
     * 圆环半径
     */
    private float mRingRadius;

    /**
     * 圆环宽度
     */
    private float mStrokeWidth;

    /**
     * 圆心 X 轴坐标
     */
    private int mCenterX;

    /**
     * 圆心 Y 轴坐标
     */
    private int mCenterY;

    /**
     * 总进度
     */
    private int mTotalProgress;

    /**
     * 当前进度
     */
    private int mProgress;


    public MusicRoundProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressView);
        //半径
        mRadius = typedArray.getDimension(R.styleable.RoundProgressView_radius, 40);
        //宽度
        mStrokeWidth = typedArray.getDimension(R.styleable.RoundProgressView_strokeWidth, 5);
        //颜色
        mPaintColor = typedArray.getColor(R.styleable.RoundProgressView_strokeColor, 0xFFFFFFFF);
        //圆环半径 = 半径 + 圆环宽度的1/2
        mRingRadius = mRadius + mStrokeWidth / 2;

        //画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;

        if (mProgress > 0) {
            RectF rectF = new RectF();
            rectF.left = (mCenterX - mRingRadius);
            rectF.top = (mCenterY - mRingRadius);
            rectF.right = mRingRadius * 2 + (mCenterX - mRingRadius);
            rectF.bottom = mRingRadius * 2 + (mCenterY - mRingRadius);
            canvas.drawArc(rectF, -90, ((float) mProgress / mTotalProgress) * 360, false, mPaint);
        }
    }

    /**
     * 设置进度
     */
    public void setProgress(int progress, int totalProgress) {

        mProgress = progress;
        mTotalProgress = totalProgress;
        //重绘
        postInvalidate();
    }
}
