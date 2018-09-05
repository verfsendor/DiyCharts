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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.diy.charts.beans.BarChartBean;
import com.diy.charts.charts.R;
import com.diy.charts.formatter.AxisFormatter;
import com.diy.charts.formatter.SlimChartAxisFormatter;
import com.diy.charts.listener.DetorListener;
import com.diy.charts.listener.OnBarChartItemClickListener;
import com.diy.charts.utils.GestureDetorManager;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 顺滑折线图
 */
public class BarChart extends View implements DetorListener {
    private Context mContext;
    private AxisFormatter formatter;
    private GestureDetorManager gestureDetorManager;
    private ArrayList<BarChartBean> mData;
    private TextPaint mTextPaint;//文字画笔
    private Paint mChartPaint;//表格画笔
    private Paint defaultPaint;//多用画图
    private Paint clickPaint;//多用画图
    private boolean drawNum = true;
    private OnBarChartItemClickListener listener;
    private boolean rePicture = true;//重新录制
    private Picture picture;

    private int mTextColor = Color.parseColor("#ffffff");
    private int mChartColor = Color.parseColor("#ffffff");
    private int mBgcolor = Color.parseColor("#6495ED");
    private int mClickLinecolor = Color.parseColor("#333333");

    /**
     * 水平留白距离和底部留白距离
     */
    private  int PADDING_LEFT = 50;
    private  int PADDING_BOTTOM = 100;

    float valueWidth ;//每个单位长度的宽度
    float valueHeight ;//每个单位长度的高度
    float sourcex;//当前坐标原点
    float sourcey;//当前坐标原点
    float axisWidth = 50;//坐标值标记文字之间的最小距离
    float axisHeight = 100;//坐标值标记文字之间的最小距离
    float zeroHeight = 0; //纵坐标0的位置偏移量
    float animationValue = 1f;
    private int clickPosition = -1;

    public BarChart(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    private void init(AttributeSet attrs){
        getAttrs(attrs);
        gestureDetorManager = new GestureDetorManager(getContext());
        gestureDetorManager.setDetorListener(this);
        mData = new ArrayList<>();
        initPaint();

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
        canvas.drawColor(mBgcolor);
        if(mData == null || mData.size() == 0){
            Rect rect = new Rect(0,getMeasuredHeight()/2, getMeasuredWidth(), getMeasuredHeight()/2 + 100);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText("暂无数据",rect.centerX(),baseline, mTextPaint);
            return;
        }
//        if(rePicture){
            recordPicture();
            canvas.drawPicture(picture);
//        }else {
//            canvas.drawPicture(picture);
//            if(slikLineChartPoint != null){
//                canvas.drawLine(slikLineChartPoint.getX(), PADDING_LEFT, slikLineChartPoint.getX(), slikLineChartPoint.getY() - slikChartAdapter.mData.get(clickPosition1).getCircleRadius(), clickPaint);
//                canvas.drawLine(slikLineChartPoint.getX(), slikLineChartPoint.getY() + slikChartAdapter.mData.get(clickPosition1).getCircleRadius(),slikLineChartPoint.getX(), getMeasuredHeight() - PADDING_BOTTOM, clickPaint);
//                canvas.drawLine(PADDING_LEFT, slikLineChartPoint.getY(), slikLineChartPoint.getX() - slikChartAdapter.mData.get(clickPosition1).getCircleRadius(),slikLineChartPoint.getY(),clickPaint);
//                canvas.drawLine(slikLineChartPoint.getX()  + slikChartAdapter.mData.get(clickPosition1).getCircleRadius(),slikLineChartPoint.getY(),getMeasuredWidth() - PADDING_LEFT,slikLineChartPoint.getY(),clickPaint);
//
//            }
//            rePicture = true;
//        }

    }

    public void setData(ArrayList<BarChartBean> data){
        mData = data;
        calPos();
        showStartAnimation();
    }

    public void notifyDataChanged(ArrayList<BarChartBean> data){
        mData = data;
        calPos();
        invalidate();
    }

    /**
     * 画条形图
     * 根据不同的锚点，根据缩放比例，对坐标系进行缩放，绘制折线图，为了避免缩放时文字显示以及图表失真，不使用画布
     * 进行缩放，改为坐标系尺度缩放并重绘。
     * @param canvas
     */
    private void drawLines(Canvas canvas){
        defaultPaint.setStyle(Paint.Style.FILL);
        for(int i = 0; i < mData.size(); i ++){
            defaultPaint.setColor(mData.get(i).getColor());
            float x = sourcex + valueWidth * i;
            float y =  animationValue * (sourcey - valueHeight * mData.get(i).getValue());
            float y0 = getMeasuredHeight() - PADDING_BOTTOM - zeroHeight; //0刻度的纵坐标
            y = y0 - animationValue * (valueHeight * mData.get(i).getValue() - (sourcey - y0));
            mData.get(i).setXY(x,y);
            if(i == clickPosition){
                defaultPaint.setColor(Color.parseColor("#000000"));
            }
            canvas.drawRect(x,y  + (1- animationValue)*(y0 - y),x + valueWidth, y0,defaultPaint);
            defaultPaint.setAlpha(100);
            if(drawNum){
                float txtwidth = mChartPaint.measureText("" + mData.get(i).getValue());
                canvas.drawText("" + mData.get(i).getValue(), x + valueWidth/2 - txtwidth/2 , y  + (1- animationValue)*(y0 - y) - 15, mChartPaint);
            }
        }
    }

    /**
     * 画坐标轴刻度
     * @param canvas
     */
    private void drawAxis(Canvas canvas){
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
                if (valuex - 1 < mData.size()) {
                    float valuetxtWidth = mChartPaint.measureText(mData.get(valuex - 1).getName());
                    float valuetxtheight = mChartPaint.measureText("X");
                    canvas.drawText(mData.get(valuex - 1).getName(), x + valueWidth / 2 - valuetxtWidth / 2, getMeasuredHeight() - PADDING_BOTTOM + valuetxtheight * 2, mChartPaint);
                }
            }
        }
    }

    /**
     * 画坐标轴，图例和助记
     * @param canvas
     */
    private void drawBottomTxt(Canvas canvas){
        defaultPaint.setColor(mBgcolor);
        defaultPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,PADDING_LEFT, getMeasuredHeight(),defaultPaint);
        canvas.drawRect(0,getMeasuredHeight() - PADDING_BOTTOM,getMeasuredWidth(), getMeasuredHeight(),defaultPaint);
        canvas.drawLine(PADDING_LEFT,PADDING_LEFT, PADDING_LEFT, getMeasuredHeight() - PADDING_BOTTOM, mChartPaint);
        canvas.drawLine(PADDING_LEFT,getMeasuredHeight() - PADDING_BOTTOM,
                getMeasuredWidth() - PADDING_LEFT, getMeasuredHeight() - PADDING_BOTTOM,mChartPaint);

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
        Log.v("verf","onSingleTap");
        treatClickEvent(event);
    }

    /**
     * 在ontouEvent中托管MotionEvent给手势监听器
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetorManager.onTouchEvent(event);
        if(gestureDetorManager.scaleValueX == 1){
//            treatClickEvent(event);
        }
        return true;
    }



    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void calPos(){
        if(mData == null || mData.size() <= 0){
            return;
        }
        valueWidth = (getMeasuredWidth() - PADDING_LEFT - PADDING_LEFT)/ mData.size();
        valueHeight = (getMeasuredHeight() - PADDING_BOTTOM - PADDING_LEFT - zeroHeight)/(getMaxValue() * 1.1f);
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
        drawLines(canvas);
        drawBottomTxt(canvas);
        drawAxis(canvas);
        picture.endRecording();
    }

    public float getMaxValue(){
        float result = 0;
        for(int i = 0; i < mData.size(); i ++){
            if(mData.get(i).getValue() > result){
                result = mData.get(i).getValue();
            }
        }
        return result;
    }

    public void setOnBarChartItemClickListener(OnBarChartItemClickListener listener){
        this.listener = listener;
    }

    //处理点击事件
    public void treatClickEvent(MotionEvent e){
        Log.v("verf","treatClickEvent");
        clickPosition = -1;
        for(int i = 0; i < mData.size(); i ++){
            Log.v("verf","click data " + i + " "  + mData.get(i).getX() + " " + e.getX());
            if(e.getX() <= mData.get(i).getX() + valueWidth && e.getX() > mData.get(i).getX()){
                Log.v("verf","click data xxxxxxxxxxxxxxx");
                clickPosition = i;
                if(listener != null){
                    listener.onChartItemClick(i);
                }
            }
        }
        invalidate();
    }
}
