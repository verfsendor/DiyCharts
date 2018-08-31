package com.diy.charts.adapter;
import com.diy.charts.beans.SlikLineChartBean;
import java.util.ArrayList;

/**
 * Created by xuzhendong on 2018/8/31.
 */

public class SlikChartAdapter {
    public ArrayList<SlikLineChartBean> mData;
    private int maxSize;
    private float maxValue;

    public SlikChartAdapter(){
        mData = new ArrayList<>();
    }

    public void setData(ArrayList<SlikLineChartBean> data){
        this.mData = data;
        maxSize = 0;
        maxValue = 0;
    }

    /**
     * 获取所有直线中数据量最大的一条中的数据个数
     * @return
     */
    public int getMaxDatasize(){
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
    public float getMaxValue(){
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

    public boolean isEmpty(){
        if(mData == null || mData.size() == 0){
            return true;
        }
        return false;
    }

}
