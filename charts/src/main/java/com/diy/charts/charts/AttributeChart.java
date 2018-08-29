package com.diy.charts.charts;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import com.diy.charts.charts.beans.AttributeChartData;
import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/28.
 * 属性列表
 */

public class AttributeChart extends SurfaceView{
    private Context mContext;
    private TextPaint mTextPaint;
    private Paint mChartPaint;
    private Paint mCoverPaint;
    private ArrayList<Path> mPaths;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGesturDetector;
    float []pos1 = new float[2];
    float []pos2 = new float[2];
    float []pos3 = new float[2];

    private int mTextColor = Color.parseColor("#ffffff");
    private int mChartColor = Color.parseColor("#ffffff");
    private int mCoverColor = Color.parseColor("#809400D3");
    private final int LINE_WIDTH = 80;
    private final int CHART_PADDING = 100;

    private float focusX;
    private float focusY;
    private float scaleValue = 1f;

    private float scrollDistanceX;
    private float scrollDistanceY;

    private ArrayList<AttributeChartData> mData;
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

    public void getAttrs(AttributeSet attrs){
        if(attrs != null){

        }
    }

    public void init(AttributeSet attrs){
        getAttrs(attrs);
        mData = new ArrayList<>();
        mPaths = new ArrayList<>();
        initPaint();
        mScaleGestureDetector =  new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                scaleValue = scaleValue * detector.getScaleFactor();
                //设置最大缩放比例
                scaleValue =scaleValue > 3 ? 3 : scaleValue;
                //当缩放比例接近原大小时，画布平移回原点
                if(scaleValue > 0.9 && scaleValue < 1.1){
                    scrollDistanceX = 0;
                    scrollDistanceY = 0;
                }
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
        mGesturDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
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

    public void initPaint(){
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
        mChartPaint.setStrokeWidth(5);
        mChartPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(ArrayList<AttributeChartData> data){
        this.mData = data;
        invalidate();
    }

    public int getMaxValues(){
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //处理缩放事件，对画布进行缩放
        canvas.scale(scaleValue,scaleValue,focusX,focusY);
        //处理平移事件，对画布进行平移
        canvas.translate(-scrollDistanceX,-scrollDistanceY);
        //没有数据时进行文字提示-暂无数据
        if(mData == null || mData.size() == 0){
            Rect rect = new Rect(0,getMeasuredHeight()/2, getMeasuredWidth(), getMeasuredHeight()/2 + 100);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText("暂无数据",rect.centerX(),baseline, mTextPaint);
            return;
        }
        int n = mData.size();
        mPaths.clear();
        //遍历画刻度横线
        for(int i = getMeasuredWidth()/2; i > CHART_PADDING; i = i - LINE_WIDTH){
            Path path = new Path();
            path.addCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, getMeasuredWidth()/2 - i,Path.Direction.CCW);
            mPaths.add(path);
            PathMeasure measure = new PathMeasure(path, false);     // 创建 PathMeasure
            //遍历画圆，获取固定点坐标，连线画横线
            for(int j = 0; j < n ; j ++){
                measure.getPosTan(measure.getLength() * j / n, pos1, null);
                if(j + 1 >= n){
                    measure.getPosTan(measure.getLength() * 0,pos2,null);
                }else{
                    measure.getPosTan(measure.getLength() * (j + 1)/n,pos2,null);
                }
                canvas.drawLine(pos1[0],pos1[1],pos2[0],pos2[1],mChartPaint);
                //判断，如果是最大的圆，获取固定点坐标画纵向线
                if(i - LINE_WIDTH <= CHART_PADDING){
                    canvas.drawLine(getMeasuredWidth()/2, getMeasuredHeight()/2, pos1[0], pos1[1], mChartPaint);
//                    if(pos1[0] < getMeasuredWidth()/2){
//                        pos1[0] -= 80;
//                    }
                    //写注记文字
                    canvas.drawText(mData.get(j).getName(), pos1[0],pos1[1], mTextPaint);
                }
            }
        }
        for(int i = 0; i < mPaths.size() - 1; i ++){
            PathMeasure measure = new PathMeasure(mPaths.get(i), false);
            measure.getPosTan(0,pos1,null);
            canvas.drawText("" + getMaxValues()/mPaths.size() * (i + 1), pos1[0],pos1[1] - 5, mTextPaint);
        }
        Path chartPath = new Path();
        for(int i = 0; i < mData.size(); i ++){
            float radius = 0;
            radius = (getMeasuredWidth()/2 - CHART_PADDING) * (mData.get(i).getValue()/getMaxValues());
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
            canvas.drawCircle(pos3[0], pos3[1],10,mChartPaint);
        }
        chartPath.close();
        chartPath.setFillType(Path.FillType.EVEN_ODD);
        mTextPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(chartPath,mTextPaint);
        mCoverPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(chartPath,mCoverPaint);
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
}
