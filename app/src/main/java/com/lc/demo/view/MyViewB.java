package com.lc.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MyViewB extends View {

    private final String TAG = getClass().getSimpleName();

    private String label = getClass().getSimpleName();

    private Paint mPaint = new Paint();

    public MyViewB(Context context) {
        this(context,null);
    }

    public MyViewB(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyViewB(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setTextSize(40f);
        mPaint.setColor(Color.BLUE);
    }


    public void setLabel(String info){
        label = info;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        SpecUtils.parseSpec(TAG,widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG,"onLayout changed:"+changed+",l"+left+""+top+""+right+""+bottom);
        //layout(left,top,right,bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG,"onDraw:"+canvas);
        canvas.drawText(label,0,getMeasuredHeight()  / 2,mPaint);
    }
}
