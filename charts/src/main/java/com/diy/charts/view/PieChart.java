package com.diy.charts.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.diy.charts.beans.PiechartBean;
import com.diy.charts.beans.PointBean;
import com.diy.charts.charts.R;
import com.diy.charts.utils.ChartUtil;

import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 顺滑折线图
 */
public class PieChart extends View {
    private Context mContext;
    private GestureDetector gestureDetector;
    private ArrayList<PiechartBean> mData;
    private TextPaint mTextPaint;//文字画笔
    private Paint mChartPaint;//tub画笔
    private Paint defaultPaint;//多用画图
    private RectF rect;
    private RectF clickRect;
    private boolean showTxt = false;//是否展示
    private boolean showTuli = true;//是否展示地图图例

    private boolean showclick;

    private String centerTxt = "PieChart By verf";
    private float[] pos = new float[2];
    private int mTextColor = Color.parseColor("#ffffff");
    private int mChartColor = Color.parseColor("#ffffff");
    private int mBgcolor = Color.parseColor("#6495ED");
    private int smallCircleOutColor = Color.parseColor("#50ffffff");
    private int smallCircleColor = Color.parseColor("#ffffff");
    private int smallCircleTxtColor = Color.parseColor("#6495ED");

    private int smallRadius = 190;
    private int circleRadius = 400;
    private float startAngel = 0; //绘制饼图的偏移量
    private float animationValue = 1f;
    private PointBean pointBean;
    /**
     * 水平留白距离和底部留白距离
     */
    private final int PADDING_LEFT = 50;
    private final int PADDING_BOTTOM = 50;
    private int clicki;

    public PieChart(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private void getAttrs(AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, com.diy.charts.charts.R.styleable.PieChart);
            mTextColor = typedArray.getColor(R.styleable.PieChart_PieChart_TextColor, mTextColor);
            mChartColor = typedArray.getColor(R.styleable.PieChart_PieChart_ChartColor, mChartColor);
            typedArray.recycle();
        }
    }

    private void init(AttributeSet attrs){
        getAttrs(attrs);
        mData = new ArrayList<>();
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(Math.abs(e.getX() - getMeasuredWidth()/2) <= circleRadius && Math.abs(e.getY() - getMeasuredHeight()/2) <= circleRadius){
                    double degree = ChartUtil.getTanDegreeX(e.getX() - getMeasuredWidth()/2, e.getY() - getMeasuredHeight()/2);
                    for(int i = 0; i < mData.size(); i ++){
                        if(mData.get(i).getStartAngel() < degree && mData.get(i).getStartAngel() + mData.get(i).getSweepAngel() > degree){
                            clicki = i;
                            showclick = true;
                            invalidate();
                        }
                    }
                }

                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        initPaint();

    }

    private void initPaint(){
        mTextPaint = new TextPaint();
        mTextPaint.setStrokeWidth(2f);
        mTextPaint.setTextSize(45);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);

        mChartPaint = new Paint();
        mChartPaint.setColor(mChartColor);
        mChartPaint.setAntiAlias(true);
        mChartPaint.setStrokeWidth(3);
        mChartPaint.setTextSize(25);
        mChartPaint.setStyle(Paint.Style.FILL);

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
        rect = new RectF(getMeasuredWidth()/2 - circleRadius, getMeasuredHeight()/2 - circleRadius,
                getMeasuredWidth()/2 + circleRadius, getMeasuredHeight()/2 + circleRadius);
        clickRect = new RectF(getMeasuredWidth()/2 - circleRadius - 20, getMeasuredHeight()/2 - circleRadius - 20,
                getMeasuredWidth()/2 + circleRadius + 20, getMeasuredHeight()/2 + circleRadius + 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBgcolor);
        /**
         * 判断数据为空时，提示暂无数据
         */
        if(mData == null || mData.size() == 0){
            Rect rect = new Rect(0,getMeasuredHeight()/2, getMeasuredWidth(), getMeasuredHeight()/2 + 100);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText("暂无数据",rect.centerX(),baseline, mTextPaint);
            return;
        }
        defaultPaint.setStyle(Paint.Style.FILL);
        /**
         * 画图例和注记
         */
        for(int i = 0; i < mData.size(); i ++){
            float txtheight = mChartPaint.measureText("X");
            float txtwidth = mChartPaint.measureText(mData.get(i).getName());
            float txtpaddingHeight = 20;
            float txtpaddingWidth = 30;
            float rectWidth = 50;
            canvas.drawText(mData.get(i).getName(),getMeasuredWidth() - FOCUS_LEFT - txtwidth,
                    PADDING_LEFT + (i + 1) * txtheight + i * txtpaddingHeight , mChartPaint);
            defaultPaint.setColor(mData.get(i).getColor());
            float startx = getMeasuredWidth() - FOCUS_LEFT - txtwidth - txtpaddingWidth - rectWidth;
            float starty = PADDING_LEFT + i * 20 + i * txtheight;
            canvas.drawRect(startx, starty, startx + rectWidth, starty + txtheight, defaultPaint);
        }

        float drawStartangel = startAngel;
        //画扇形
        for(int i = 0; i < mData.size(); i ++){
            defaultPaint.setColor(mData.get(i).getColor());
            mData.get(i).setStartAngel(drawStartangel);
            mData.get(i).setSweepAngel(360 * mData.get(i).getPercent());
            if(showclick && i == clicki){
                canvas.drawArc(clickRect, drawStartangel, 360 * mData.get(i).getPercent() * animationValue,true,defaultPaint);
                showclick = false;
            }else {
                canvas.drawArc(rect, drawStartangel, 360 * mData.get(i).getPercent()* animationValue, true, defaultPaint);
            }
            drawStartangel = drawStartangel + 360 * mData.get(i).getPercent() * animationValue;

            Path path = new Path();
            path.addCircle(getMeasuredWidth()/2,getMeasuredHeight()/2, circleRadius - (circleRadius - smallRadius)/2, Path.Direction.CW);
            PathMeasure measure = new PathMeasure(path, false);
            //写扇形上的文字，通过PathMeasure确定中点坐标
            float txtWidth = mChartPaint.measureText(mData.get(i).getName());
            float txtheight = mChartPaint.measureText("X");
            float txtangel = (mData.get(i).getStartAngel() + mData.get(i).getSweepAngel() * animationValue/2) % 360;
            if(txtangel < 0){
                txtangel = 360 + txtangel;
            }
            measure.getPosTan(measure.getLength() * (txtangel/360),pos,null);
            canvas.drawText(mData.get(i).getName(),pos[0] - txtWidth/2, pos[1] + txtheight/2, mChartPaint);
            canvas.drawText(mData.get(i).getPercenttxt(),pos[0] - txtWidth/2, pos[1] + txtheight/2 + 40, mChartPaint);
        }

        //画中心的圆以及绘制中心文字
        float smallCircletxtWidth = mTextPaint.measureText(centerTxt);
        float smallCircletxtHeigth = mTextPaint.measureText("X");
        float circlePadding = 25;
        defaultPaint.setColor(smallCircleOutColor);
        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, smallRadius + circlePadding,defaultPaint);
        defaultPaint.setColor(smallCircleColor);
        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, smallRadius ,defaultPaint);
        mTextPaint.setColor(smallCircleTxtColor);
        canvas.drawText(centerTxt,getMeasuredWidth()/2 - smallCircletxtWidth/2, getMeasuredHeight()/2 + smallCircletxtHeigth/2, mTextPaint);
    }

    public void setData(ArrayList<PiechartBean> data){
        mData = data;
        calPercent();
        showStartAnimation();
    }

    public void notifyDataChanged(ArrayList<PiechartBean> data){
        mData = data;
        calPercent();
        invalidate();
    }

    /**
     * 画坐标轴，图例和助记
     * @param canvas
     */
    private void drawBottomTxt(Canvas canvas){

    }

    /**
     * 在ontouEvent中托管MotionEvent给手势监听器
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(pointBean == null){
                pointBean = new PointBean(event.getX(),event.getY());
            }else {
                //获取两次滑动的坐标点与中心点连线在 Y轴负方向上的夹角。做差后即为此次滑动后图形需要转动的角度
                double degree1 = ChartUtil.getTanDegreeInverseY(event.getX() - getMeasuredWidth()/2, event.getY() - getMeasuredHeight()/2);
                double degree2 = ChartUtil.getTanDegreeInverseY(pointBean.getX() - getMeasuredWidth()/2, pointBean.getY() - getMeasuredHeight()/2);
                //Y轴负方向重合时，可能返回0，也可能返回360， 判断如果上个点在第三象限，那么应该从左滑向右，计做 360，反之计作0
                if(pointBean.getY() > getMeasuredHeight()/2 && pointBean.getX() < getMeasuredWidth()/2  && degree2 == 0){
                    degree2 = 360;
                }
                if(pointBean.getY() > getMeasuredHeight()/2 && pointBean.getX() > getMeasuredWidth()/2  && degree2 == 360){
                    degree2 = 0;
                }
                startAngel += (degree2 - degree1);
                startAngel = startAngel % 360;
                pointBean.setX(event.getX());
                pointBean.setY(event.getY());
                invalidate();
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            pointBean = null;
        }
        return true;
    }


    /**
     * 负责第一次设置数据时的显示动画
     */
    private void calPercent(){
        float total = 0;
        for(int i = 0; i < mData.size(); i ++){
            total += mData.get(i).getValue();
        }
        for(int i = 0; i < mData.size(); i ++){
            mData.get(i).setPercent(mData.get(i).getValue()/total);
        }
    }

    /**
     * 展示开始动画
     */
    public void showStartAnimation(){
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
}
