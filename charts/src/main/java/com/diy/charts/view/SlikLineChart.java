package com.diy.charts.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.diy.charts.adapter.SlikChartAdapter;
import com.diy.charts.beans.PointBean;
import com.diy.charts.beans.SlikLineChartBean;
import com.diy.charts.beans.SlikLineChartPoint;
import com.diy.charts.charts.R;
import com.diy.charts.formatter.AxisFormatter;
import com.diy.charts.formatter.SlimChartAxisFormatter;
import com.diy.charts.listener.DetorListener;
import com.diy.charts.listener.OnSlikLineChartItemClickListener;
import com.diy.charts.utils.GestureDetorManager;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 顺滑折线图
 */
public class SlikLineChart extends View implements DetorListener {
    private Context mContext;
    private AxisFormatter formatter;
    private GestureDetorManager gestureDetorManager;
    private SlikChartAdapter slikChartAdapter;
    private OnSlikLineChartItemClickListener chartItemClickListener;
    private TextPaint mTextPaint;//文字画笔
    private Paint mChartPaint;//表格画笔
    private Paint defaultPaint;//多用画图
    private Paint clickPaint;//多用画图

    private boolean rePicture = true;//重新录制
    private Picture picture;

    private int mTextColor = Color.parseColor("#ffffff");
    private int mChartColor = Color.parseColor("#ffffff");
    private int mBgcolor = Color.parseColor("#6495ED");
    private int mClickLinecolor = Color.parseColor("#333333");

    /**
     * 水平留白距离和底部留白距离
     */
    private int PADDING_LEFT = 50;
    private int paddingBottom = 120;

    float valueWidth ;//每个单位长度的宽度
    float valueHeight ;//每个单位长度的高度
    float sourcex;//当前坐标原点
    float sourcey;//当前坐标原点
    float axisWidth = 100;//坐标值标记文字之间的最小距离
    float axisHeight = 100;//坐标值标记文字之间的最小距离
    float zeroHeight = 100; //纵坐标0的位置偏移量
    float animationValue = 1f;
    private SlikLineChartPoint slikLineChartPoint;
    private int clickPosition1;
    private int clickPosition2;

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
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.SlikLineChart);
            mTextColor = typedArray.getColor(R.styleable.SlikLineChart_SlikLineChart_TextColor, mTextColor);
            mChartColor = typedArray.getColor(R.styleable.SlikLineChart_SlikLineChart_ChartColor, mChartColor);
            paddingBottom = typedArray.getColor(R.styleable.SlikLineChart_SlikLineChart_ChartColor, paddingBottom);
            typedArray.recycle();
        }
    }

    private void init(AttributeSet attrs){
        getAttrs(attrs);
        gestureDetorManager = new GestureDetorManager(getContext());
        gestureDetorManager.setDetorListener(this);
        slikChartAdapter = new SlikChartAdapter();
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
        if(slikChartAdapter.isEmpty()){
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
            if(slikLineChartPoint != null){
                canvas.drawLine(slikLineChartPoint.getX(), PADDING_LEFT, slikLineChartPoint.getX(), slikLineChartPoint.getY() - slikChartAdapter.mData.get(clickPosition1).getCircleRadius(), clickPaint);
                canvas.drawLine(slikLineChartPoint.getX(), slikLineChartPoint.getY() + slikChartAdapter.mData.get(clickPosition1).getCircleRadius(),slikLineChartPoint.getX(), getMeasuredHeight() - paddingBottom, clickPaint);
                canvas.drawLine(PADDING_LEFT, slikLineChartPoint.getY(), slikLineChartPoint.getX() - slikChartAdapter.mData.get(clickPosition1).getCircleRadius(),slikLineChartPoint.getY(),clickPaint);
                canvas.drawLine(slikLineChartPoint.getX()  + slikChartAdapter.mData.get(clickPosition1).getCircleRadius(),slikLineChartPoint.getY(),getMeasuredWidth() - PADDING_LEFT,slikLineChartPoint.getY(),clickPaint);

            }
            rePicture = true;
        }

    }

    public void setData(ArrayList<SlikLineChartBean> data){
        slikChartAdapter.setData(data);
        Log.v("verf","data size " + data.size());
        calPos();
        showStartAnimation();
    }

    public void notifyDataChanged(ArrayList<SlikLineChartBean> data){
        slikChartAdapter.setData(data);
        calPos();
        invalidate();
    }

    /**
     * 根据不同的锚点，根据缩放比例，对坐标系进行缩放，绘制折线图，为了避免缩放时文字显示以及图表失真，不使用画布
     * 进行缩放，改为坐标系尺度缩放并重绘。
     * @param canvas
     */
    private void drawLines(Canvas canvas){
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setStrokeJoin(Paint.Join.ROUND);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(10);
        defaultPaint.setPathEffect(cornerPathEffect);
        for(int i = 0; i < slikChartAdapter.mData.size(); i ++){
           SlikLineChartBean bean = slikChartAdapter.mData.get(i);
           Path path = new Path();
           path.moveTo(sourcex, sourcey);
           ArrayList<PointBean> points = new ArrayList<>();
           for(int j = 0; j < bean.getData().size(); j ++){
               float x = sourcex + valueWidth * (j + 1);
               float y =  animationValue * (sourcey - valueHeight * bean.getData().get(j).getValue());
               float y0 = getMeasuredHeight() - paddingBottom - zeroHeight; //0刻度的纵坐标
               y = y0 - animationValue * (valueHeight * bean.getData().get(j).getValue() - (sourcey - y0));
               path.lineTo(x,y);
               bean.getData().get(j).setXY(x,y);
               points.add(new PointBean(x,y,"" + (int)bean.getData().get(j).getValue()));
               defaultPaint.setColor(bean.getCirclecolor());
           }
           defaultPaint.setColor(bean.getLinecolor());
           canvas.drawPath(path,defaultPaint);
            if(bean.isShowCircle()){
                for(int k = 0; k < points.size(); k ++){
                    defaultPaint.setStyle(Paint.Style.FILL);
                    defaultPaint.setColor(mBgcolor);
                    canvas.drawCircle(points.get(k).getX(),points.get(k).getY(),bean.getCircleRadius(),defaultPaint);
                    defaultPaint.setStyle(Paint.Style.STROKE);
                    defaultPaint.setColor(bean.getCirclecolor());
                    defaultPaint.setStrokeWidth(2);
                    if(bean.isShowNum()) {
                        defaultPaint.setTextSize(bean.getNumTextsize());
                        float txtWidth = defaultPaint.measureText(points.get(k).getDesc());
                        defaultPaint.setColor(Color.WHITE);
                        defaultPaint.setStrokeWidth(5);
                        defaultPaint.setAntiAlias(true);
                        defaultPaint.setStyle(Paint.Style.FILL);
                        canvas.drawText(points.get(k).getDesc(), points.get(k).getX() - txtWidth/2, points.get(k).getY() - 15, defaultPaint);
                        defaultPaint.setTextSize(20);
                    }
                    canvas.drawCircle(points.get(k).getX(),points.get(k).getY(),bean.getCircleRadius(),defaultPaint);
                    defaultPaint.setStrokeWidth(3);
                    defaultPaint.setColor(bean.getLinecolor());
                }
            }
       }
    }

    /**
     * 画坐标轴刻度
     * @param canvas
     */
    private void drawAxis(Canvas canvas) {
        float valuey = 0;
        for (float y = sourcey; y > PADDING_LEFT; y -= axisHeight) {
            if (y < getMeasuredHeight() - paddingBottom) {
                String txt = "";
                valuey = (sourcey - y) / valueHeight;
                if (axisHeight / valueHeight < 1) {
                    BigDecimal b = new BigDecimal(valuey);
                    txt = "" + b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                } else {
                    txt = "" + (int) valuey;
                }
                if (mChartPaint.measureText(txt) > PADDING_LEFT) {
                    PADDING_LEFT = 70;
                }
                mChartPaint.setStrokeWidth(0.5f);
                canvas.drawLine(PADDING_LEFT, y, getMeasuredWidth() - PADDING_LEFT, y, mChartPaint);
                mChartPaint.setStrokeWidth(3);
                canvas.drawText(txt, PADDING_LEFT - mChartPaint.measureText(txt) - 10, y, mChartPaint);
            }
        }
        int valuex = 0;
        for (float x = sourcex; x < getMeasuredWidth() - PADDING_LEFT; x += valueWidth) {
            if (x > PADDING_LEFT) {
                valuex = (int) ((x - sourcex) / valueWidth);
                canvas.drawLine(x, getMeasuredHeight() - paddingBottom, x, getMeasuredHeight() - paddingBottom - 10, mChartPaint);
                float txtWidth = mChartPaint.measureText(getFormatter().getCoordinate(valuex));
                float txtHeight = mChartPaint.measureText("10");
                if (valueWidth >= axisWidth) {
                    canvas.drawText(getFormatter().getCoordinate(valuex), x - txtWidth / 2, getMeasuredHeight() - paddingBottom + txtHeight, mChartPaint);
                } else {
                    int n = (int) (axisWidth / valueWidth);
                    if (valuex % n == 0) {
                        canvas.drawText(getFormatter().getCoordinate(valuex), x - txtWidth / 2, getMeasuredHeight() - paddingBottom + txtHeight, mChartPaint);
                    }
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
        canvas.drawRect(0,getMeasuredHeight() - paddingBottom,getMeasuredWidth(), getMeasuredHeight(),defaultPaint);
        canvas.drawLine(PADDING_LEFT,PADDING_LEFT, PADDING_LEFT, getMeasuredHeight() - paddingBottom, mChartPaint);
        canvas.drawLine(PADDING_LEFT,getMeasuredHeight() - paddingBottom,
                getMeasuredWidth() - PADDING_LEFT, getMeasuredHeight() - paddingBottom,mChartPaint);

        float  startx = PADDING_LEFT;
        float  starty = getMeasuredHeight() - paddingBottom + 50;
        float  txtHeight = mTextPaint.measureText("图");
        for(int i = 0; i < slikChartAdapter.mData.size(); i ++){
            if(slikChartAdapter.mData.get(i).getLinecolor() != 0) {
                defaultPaint.setColor(slikChartAdapter.mData.get(i).getLinecolor());
            }
            float width = mTextPaint.measureText(slikChartAdapter.mData.get(i).getName());
            canvas.drawText(slikChartAdapter.mData.get(i).getName(),startx ,starty + txtHeight,mTextPaint);
            startx = startx + width + 20;
            canvas.drawRect(startx, starty, startx + 50 ,starty + txtHeight,defaultPaint);
            startx = startx + 50 + 30;
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
        if(event.getY() < getMeasuredHeight() - paddingBottom){
            int value = (int) ((event.getX() - sourcex)/valueWidth);
            SlikLineChartPoint point = new SlikLineChartPoint();
            int clicki = 0;
            point.setY(10000);
            for(int i = 0; i < slikChartAdapter.mData.size(); i ++){
                if(slikChartAdapter.mData.get(i).getData().size() > value){
                    if(point == null){
                        clicki = i;
                        point = slikChartAdapter.mData.get(i).getData().get(value);
                    } else if(slikChartAdapter.mData.get(i).getData().get(value).getY() < point.getY()){
                        clicki = i;
                        point = slikChartAdapter.mData.get(i).getData().get(value);
                    }
                }
            }
            if(point != null){
//                chartItemClickListener.onChartItemClick(clicki,value);
                slikLineChartPoint = point;
                clickPosition1 = clicki;
                clickPosition2 = value;
                rePicture = false;
                invalidate();
            }
        }
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
        if(slikChartAdapter.mData == null || slikChartAdapter.mData.size() <= 0 || slikChartAdapter.getMaxDatasize() == 0){
            return;
        }
        valueWidth = (getMeasuredWidth() - PADDING_LEFT - PADDING_LEFT)/ slikChartAdapter.getMaxDatasize();
        valueHeight = (getMeasuredHeight() - paddingBottom - PADDING_LEFT - zeroHeight)/(slikChartAdapter.getMaxValue() * 1.1f);
        sourcex = PADDING_LEFT + gestureDetorManager.scrollDistanceX;
        sourcey = getMeasuredHeight() - paddingBottom - zeroHeight + gestureDetorManager.scrollDistanceY;
        sourcex = sourcex + (1 - gestureDetorManager.scaleValueX) * (gestureDetorManager.focusX - sourcex);
        sourcey = sourcey - (1 - gestureDetorManager.scaleValueY) * (sourcey - gestureDetorManager.focusY);
        if(sourcex > PADDING_LEFT){
            sourcex = PADDING_LEFT;
        }
        if(sourcey < getMeasuredHeight() - paddingBottom - zeroHeight){
            sourcey = getMeasuredHeight() - paddingBottom - zeroHeight;
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

    public void setOnchartItemClickListener(OnSlikLineChartItemClickListener listener){
        this.chartItemClickListener = listener;
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
}
