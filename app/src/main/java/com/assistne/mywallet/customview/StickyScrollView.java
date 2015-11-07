//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.assistne.mywallet.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ScrollView;

import com.assistne.mywallet.R;

import java.util.ArrayList;
import java.util.Iterator;

public class StickyScrollView extends ScrollView {
    public static final String TAG = "Sticky Scroll View";
//    标志位，判定是否固定
    public static final String STICKY_TAG = "sticky";
//    标志位，判定固定期间状态是否发生变化
    public static final String FLAG_NON_CONSTANT = "-nonconstant";
//    标志位，判定是否透明
    public static final String FLAG_HAS_TRANSPARENCY = "-hastransparency";
//    默认值，阴影高度(dp)
    private static final int DEFAULT_SHADOW_HEIGHT = 10;
//    包含sticky的View集合
    private ArrayList<View> stickyViews;
//    当前固定的View
    private View currentlyStickingView;
    private float stickyViewTopOffset;
    private int stickyViewLeftOffset;
    private boolean redirectTouchesToStickyView;
    private boolean clippingToPadding;
    private boolean clipToPaddingHasBeenSet;
//    阴影高度
    private int mShadowHeight;
//    阴影使用的Drawable
    private Drawable mShadowDrawable;
    private final Runnable invalidateRunnable;

//    构造函数用于代码中创建实例-> new StickyScrollView(this);
    public StickyScrollView(Context context) {
        this(context, null);
    }
//    xml文件中创建实例
    public StickyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842880);
//        Log.d(TAG, "construct by xml");
    }

    public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        当设置为nonconstant的时候不断重绘stickyView来实现View状态的变化显示
        this.invalidateRunnable = new Runnable() {
            public void run() {
                if(StickyScrollView.this.currentlyStickingView != null) {
//                    获取当前固定View相对于最外层ViewGroup(ScrollView的子View)的矩形区域
                    int l = StickyScrollView.this.getLeftForViewRelativeOnlyChild(StickyScrollView.this.currentlyStickingView);
                    int t = StickyScrollView.this.getBottomForViewRelativeOnlyChild(StickyScrollView.this.currentlyStickingView);
                    int r = StickyScrollView.this.getRightForViewRelativeOnlyChild(StickyScrollView.this.currentlyStickingView);
                    int b = (int)((float)StickyScrollView.this.getScrollY() + (float)StickyScrollView.this.currentlyStickingView.getHeight() + StickyScrollView.this.stickyViewTopOffset);
//                    重绘矩形区域
                    StickyScrollView.this.invalidate(l, t, r, b);
                }

                StickyScrollView.this.postDelayed(this, 16L);
            }
        };

        this.setup();//  实例化stickyViews(ArrayList)
//        获取xml中设置的值
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickyScrollView, defStyle, 0);
//        获取阴影高度，默认值为10dp
        float density = context.getResources().getDisplayMetrics().density;
        int defaultShadowHeightInPix = (int)(DEFAULT_SHADOW_HEIGHT * density + 0.5F);
        this.mShadowHeight = a.getDimensionPixelSize(0, defaultShadowHeightInPix);
//        获取阴影使用的Drawable Resource ID
        int shadowDrawableRes = a.getResourceId(1, -1);
        if(shadowDrawableRes != -1) {
            this.mShadowDrawable = context.getResources().getDrawable(shadowDrawableRes);
        }
//        回收TypedArray
        a.recycle();
    }

    public void setShadowHeight(int height) {
        this.mShadowHeight = height;
    }

    public void setup() {
        this.stickyViews = new ArrayList();
    }

    private int getLeftForViewRelativeOnlyChild(View v) {
        int left;
        for(left = v.getLeft(); v.getParent() != this.getChildAt(0); left += v.getLeft()) {
            v = (View)v.getParent();
        }

        return left;
    }

    private int getTopForViewRelativeOnlyChild(View v) {
        int top;
        for(top = v.getTop(); v.getParent() != this.getChildAt(0); top += v.getTop()) {
            v = (View)v.getParent();
        }

        return top;
    }

    private int getRightForViewRelativeOnlyChild(View v) {
        int right;
        for(right = v.getRight(); v.getParent() != this.getChildAt(0); right += v.getRight()) {
            v = (View)v.getParent();
        }

        return right;
    }

    private int getBottomForViewRelativeOnlyChild(View v) {
        int bottom;
        for(bottom = v.getBottom(); v.getParent() != this.getChildAt(0); bottom += v.getBottom()) {
            v = (View)v.getParent();
        }

        return bottom;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        Log.d(TAG, "on layout");
        if(!this.clipToPaddingHasBeenSet) {
//          默认设置为不能在Padding绘制
           this.clippingToPadding = true;
        }
//        Log.d(TAG, "clipping to padding " + clippingToPadding);
        this.notifyHierarchyChanged();
    }

//    设置是否允许在padding区域绘制
    public void setClipToPadding(boolean clipToPadding) {
        super.setClipToPadding(clipToPadding);
//        Log.d(TAG, "set clip to padding " + clipToPadding);
        this.clippingToPadding = clipToPadding;
        this.clipToPaddingHasBeenSet = true;
    }

    public void addView(View child) {
        super.addView(child);
        this.findStickyViews(child);
    }

    public void addView(View child, int index) {
        super.addView(child, index);
        this.findStickyViews(child);
    }

    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        this.findStickyViews(child);
    }

    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        this.findStickyViews(child);
    }

    public void addView(View child, LayoutParams params) {
        super.addView(child, params);
        this.findStickyViews(child);
    }

//    在draw()中自身绘制完但子View还未开始绘制时被调用
    protected void dispatchDraw(Canvas canvas) {
//        Log.d(TAG, "dispatchDraw method");
        super.dispatchDraw(canvas);
        if(this.currentlyStickingView != null) {
//            保存当前canvas的状态
            canvas.save();
//            canvas的大小即是ScrollView的大小
//            移动canvas左边和顶边至stickyView位置
//            Log.d(TAG, "getScrollY : " + pixelToDp(getScrollY()));
            canvas.translate(
//                    stickyView左边相对于scrollView左边等于padding加上stickyView相对scrollView子View左边的距离
                    (float) (this.getPaddingLeft() + this.stickyViewLeftOffset),
//                    canvas顶边会随着scrollView移动向上移动
                    (float) getScrollY() + this.stickyViewTopOffset + (float) (this.clippingToPadding ? this.getPaddingTop() : 0));

            canvas.clipRect(0.0F,
                    this.clippingToPadding ? -this.stickyViewTopOffset : 0.0F,
                    (float) (this.getWidth() - this.stickyViewLeftOffset),
                    (float) (this.currentlyStickingView.getHeight() + this.mShadowHeight + 1));
            if(this.mShadowDrawable != null) {
                byte left = 0;
                int right = this.currentlyStickingView.getWidth();
                int top = this.currentlyStickingView.getHeight();
                int bottom = this.currentlyStickingView.getHeight() + this.mShadowHeight;
                this.mShadowDrawable.setBounds(left, top, right, bottom);
                this.mShadowDrawable.draw(canvas);
            }

            canvas.clipRect(0.0F, this.clippingToPadding?-this.stickyViewTopOffset:0.0F, (float)this.getWidth(), (float)this.currentlyStickingView.getHeight());
            if(this.getStringTagForView(this.currentlyStickingView).contains(FLAG_HAS_TRANSPARENCY)) {
                this.showView(this.currentlyStickingView);
                this.currentlyStickingView.draw(canvas);
                this.hideView(this.currentlyStickingView);
            } else {
                this.currentlyStickingView.draw(canvas);
            }
//            回滚到Canvas.save()时的状态
            canvas.restore();
        }

    }

    private float pixelToDp(float pixel) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return pixel/density;
    }

//    点击事件的入口
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.redirectTouchesToStickyView = false;
//        点击时运行
//        是否重定向点击时间到stickyView默认值为false
//        若点击事件不成立或者当前没有stickyView则不需要重定向
        if(ev.getAction() == MotionEvent.ACTION_DOWN && currentlyStickingView != null) {
//            点击位置是否在stickyView底边的上方
            boolean aboveStickyBottom = ev.getY() <= (float)this.currentlyStickingView.getHeight() + this.stickyViewTopOffset;
//            点击位置是否在stickyView左边的右方
            boolean rightOfStickyLeft = ev.getX() >= (float)this.getLeftForViewRelativeOnlyChild(this.currentlyStickingView);
//            点击位置是否在stickyView右边的左方
            boolean leftOfStickyRight = ev.getX() <= (float)this.getRightForViewRelativeOnlyChild(this.currentlyStickingView);
//            即使点击ScrollView顶部的Padding（如果有）区域也认为点击在StickyView上
            this.redirectTouchesToStickyView = aboveStickyBottom && rightOfStickyLeft && leftOfStickyRight;
        }
//        重定向点击位置，向上移动到stickyView的真实位置上（超出可见范围）
        if(this.redirectTouchesToStickyView) {
            ev.offsetLocation(
                    0.0F,
                    -1.0F * ((float) this.getScrollY() + this.stickyViewTopOffset - (float) this.getTopForViewRelativeOnlyChild(this.currentlyStickingView)));
        }

        return super.dispatchTouchEvent(ev);
    }

//    当没有View响应点击事件时执行该方法
    public boolean onTouchEvent(MotionEvent ev) {
        if(this.redirectTouchesToStickyView) {
//            取消重定向点击事件位置
            ev.offsetLocation(
                    0.0F,
                    (float)this.getScrollY() + this.stickyViewTopOffset - (float)this.getTopForViewRelativeOnlyChild(this.currentlyStickingView));
        }

        return super.onTouchEvent(ev);
    }

//    ScrollView滚动时执行
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        this.doTheStickyThing();
    }

//    确定stickyView
    private void doTheStickyThing() {
        View viewThatShouldStick = null;
        View approachingView = null;
        Iterator i$ = this.stickyViews.iterator();

        while(true) {
            View v;
            int viewTop;
            label67:
            do {
                while(i$.hasNext()) {
                    v = (View)i$.next();
//
                    viewTop = (int)getViewTopFromPortTop(v);
//                    Log.d(TAG, "view " + v.toString() + "  top:" + viewTop);
                    if(viewTop <= 0) {
                        continue label67;
                    }
                    if(approachingView == null || viewTop < getViewTopFromPortTop(approachingView)) {
//                        获取在可见区域顶部下方中最接近可见区域顶部的stickyView
                        approachingView = v;
                    }
                }

//                Log.d(TAG, "approachingView is " + approachingView.toString());

                if(viewThatShouldStick != null) {
//                    Log.d(TAG, "viewThatShouldStick is not null :" + viewThatShouldStick.toString());
                    this.stickyViewTopOffset = approachingView == null?0.0F:Math.min(0, getViewTopFromPortTop(approachingView) - viewThatShouldStick.getHeight());
                    if(viewThatShouldStick != this.currentlyStickingView) {
                        if(this.currentlyStickingView != null) {
                            this.stopStickingCurrentlyStickingView();
                        }

                        this.stickyViewLeftOffset = this.getLeftForViewRelativeOnlyChild(viewThatShouldStick);
                        this.startStickingView(viewThatShouldStick);
                    }
                } else if(this.currentlyStickingView != null) {
//                    Log.d(TAG, "currentlyStickingView is not null");
                    this.stopStickingCurrentlyStickingView();
                }

                return;
            } while(viewThatShouldStick != null && viewTop <= getViewTopFromPortTop(viewThatShouldStick));
//            Log.d(TAG, "out of do-while clause");
            viewThatShouldStick = v;
//            Log.d(TAG, "viewThatShouldStick is :" + viewThatShouldStick.toString());
        }
    }

//    获取View顶部相对ScrollView可见区域的距离
    private float getViewTopFromPortTop(View v) {
//        getTop...OnlyChild()->求控件相对ScrollView子控件顶部的位置，固定值
//        getScrollY()->求ScrollView当前可见区域顶部在整个ScrollView范围的位置，滚动时变化
//        viewTop->控件相对于可见区域顶部的位置，负值说明控件在可见区域上方，必不可见，正值说明在可见区域顶部的下方，可能可见，可能不可见，取决该值是否大于可见区域的大小
        return this.getTopForViewRelativeOnlyChild(v) - this.getScrollY() + (this.clippingToPadding?0:this.getPaddingTop());
    }

    private void startStickingView(View viewThatShouldStick) {
        this.currentlyStickingView = viewThatShouldStick;
//        tag包含hastransparancy则把原来的View隐藏掉
        if(this.getStringTagForView(this.currentlyStickingView).contains(FLAG_HAS_TRANSPARENCY)) {
            this.hideView(this.currentlyStickingView);
        }
//        tag包含nonconstant则不断重绘冻结的区域
        if(((String)this.currentlyStickingView.getTag()).contains(FLAG_NON_CONSTANT)) {
            this.post(this.invalidateRunnable);
        }

    }

    private void stopStickingCurrentlyStickingView() {
        if(this.getStringTagForView(this.currentlyStickingView).contains(FLAG_HAS_TRANSPARENCY)) {
            this.showView(this.currentlyStickingView);
        }

        this.currentlyStickingView = null;
        this.removeCallbacks(this.invalidateRunnable);
    }

    public void notifyStickyAttributeChanged() {
        this.notifyHierarchyChanged();
    }

    private void notifyHierarchyChanged() {
        if(this.currentlyStickingView != null) {
            this.stopStickingCurrentlyStickingView();
        }

        this.stickyViews.clear();
        this.findStickyViews(this.getChildAt(0));
        this.doTheStickyThing();
        this.invalidate();
    }

//    寻找tag包含sticky的View,添加到stickyViews(ArrayList)中
    private void findStickyViews(View v) {
//        如果是ViewGroup
        if(v instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)v;
//            遍历子View
            for(int i = 0; i < viewGroup.getChildCount(); ++i) {
                View childView = viewGroup.getChildAt(i);
                if(isSticky(childView)) {
//                    tag包含sticky则添加到stickyViews
                    this.stickyViews.add(childView);
                } else if(childView instanceof ViewGroup) {
//                    tag不包含sticky但是改子View是ViewGroup则遍历该子View
                    this.findStickyViews(childView);
                }
            }
        } else {
//            如果是View
            if(isSticky(v)) {
                this.stickyViews.add(v);
            }
        }

    }

    private boolean isSticky(View v) {
        return getStringTagForView(v).contains(STICKY_TAG);
    }

    private String getStringTagForView(View v) {
        Object tagObject = v.getTag();
        return String.valueOf(tagObject);
    }

    private void hideView(View v) {
        if(VERSION.SDK_INT >= 11) {
            v.setAlpha(0.0F);
        } else {
            AlphaAnimation anim = new AlphaAnimation(1.0F, 0.0F);
            anim.setDuration(0L);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }

    }

    private void showView(View v) {
        if(VERSION.SDK_INT >= 11) {
            v.setAlpha(1.0F);
        } else {
            AlphaAnimation anim = new AlphaAnimation(0.0F, 1.0F);
            anim.setDuration(0L);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }

    }
}
