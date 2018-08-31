package com.diy.charts.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.diy.charts.AxisFormatter;
import com.diy.charts.SlimChartAxisFormatter;
import com.diy.charts.beans.PointBean;
import com.diy.charts.beans.SlikLineChartBean;
import com.diy.charts.detector.DirectionGestureDector;
import com.diy.charts.detector.DirectionGestureDectorListenr;
import com.diy.charts.detector.GestureData;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 顺滑折线图
 */
public class SlikLineChart extends View{
    private AxisFormatter formatter;
    private Context mContext;
    private TextPaint mTextPaint;//文字画笔
    private Paint mChartPaint;//表格画笔
    private Paint defaultPaint;//多用画图

    /**
     * 手势监听器
     */
    private DirectionGestureDector mDirectionGestureDector;
    private GestureDetector mGesturDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private int mTextColor = Color.parseColor("#ffffff");
    private int mChartColor = Color.parseColor("#ffffff");
    private int mBgcolor = Color.parseColor("#6495ED");

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
    float valueWidth ;
    float valueHeight ;
    float sourcex;
    float sourcey;
    float axisWidth = 30;//坐标值标记文字之间的最小距离
    float axisHeight = 100;//坐标值标记文字之间的最小距离

    private ArrayList<SlikLineChartBean> mData;
    private int maxSize;
    private float maxValue;

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
                if(scaleValueY < 1){
                    scaleValueY = 1;
                    scrollDistanceY = 0;
                }
                if(scaleValueX < 1){
                    scaleValueX = 1;
                    scrollDistanceX = 0;
                }
                scaleValueX = scaleValueX > 5 ? 5 : scaleValueX;
                scaleValueY = scaleValueY > 5 ? 5 : scaleValueY;
                invalidate();
                return true;
            }
        });
        mScaleGestureDetector =  new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                scaleValueX = scaleValueX * detector.getScaleFactor();
                scaleValueY = scaleValueY * detector.getScaleFactor();
                if(scaleValueY < 1){
                    scaleValueY = 1;
                    scrollDistanceY = 0;
                }
                if(scaleValueX < 1){
                    scaleValueX = 1;
                    scrollDistanceX = 0;
                }
                scaleValueX = scaleValueX > 1.5f ? 1.5f : scaleValueX;
                scaleValueY = scaleValueY > 1.5f ? 1.5f : scaleValueY;
                invalidate();         //当缩放比例接近原大小时，画布平移回原点
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
                if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1){
                    return true;
                }
                //只有当value>1,即图表处于放大状态时才可以拖动
                if(scaleValueX > 1) {
                    scrollDistanceX = scrollDistanceX - distanceX;
                    //不可以往
                }
                if(scaleValueY > 1) {
                    scrollDistanceY = scrollDistanceY - distanceY;
                }
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
        defaultPaint.setStyle(Paint.Style.STROKE);
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
        canvas.drawColor(mBgcolor);
        drawLines(canvas);
        drawBottomTxt(canvas);
        drawAxis(canvas);
    }



    /**
     * 在ontouEvent中托管MotionEvent给手势监听器
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mDirectionGestureDector.onTouchEvent(event);
        mGesturDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setData(ArrayList<SlikLineChartBean> data){
        this.mData = data;
        maxSize = 0;
        invalidate();
    }

    /**
     * 根据不同的锚点，根据缩放比例，对坐标系进行缩放，绘制折线图，为了避免缩放时文字显示以及图表失真，不使用画布
     * 进行缩放，改为坐标系尺度缩放并重绘。
     * @param canvas
     */
    private void drawLines(Canvas canvas){
        valueWidth = (getMeasuredWidth() - CHART_PADDING_HORIZONTAL - CHART_PADDING_HORIZONTAL)/ getMaxDatasize();
        valueHeight = (getMeasuredHeight() - CHART_PADDING_BOTTOM - CHART_PADDING_HORIZONTAL)/getMaxValue();
        sourcex = CHART_PADDING_HORIZONTAL + scrollDistanceX;
        sourcey = getMeasuredHeight() - CHART_PADDING_BOTTOM + scrollDistanceY;
        sourcex = sourcex + (1 - scaleValueX) * (focusX - sourcex);
        sourcey = sourcey - (1 - scaleValueY) * (sourcey - focusY);
        if(sourcex > CHART_PADDING_HORIZONTAL){
            sourcex = CHART_PADDING_HORIZONTAL;
        }
        if(sourcey < getMeasuredHeight() - CHART_PADDING_BOTTOM){
            sourcey = getMeasuredHeight() - CHART_PADDING_BOTTOM;
        }
        valueWidth = valueWidth * scaleValueX;
        valueHeight = valueHeight * scaleValueY;
        defaultPaint.setStyle(Paint.Style.STROKE);
        defaultPaint.setStrokeJoin(Paint.Join.ROUND);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(5);
        defaultPaint.setPathEffect(cornerPathEffect);
        for(int i = 0; i < mData.size(); i ++){
           SlikLineChartBean bean = mData.get(i);
           Path path = new Path();
           path.moveTo(sourcex, sourcey);
           ArrayList<PointBean> points = new ArrayList<>();
           for(int j = 0; j < bean.getData().size(); j ++){
               Log.v("verf","i = " + i + " " + j + " ");
               float x = sourcex + valueWidth * (j + 1);
               float y = sourcey - valueHeight * bean.getData().get(j).getValue();
               path.lineTo(x,y);
               points.add(new PointBean(x,y));
               canvas.drawText("" + bean.getData().get(j).getValue() ,x,y - 15,mChartPaint);
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
                    canvas.drawCircle(points.get(k).getX(),points.get(k).getY(),bean.getCircleRadius(),defaultPaint);
                    defaultPaint.setColor(bean.getLinecolor());
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
        canvas.drawRect(0,0,CHART_PADDING_HORIZONTAL, getMeasuredHeight(),defaultPaint);
        canvas.drawRect(0,getMeasuredHeight() - CHART_PADDING_BOTTOM,getMeasuredWidth(), getMeasuredHeight(),defaultPaint);
        canvas.drawLine(CHART_PADDING_HORIZONTAL,CHART_PADDING_HORIZONTAL, CHART_PADDING_HORIZONTAL, getMeasuredHeight() - CHART_PADDING_BOTTOM, mChartPaint);
        canvas.drawLine(CHART_PADDING_HORIZONTAL,getMeasuredHeight() - CHART_PADDING_BOTTOM,
                getMeasuredWidth() - CHART_PADDING_HORIZONTAL, getMeasuredHeight() - CHART_PADDING_BOTTOM,mChartPaint);

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

    private void drawAxis(Canvas canvas){
        float valuey = 0;
        for(float y = sourcey; y > CHART_PADDING_HORIZONTAL; y-=axisHeight){
            if(y < getMeasuredHeight() - CHART_PADDING_BOTTOM){
                String txt = "";
                valuey = (sourcey - y)/valueHeight;
                if(axisHeight/valueHeight < 1) {
                    BigDecimal b = new BigDecimal(valuey);
                    txt = "" + b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                }else {
                    txt = "" + (int)valuey;
                }
                canvas.drawLine(CHART_PADDING_HORIZONTAL, y, CHART_PADDING_HORIZONTAL + 8, y, mChartPaint);
                canvas.drawText(txt,CHART_PADDING_HORIZONTAL - mChartPaint.measureText(txt) - 10, y,mChartPaint);
            }
        }
        int valuex = 0;
        for(float x = sourcex; x < getMeasuredWidth() - CHART_PADDING_HORIZONTAL; x+=axisWidth){
            if(x > CHART_PADDING_HORIZONTAL){
                valuex = (int) ((x - sourcex)/valueHeight) + 1;
                canvas.drawLine(x, getMeasuredHeight() - CHART_PADDING_BOTTOM, x, getMeasuredHeight() - CHART_PADDING_BOTTOM - 10, mChartPaint);
                canvas.drawText(getFormatter().getCoordinate(valuex),x, getMeasuredHeight() - CHART_PADDING_BOTTOM + 15,mChartPaint);
            }
        }
    }


    /**
     * 获取所有直线中数据量最大的一条中的数据个数
     * @return
     */
    private int getMaxDatasize(){
        if(maxSize != 0){
            return maxSize;
        }
        for(int i = 0; i < mData.size(); i ++){
            if(mData.get(i).getData().size() > maxSize){
                maxSize = mData.get(i).getData().size();
            }
        }
        return maxSize;
    }

    /**
     * 获取所有直线中单笔数量最大的数据
     * @return
     */
    private float getMaxValue(){
        if(maxValue != 0){
            return maxValue;
        }
        for(int i = 0; i < mData.size(); i ++){
            for(int j = 0; j < mData.get(i).getData().size(); j ++){
                if(mData.get(i).getData().get(j).getValue() > maxValue){
                    maxValue = mData.get(i).getData().get(j).getValue();
                }
            }
        }
        return maxValue;
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
}
