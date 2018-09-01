package com.diy.charts.view;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Picture;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.diy.charts.beans.AttributeChartBean;

import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 雷达图
 */

public class AttributeChart extends SurfaceView{
    private Context mContext;
    private TextPaint mTextPaint;//文字画笔
    private Paint mChartPaint;//表格画笔
    private Paint mCoverPaint;//数据显示蒙版的颜色
    private Paint mLinePathPaint;//数据间连线的画笔
    /**
     * 手势监听器
     */
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGesturDetector;


    /**
     * 记录雷达图圆形外扩的path
     */
    private ArrayList<Path> mPaths;


    /**
     * 记录画线时的锚点坐标
     */
    float []pos1 = new float[2];
    float []pos2 = new float[2];
    float []pos3 = new float[2];
    float []pos4 = new float[2];
    float []pos5 = new float[2];


    private int mTextColor = Color.parseColor("#ffffff");
    private int mChartColor = Color.parseColor("#ffffff");
    private int mCoverColor = Color.parseColor("#809400D3");
    private int mLineColor = Color.parseColor("#f400D3");

    /**
     * 刻度线间距以及chart到view边缘的留白距离
     */
    private final int LINE_WIDTH = 80;
    private final int CHART_PADDING = 100;

    /**
     * 缩放中心以及缩放比例
     */
    private float focusX;
    private float focusY;
    private float scaleValue = 1f;

    /**
     * 平移x y 距离
     */
    private float scrollDistanceX;
    private float scrollDistanceY;

    private ValueAnimator valueAnimator;
    private Picture mPicture;
    private float animationValue;
    private boolean showAnim;
    private boolean rePicture = true;//判断是否需要重新绘制表盘
    private ArrayList<AttributeChartBean> mData;
    public AttributeChart(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public AttributeChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public AttributeChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }

    private void getAttrs(AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, com.diy.charts.charts.R.styleable.AttributeChart);
//            progressStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.CircleProgressBarView_progressStrokeWidth, defaultStrokeWidth);
            mTextColor = typedArray.getColor(com.diy.charts.charts.R.styleable.AttributeChart_Attribute_TextColor, mTextColor);
            mChartColor = typedArray.getColor(com.diy.charts.charts.R.styleable.AttributeChart_Attribute_ChartColor, mChartColor);
            mCoverColor = typedArray.getColor(com.diy.charts.charts.R.styleable.AttributeChart_Attribute_CoverColor, mCoverColor);
            mLineColor = typedArray.getColor(com.diy.charts.charts.R.styleable.AttributeChart_Attribute_LineColor, mLineColor);
            showAnim = typedArray.getBoolean(com.diy.charts.charts.R.styleable.AttributeChart_Attribute_showAnim, true);
            typedArray.recycle();
        }
    }

    private void init(AttributeSet attrs){
        getAttrs(attrs);
        mData = new ArrayList<>();
        mPaths = new ArrayList<>();
        initPaint();
        mScaleGestureDetector =  new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                scaleValue = scaleValue * detector.getScaleFactor();
                Log.v("aaaa","getScaleFactor " + detector.getScaleFactor() + " " + scaleValue);
                //设置最大缩放比例
                if(scaleValue > 3){
                    scaleValue =3;
                }else {
                    if(scaleValue > 0.9 && scaleValue < 1.1){
                        scrollDistanceX = 0;
                        scrollDistanceY = 0;
                    }
                    invalidate();
                }
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
                if(scaleValue > 1.4) {
                    scrollDistanceX = scrollDistanceX + distanceX;
                    scrollDistanceY = scrollDistanceY + distanceY;
                    invalidate();
                }
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

        mCoverPaint = new Paint();
        mCoverPaint.setColor(mCoverColor);
        mCoverPaint.setAntiAlias(true);
        mCoverPaint.setStrokeWidth(7);
        mCoverPaint.setStyle(Paint.Style.FILL);

        mChartPaint = new Paint();
        mChartPaint.setColor(mChartColor);
        mChartPaint.setAntiAlias(true);
        mChartPaint.setStrokeWidth(3);
        mChartPaint.setStyle(Paint.Style.FILL);

        mLinePathPaint = new Paint();
        mLinePathPaint.setColor(mLineColor);
        mLinePathPaint.setAntiAlias(true);
        mLinePathPaint.setStrokeWidth(5);
        mLinePathPaint.setStyle(Paint.Style.STROKE);
    }


    public void setData(ArrayList<AttributeChartBean> data){
        this.mData = data;
        rePicture = true;
        if(showAnim) {
            showStartAnimation();
        }else {
            invalidate();
        }
    }

    /**
     * 更新数据
     * @param data
     */
    public void notifyDataChanged(ArrayList<AttributeChartBean> data){
        this.mData = data;
        rePicture = true;
        invalidate();
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
        /**
         * 处理缩放和平移操作，根据手势监听器的值对画布进行缩放和平移
         */
        canvas.scale(scaleValue,scaleValue,focusX,focusY);
        canvas.translate(-scrollDistanceX,-scrollDistanceY);

        //表盘部分录制到picture上，在下次绘制时直接复用
        if(rePicture){
            recordPicture();
        }
        canvas.drawPicture(mPicture);

        /**
         * 遍历数据值，根据与最大坐标的比例，获得每个数据所在圆的半径。根据等分值获取具体点坐标，然后加入path中。
         */
        Path chartPath = new Path();
        ArrayList<Float> points = new ArrayList<>();
        for(int i = 0; i < mData.size(); i ++){
            float radius = 0;
            radius = (getMeasuredWidth()/2 - CHART_PADDING) * (mData.get(i).getValue()/getMaxValues());
            radius = radius * animationValue;
            Path path = new Path();
            path.moveTo(getMeasuredWidth()/2 + radius, getMeasuredHeight()/2);
            path.addCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, radius, Path.Direction.CW);
            PathMeasure measure = new PathMeasure(path, false);
            measure.getPosTan(measure.getLength() * i/mData.size(),pos3,null);
            if(i == 0){
                chartPath.moveTo(pos3[0],pos3[1]);
            }else {
                chartPath.lineTo(pos3[0], pos3[1]);
            }
            points.add(pos3[0]);
            points.add(pos3[1]);
        }
        chartPath.close();
        /**
         * 画path的外围线与内部的填充色
         */
        canvas.drawPath(chartPath,mLinePathPaint);
        canvas.drawPath(chartPath,mCoverPaint);
        //画数据点的圆点
        for(int i = 0; i < points.size(); i += 2){
            if(i + 1 < points.size()){
                canvas.drawCircle(points.get(i), points.get(i + 1),10,mChartPaint);
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
        mScaleGestureDetector.onTouchEvent(event);
        mGesturDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 获得坐标度量最大值，数据列表中数据值的最大值，并进一位。确保坐标大于每一条数据
     * @return
     */
    private int getMaxValues(){
        if(mData == null || mData.size() == 0){
            return 0;
        }
        int max = 0;
        for(int i = 0; i < mData.size(); i ++){
            if(mData.get(i).getValue() > max){
                max = (int) mData.get(i).getValue();
            }
        }
        max = 5 * (max / 5 + 1);
        return max;
    }

    /**
     * 负责第一次设置数据时的显示动画
     */
    private void showStartAnimation(){
        valueAnimator = ValueAnimator.ofFloat(0,1)
                .setDuration(800);
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(valueAnimator != null){
            valueAnimator.cancel();
        }
    }

    /**
     * 将固定的表盘部分绘制在picture上进行复用
     */
    private void recordPicture(){
        mPicture = new Picture();
        Canvas canvas = mPicture.beginRecording(getMeasuredWidth(), getMeasuredHeight());
        int maxRadius;
        int n = mData.size();
        mPaths.clear();
        // 开始录制 (接收返回值Canvas)
        /**
         * 遍历画雷达波纹横线，每隔固定距离，画圆，通过Pathmeasure获取固定等分长度的点坐标，依次两两相连
         */
        for(int i = getMeasuredWidth()/2 - LINE_WIDTH; i > CHART_PADDING; i = i - LINE_WIDTH){
            Path path = new Path();
            path.addCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, getMeasuredWidth()/2 - i,Path.Direction.CCW);
            Log.v("verf","name add path " + path);
            mPaths.add(path);
            PathMeasure measure = new PathMeasure(path, false);     // 创建 PathMeasure
            for(int j = 0; j < n ; j ++){
                measure.getPosTan(measure.getLength() * j / n, pos1, null);
                if(j + 1 >= n){
                    measure.getPosTan(measure.getLength() * 0,pos2,null);
                }else{
                    measure.getPosTan(measure.getLength() * (j + 1)/n,pos2,null);
                }
                canvas.drawLine(pos1[0],pos1[1],pos2[0],pos2[1],mChartPaint);
                /**
                 * 判断距离，如果是最外的一层，获取等分点坐标后，坐标与圆心画雷达图纵向直线
                 */
                //判断，如果是最大的圆，获取固定点坐标画纵向线
                if(i - LINE_WIDTH <= CHART_PADDING){
                    maxRadius = getMeasuredWidth()/2 - i;
                    canvas.drawLine(getMeasuredWidth()/2, getMeasuredHeight()/2, pos1[0], pos1[1], mChartPaint);
                }
            }
        }

        /**
         * 绘制属性名称，因为文字都是以左下角为锚点，所以进行判断不同象限采用不同的位移进行处理，使之均匀分布
         */
        PathMeasure pathMeasure = new PathMeasure(mPaths.get(mPaths.size() - 1), false);     // 创建 PathMeasure
        for(int j = 0; j < n ; j ++) {
            pathMeasure.getPosTan(pathMeasure.getLength() * j / n, pos4, null);
            float width = mTextPaint.measureText(mData.get(j).getName()) + 5;
            float height = width/mData.get(j).getName().length() + 5;
            float distanx = pos4[0] - getMeasuredWidth() / 2;
            float distany = pos4[1] - getMeasuredHeight() / 2;
            if (distanx < 0 && distany < 0) {
                canvas.drawText(mData.get(j).getName(), pos4[0] - width, pos4[1], mTextPaint);
            } else if(distanx > 0 && distany > 0){
                canvas.drawText(mData.get(j).getName(), pos4[0], pos4[1] + height, mTextPaint);
            }else if(distanx < 0 && distany > 0){
                canvas.drawText(mData.get(j).getName(), pos4[0] - width, pos4[1] + height, mTextPaint);
            }else if(distanx > 0 && distany < 0){
                canvas.drawText(mData.get(j).getName(), pos4[0], pos4[1], mTextPaint);
            }else if(distany == 0 && distanx > 0){
                canvas.drawText(mData.get(j).getName(), pos4[0], pos4[1] + height, mTextPaint);
            }else if(distany == 0 && distanx < 0){
                canvas.drawText(mData.get(j).getName(), pos4[0] - width, pos4[1] + height/2 , mTextPaint);
            }else if(distanx == 0 && distany > 0){
                canvas.drawText(mData.get(j).getName(), pos4[0] - width/2, pos4[1] , mTextPaint);
            } else if(distanx == 0 && distany < 0){
                canvas.drawText(mData.get(j).getName(), pos4[0] - width/2, pos4[1] - height , mTextPaint);
            }
        }
        /**
         * 遍历波纹层，获取圆上的坐标点，获取数据中最大的值确定坐标范围。得出每层波纹的坐标值并在角度为0的固定位置书写。
         */
        for(int i = 0; i < mPaths.size(); i ++){
            PathMeasure measure = new PathMeasure(mPaths.get(i), false);
            measure.getPosTan(0,pos5,null);
            canvas.drawText("" + getMaxValues()/mPaths.size() * (i + 1), pos5[0],pos5[1] - 5, mTextPaint);
        }
        mPicture.endRecording();
        rePicture = false;
    }
}
