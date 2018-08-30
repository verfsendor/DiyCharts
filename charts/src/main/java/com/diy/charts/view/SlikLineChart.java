package com.diy.charts.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.diy.charts.beans.SlikLineChartBean;
import com.diy.charts.detector.DirectionGestureDector;
import com.diy.charts.detector.DirectionGestureDectorListenr;
import com.diy.charts.detector.GestureData;

import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 雷达图
 */

public class SlikLineChart extends View{
    private Context mContext;
    private TextPaint mTextPaint;//文字画笔
    private Paint mChartPaint;//表格画笔
    private Paint defaultPaint;//表格画笔

    /**
     * 手势监听器
     */
    private DirectionGestureDector mDirectionGestureDector;
    private GestureDetector mGesturDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private int mTextColor = Color.parseColor("#ffffff");
    private int mChartColor = Color.parseColor("#ffffff");

    /**
     * 刻度线间距以及chart到view边缘的留白距离
     */
    private final int LINE_WIDTH = 80;
    private final int CHART_PADDING_HORIZONTAL = 50;
    private final int CHART_PADDING_BOTTOM = 200;

    /**
     * 缩放中心以及缩放比例
     */
    private float focusX;
    private float focusY;
    private float scaleValueX = 1f;
    private float scaleValueY = 1f;
    private float mscale = 1f;
    /**
     * 平移x y 距离
     */
    private float scrollDistanceX;
    private float scrollDistanceY;

    private ArrayList<SlikLineChartBean> mData;

    public SlikLineChart(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public SlikLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public SlikLineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private void getAttrs(AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, com.diy.charts.charts.R.styleable.AttributeChart);
            mTextColor = typedArray.getColor(com.diy.charts.charts.R.styleable.AttributeChart_Attribute_TextColor, mTextColor);
            mChartColor = typedArray.getColor(com.diy.charts.charts.R.styleable.AttributeChart_Attribute_ChartColor, mChartColor);
            typedArray.recycle();
        }
    }

    private void init(AttributeSet attrs){
        getAttrs(attrs);
        mData = new ArrayList<>();
        initPaint();
        mDirectionGestureDector = new DirectionGestureDector(new DirectionGestureDectorListenr() {
            @Override
            public boolean onScale(GestureData detector) {
                focusX = detector.getFousX();
                focusY = detector.getFousY();
                scaleValueX = scaleValueX * detector.getScaleX();
                scaleValueY = scaleValueY * detector.getScaleY();
                Log.v("verf","mDirectionGestureDector  " + " scaleValueX= " + scaleValueX + " dectorX= " + detector.getScaleX() );
                Log.v("verf","mDirectionGestureDector  " + " scaleValueY= " + scaleValueY + " dectorY= " + detector.getScaleY());
                scaleValueY = scaleValueY < 1 ? 1: scaleValueY;
                scaleValueX = scaleValueX < 1 ? 1: scaleValueX;
                invalidate();
                return true;
            }
        });
        mScaleGestureDetector =  new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mscale = mscale * detector.getScaleFactor();
                Log.v("aaaa","mScaleGestureDetector scaleValue " + detector.getScaleFactor() + " Mscale " + mscale + " fousxy " + detector.getFocusX() + " " + detector.getFocusY());
                //当缩放比例接近原大小时，画布平移回原点
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });
        mGesturDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                scrollDistanceX = scrollDistanceX + distanceX;
                scrollDistanceY = scrollDistanceY + distanceY;
                invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    private void initPaint(){
        mTextPaint = new TextPaint();
        mTextPaint.setStrokeWidth(2f);
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);

        mChartPaint = new Paint();
        mChartPaint.setColor(mChartColor);
        mChartPaint.setAntiAlias(true);
        mChartPaint.setStrokeWidth(3);
        mChartPaint.setStyle(Paint.Style.FILL);

        defaultPaint = new Paint();
        defaultPaint.setColor(mChartColor);
        defaultPaint.setAntiAlias(true);
        defaultPaint.setStrokeWidth(3);
        defaultPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 没有数据时进行文字提示"暂无数据"
         */
        if(mData == null || mData.size() == 0){
            Rect rect = new Rect(0,getMeasuredHeight()/2, getMeasuredWidth(), getMeasuredHeight()/2 + 100);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText("暂无数据",rect.centerX(),baseline, mTextPaint);
            return;
        }
        drawBottomTxt(canvas);
        canvas.drawLine(CHART_PADDING_HORIZONTAL,CHART_PADDING_HORIZONTAL, CHART_PADDING_HORIZONTAL, getMeasuredHeight() - CHART_PADDING_BOTTOM, mChartPaint);
        /**
         * 处理缩放和平移操作，根据手势监听器的值对画布进行缩放和平移
         */
        canvas.scale(scaleValueX,1,focusX,focusY);
        canvas.drawLine(CHART_PADDING_HORIZONTAL,getMeasuredHeight() - CHART_PADDING_BOTTOM,
                getMeasuredWidth() - CHART_PADDING_HORIZONTAL, getMeasuredHeight() - CHART_PADDING_BOTTOM,mChartPaint);
        canvas.drawText("skgjksgdgdg",CHART_PADDING_HORIZONTAL + 200,getMeasuredHeight() - CHART_PADDING_BOTTOM,  mTextPaint);
        canvas.scale(scaleValueX,scaleValueX,focusX,focusY);
//        canvas.translate(0,-scrollDistanceY);
    }



    /**
     * 在ontouEvent中托管MotionEvent给手势监听器
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDirectionGestureDector.onTouchEvent(event);
//        mGesturDetector.onTouchEvent(event);
//        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setData(ArrayList<SlikLineChartBean> data){
        this.mData = data;
        invalidate();
    }

    /**
     * 画图例和助记
     * @param canvas
     */
    public void drawBottomTxt(Canvas canvas){
        float  startx = CHART_PADDING_HORIZONTAL;
        float  starty = getMeasuredHeight() - CHART_PADDING_BOTTOM + 80;
        float  txtHeight = mTextPaint.measureText("图");
        for(int i = 0; i < mData.size(); i ++){
            if(mData.get(i).getLinecolor() != 0) {
                defaultPaint.setColor(mData.get(i).getLinecolor());
            }
            float width = mTextPaint.measureText(mData.get(i).getName());
            canvas.drawText( mData.get(i).getName(),startx ,starty + txtHeight,mTextPaint);
            startx = startx + width + 20;
            canvas.drawRect(startx, starty, startx + 50 ,starty + txtHeight,defaultPaint);
            startx = startx + 50 + 30;
        }
    }
}
