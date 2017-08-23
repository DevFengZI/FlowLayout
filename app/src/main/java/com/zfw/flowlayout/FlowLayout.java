package com.zfw.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfw on 2017/8/17.
 */

public class FlowLayout extends ViewGroup{

    /**
     * 布局每一行的View
     */
    private List<List<View>> mViewList = new ArrayList<>();
    /**
     * 布局每一行的高
     */
    private List<Integer> mHeightList = new ArrayList<>();

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 使流式布局控件支持Margin属性
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("onMeasure", "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int desireWidth = 0;
        int desireHeight = 0;

        if(widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            desireWidth = widthSize;
            desireHeight = heightSize;
        } else {
            int childCount = getChildCount();
            int childWidth = 0;
            int childHeight = 0;
            int curWidth = 0;
            int curHeight = 0;
            List<View> lineViews = new ArrayList<>();
            for(int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                childWidth = childView.getMeasuredWidth() + params.leftMargin + params.rightMargin;
                childHeight = childView.getMeasuredHeight() + params.topMargin + params.bottomMargin;
                if(curWidth + childWidth > widthSize) { // 当前宽度大于父容器的宽度，则换行
                    desireWidth = Math.max(desireWidth, curWidth);
                    desireHeight += curHeight;
                    mViewList.add(lineViews);
                    mHeightList.add(curHeight);
                    // 换行
                    curWidth = childWidth;
                    curHeight = childHeight;
                    // 换行后清空lineView，进入下一行的记录
                    lineViews = new ArrayList<>();
                    lineViews.add(childView);

                } else {
                    curWidth += childWidth;
                    curHeight = Math.max(curHeight, childHeight);
                    // 记录每一行的子View
                    lineViews.add(childView);
                }
                // 换行时，如果整好是最后一个需要换行
                if(i == childCount - 1) {
                    desireWidth = Math.max(desireWidth, curWidth);
                    desireHeight += curHeight;
                    mViewList.add(lineViews);
                    mHeightList.add(curHeight);
                }
            }
        }
        setMeasuredDimension(desireWidth, desireHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("onLayout", "onLayout");
        int lineSize = mViewList.size();
        int childLeft = 0;
        int childTop = 0;
        int childRight = 0;
        int childBottom = 0;
        int curLeft = 0;
        int curTop = 0;
        for(int i = 0; i < lineSize; i++) {
            List<View> viewList = mViewList.get(i);
            int viewCount = viewList.size();
            for(int j = 0; j < viewCount; j++) {
                View childView = viewList.get(j);
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                childLeft = curLeft + params.leftMargin;
                childTop = curTop + params.topMargin;
                childRight = childLeft + childView.getMeasuredWidth();
                childBottom = childTop + childView.getMeasuredHeight();
                childView.layout(childLeft, childTop, childRight, childBottom);
                curLeft += childView.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            }
            curTop += mHeightList.get(i);
            curLeft = 0;
        }
        mViewList.clear();
        mHeightList.clear();
    }

}
