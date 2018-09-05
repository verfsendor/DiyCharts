package com.diy.charts.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.diy.charts.beans.SimpleEntry;
import com.diy.charts.charts.R;
import com.diy.charts.formatter.AxisFormatter;
import com.diy.charts.formatter.SlimChartAxisFormatter;
import com.diy.charts.listener.DetorListener;
import com.diy.charts.utils.GestureDetorManager;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 坐标轴基类
 */
public abstract class BaseAxisChart<T extends SimpleEntry> extends View implements DetorListener {
    protected Context mContext;
    protected AxisFormatter formatter;
    protected GestureDetorManager gestureDetorManager;
    protected ArrayList<T> mData;

    protected TextPaint mTextPaint;//文字画笔
    protected Paint mChartPaint;//表格画笔
    protected Paint defaultPaint;//多用画图
    protected Paint clickPaint;//多用画图

    protected boolean rePicture = true;//重新录制
    protected boolean showAnimation = true;
    protected Picture picture;

    protected int mTextColor = Color.parseColor("#ffffff");
    protected int mChartColor = Color.parseColor("#ffffff");
    protected int mBgcolor = Color.parseColor("#6495ED");
    protected int mClickLinecolor = Color.parseColor("#333333");

    /**
     * 水平留白距离和底部留白距离
     */
    protected int PADDING_LEFT = 50;
    protected final int PADDING_BOTTOM = 200;

    protected float valueWidth ;//每个单位长度的宽度
    protected float valueHeight ;//每个单位长度的高度
    protected float sourcex;//当前坐标原点
    protected float sourcey;//当前坐标原点
    protected float axisWidth = 50;//坐标值标记文字之间的最小距离
    protected float axisHeight = 100;//坐标值标记文字之间的最小距离
    protected float zeroHeight = 0; //纵坐标0的位置偏移量
    protected float animationValue = 1f;

    public BaseAxisChart(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public BaseAxisChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public BaseAxisChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private void getAttrs(AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.BarChart);
            mTextColor = typedArray.getColor(R.styleable.BarChart_BarChart_TextColor, mTextColor);
            mChartColor = typedArray.getColor(R.styleable.BarChart_BarChart_ChartColor, mChartColor);
            typedArray.recycle();
        }
    }

    protected void init(AttributeSet attrs){
        getAttrs(attrs);
        gestureDetorManager = new GestureDetorManager(getContext());
        gestureDetorManager.setDetorListener(this);
        mData = new ArrayList<>();
        initPaint();

    }

    protected void initPaint(){
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
        mChartPaint.setTextSize(25);
        mChartPaint.setStyle(Paint.Style.FILL);

        clickPaint = new Paint();
        clickPaint.setColor(mClickLinecolor);
        clickPaint.setAntiAlias(true);
        clickPaint.setStrokeWidth(1);
        clickPaint.setStyle(Paint.Style.FILL);

        defaultPaint = new Paint();
        defaultPaint.setColor(mChartColor);
        defaultPaint.setAntiAlias(true);
        defaultPaint.setStrokeWidth(3);
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setStrokeCap(Paint.Cap.ROUND);
        defaultPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calPos();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mData == null || mData.size() == 0){
            Rect rect = new Rect(0,getMeasuredHeight()/2, getMeasuredWidth(), getMeasuredHeight()/2 + 100);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText("暂无数据",rect.centerX(),baseline, mTextPaint);
            return;
        }
        if(rePicture){
            recordPicture();
            canvas.drawPicture(picture);
        }else {
            canvas.drawPicture(picture);
            drawclickCanvas(canvas);
            rePicture = true;
        }

    }

    /**
     * 画点击图表后的效果处理
     * @param canvas
     */
    protected void drawclickCanvas(Canvas canvas){

    }

    public void setData(ArrayList<T> data){
        mData = data;
        calPos();
        showStartAnimation();
    }

    public void notifyDataChanged(ArrayList<T> data){
        mData = data;
        calPos();
        invalidate();
    }

    /**
     * 画数据
     * 根据不同的锚点，根据缩放比例，对坐标系进行缩放，绘制折线图，为了避免缩放时文字显示以及图表失真，不使用画布
     * 进行缩放，改为坐标系尺度缩放并重绘。
     * @param canvas
     */
    protected abstract void drawDataVlaus(Canvas canvas);
    /**
     * 是否显示图表上的文字
     * @return
     */
    public abstract boolean drawValueNumber();
    /**
     * 用X轴显示值还是Y轴显示值
     * @return
     */
    public abstract boolean isYshowValue();

    public abstract void drawXAxisTxt(Canvas canvas, int value, float x);
    public abstract void drawYAxisTxt(Canvas canvas, int value, float y);

    /**
     * 画坐标轴刻度
     * @param canvas
     */
    private void drawAxis(Canvas canvas){
        //用Y轴展示数据值
        if(isYshowValue()){
            float valuey = 0;
            for(float y = sourcey; y > PADDING_LEFT; y-=axisHeight){
                if(y < getMeasuredHeight() - PADDING_BOTTOM){
                    String txt = "";
                    valuey = (sourcey - y)/valueHeight;
                    if(axisHeight/valueHeight < 1) {
                        BigDecimal b = new BigDecimal(valuey);
                        txt = "" + b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    }else {
                        txt = "" + (int)valuey;
                    }
                    mChartPaint.setStrokeWidth(0.5f);
                    canvas.drawLine(PADDING_LEFT, y, getMeasuredWidth() - PADDING_LEFT, y, mChartPaint);
                    mChartPaint.setStrokeWidth(3);
                    canvas.drawText(txt,PADDING_LEFT - mChartPaint.measureText(txt) - 10, y,mChartPaint);
                }
            }
            int valuex = 0;
            for(float x = sourcex; x < getMeasuredWidth() - PADDING_LEFT; x += valueWidth){
                if(x >= PADDING_LEFT) {
                    valuex = (int) ((x - sourcex) / valueWidth) + 1;
                    drawXAxisTxt(canvas,valuex, x);
                }
            }
        }else {//用x轴展示数据值
            float valuex = 0;
            for(float x = sourcex; x < getMeasuredWidth() - PADDING_LEFT; x+=axisHeight){
                if(x >= PADDING_LEFT){
                    String txt = "";
                    valuex = (x - sourcex)/valueWidth;
                    if(axisWidth/valueWidth < 1) {
                        BigDecimal b = new BigDecimal(valuex);
                        txt = "" + b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    }else {
                        txt = "" + (int)valuex;
                    }
                    mChartPaint.setStrokeWidth(0.5f);
                    canvas.drawLine(x,getMeasuredHeight() - PADDING_BOTTOM,  x, PADDING_LEFT, mChartPaint);
                    mChartPaint.setStrokeWidth(3);
                    canvas.drawText(txt,x - mChartPaint.measureText(txt)/2,getMeasuredHeight() - PADDING_BOTTOM + 35 , mChartPaint);
                }
            }
            int valuey = 0;
            for(float y = sourcey; y > PADDING_LEFT; y -= valueHeight){
                if(y <= getMeasuredHeight() - PADDING_BOTTOM) {
                    valuey = (int) ((sourcey - y) / valueHeight) + 1;
                    drawYAxisTxt(canvas,valuey,y);
                }
            }
        }
    }

    protected abstract boolean isdrawBottomTxt();

    protected abstract void drawBottomTxt(Canvas canvas);
    /**
     * 画坐标轴，图例和助记
     * @param canvas
     */
    private void drawBottom(Canvas canvas){
        if(isdrawBottomTxt()) {
            defaultPaint.setColor(mBgcolor);
            defaultPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, PADDING_LEFT, getMeasuredHeight(), defaultPaint);
            canvas.drawRect(0, getMeasuredHeight() - PADDING_BOTTOM, getMeasuredWidth(), getMeasuredHeight(), defaultPaint);
            canvas.drawLine(PADDING_LEFT, PADDING_LEFT, PADDING_LEFT, getMeasuredHeight() - PADDING_BOTTOM, mChartPaint);
            canvas.drawLine(PADDING_LEFT, getMeasuredHeight() - PADDING_BOTTOM,
                    getMeasuredWidth() - PADDING_LEFT, getMeasuredHeight() - PADDING_BOTTOM, mChartPaint);
            drawBottomTxt(canvas);
        }
    }



    public void setFormatter(AxisFormatter formatter){
        this.formatter = formatter;
    }

    public AxisFormatter getFormatter(){
        if(formatter == null){
            return new SlimChartAxisFormatter();
        }
        return formatter;
    }


    @Override
    public void refreshView() {
        calPos();
        invalidate();
    }

    @Override
    public void onSingleTap(MotionEvent event) {
    }

    /**
     * 在ontouEvent中托管MotionEvent给手势监听器
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetorManager.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void calPos(){
        if(isYshowValue()) {
            valueWidth = (getMeasuredWidth() - PADDING_LEFT - PADDING_LEFT) / getMaxSize();
            valueHeight = (getMeasuredHeight() - PADDING_BOTTOM - PADDING_LEFT - zeroHeight) / (getMaxValue() * 1.1f);
        }else {
            valueWidth = (getMeasuredWidth() - PADDING_LEFT - PADDING_LEFT) / (getMaxValue() * 1.1f);
            valueHeight = (getMeasuredHeight() - PADDING_BOTTOM - PADDING_LEFT - zeroHeight) / getMaxSize();
            zeroHeight = 0;
        }
        sourcex = PADDING_LEFT + gestureDetorManager.scrollDistanceX;
        sourcey = getMeasuredHeight() - PADDING_BOTTOM - zeroHeight + gestureDetorManager.scrollDistanceY;
        sourcex = sourcex + (1 - gestureDetorManager.scaleValueX) * (gestureDetorManager.focusX - sourcex);
        sourcey = sourcey - (1 - gestureDetorManager.scaleValueY) * (sourcey - gestureDetorManager.focusY);
        if(sourcex > PADDING_LEFT){
            sourcex = PADDING_LEFT;
        }
        if(sourcey < getMeasuredHeight() - PADDING_BOTTOM - zeroHeight){
            sourcey = getMeasuredHeight() - PADDING_BOTTOM- zeroHeight;
        }
        valueWidth = valueWidth * gestureDetorManager.scaleValueX;
        valueHeight = valueHeight * gestureDetorManager.scaleValueY;
    }

    /**
     * 负责第一次设置数据时的显示动画
     */
    private void showStartAnimation(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1)
                .setDuration(1000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animationValue = (float) animation.getAnimatedValue();
                invalidate();
            }

        });
        valueAnimator.start();
    }


    /**
     * 将固定的表盘部分绘制在picture上进行复用
     */
    private void recordPicture(){
        picture = new Picture();
        Canvas canvas = picture.beginRecording(getMeasuredWidth(), getMeasuredHeight());
        canvas.drawColor(mBgcolor);
        drawDataVlaus(canvas);
        drawBottom(canvas);
        drawAxis(canvas);
        picture.endRecording();
    }

    public abstract float getMaxValue();

    public abstract float getMaxSize();
}
