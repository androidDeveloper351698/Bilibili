package com.kaede.bilibilikaede.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kaede.bilibilikaede.R;

/**
 * Created by asus on 2016/2/6.
 */
public class AutoSizeMultiLinearLayout extends ViewGroup {

    private MyAdapter myAdapter;

    private int dp2px(int dp){
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp*density +0.5f);
    }

    public AutoSizeMultiLinearLayout(Context context) {
        this(context,null);
    }

    public AutoSizeMultiLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AutoSizeMultiLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addDropDownArrow();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();//parent的宽度

        for(int a=0;a<getChildCount();a++){
            View child = getChildAt(a);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.measure(getChildMeasureSpec(widthMeasureSpec,child.getPaddingLeft()+child.getPaddingRight()+lp.leftMargin+lp.rightMargin,lp.width),getChildMeasureSpec(heightMeasureSpec,child.getPaddingTop()+child.getPaddingBottom()+lp.topMargin+lp.bottomMargin,lp.height));
        }

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int availableWidth = measuredWidth - getPaddingRight() - getPaddingLeft();
        int offset = 0;
        for(int i=0;i<getChildCount();i++){
            if(i+1<getChildCount()) {
                View child = getChildAt(i);
                View nextChild = getChildAt(i + 1);
                LayoutParams lp1 = (LayoutParams) child.getLayoutParams();
                LayoutParams lp2 = (LayoutParams) nextChild.getLayoutParams();
                int childMeasuredWidth = child.getMeasuredWidth();
                int nextChildMeasuredWidth = nextChild.getMeasuredWidth();
                int childWidth = childMeasuredWidth + lp1.leftMargin + lp1.rightMargin;
                if(i == 0){
                    lp1.top = top;
                    lp1.left = measuredWidth - getPaddingRight() - childWidth;
                    offset = childWidth;
                    continue;
                }

                if(childMeasuredWidth+nextChildMeasuredWidth+lp1.leftMargin+lp1.rightMargin+lp2.leftMargin+lp2.rightMargin>availableWidth - offset){
                    offset = 0;
                    lp1.top = top;
                    lp1.left = left;
                    top = top + child.getMeasuredHeight() + lp1.topMargin + lp1.bottomMargin;
                    left = getPaddingLeft();
                    availableWidth = measuredWidth - getPaddingRight() - getPaddingLeft();
                }else {
                    lp1.top = top;
                    lp1.left = left;
                    left = left + childWidth;
                    availableWidth = availableWidth - childWidth;
                }
            }else {
                View child = getChildAt(i);
                LayoutParams lp1 = (LayoutParams) child.getLayoutParams();
                int childMeasuredWidth = child.getMeasuredWidth();
                int childWidth = childMeasuredWidth + lp1.leftMargin + lp1.rightMargin;
                lp1.top = top;
                lp1.left = left;
                left = left + childWidth;
                availableWidth = availableWidth - childWidth;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for(int i=0;i<getChildCount();i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int left = lp.left + lp.leftMargin;
            int top = lp.top + lp.topMargin;
            int right = left + child.getMeasuredWidth();
            int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
    }



    public void setAdapter(MyAdapter adapter){
        removeAllViews();
        addDropDownArrow();
        myAdapter = adapter;
        int count = myAdapter.getTextViewCount();
        for(int i=0;i<count;i++){
            addView(myAdapter.getTextView(i));
        }
        requestLayout();
    }

    //添加箭头图片 index为0
    //位于最右
    private void addDropDownArrow(){
        ImageView dropDownArrow = new ImageView(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = dp2px(5);
        lp.rightMargin = dp2px(5);
        lp.topMargin = dp2px(5);
        lp.bottomMargin = dp2px(5);
        dropDownArrow.setLayoutParams(lp);
        dropDownArrow.setImageResource(R.drawable.ic_expand_less_black);
        addView(dropDownArrow);
    }

    //以下代码为了支持Margin和Gravity属性
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams{
        private int gravity = -1;
        public int left = Integer.MIN_VALUE;
        public int top = Integer.MIN_VALUE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.AutoSizeMultiLinearLayout);
            gravity = ta.getInt(R.styleable.AutoSizeMultiLinearLayout_layout_gravity, -1);
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            this(width, height,-1);
        }

        public LayoutParams(int width,int height,int gravity){
            super(width,height);
            this.gravity = gravity;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
