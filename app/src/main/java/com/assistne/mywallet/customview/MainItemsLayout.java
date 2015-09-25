package com.assistne.mywallet.customview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.assistne.mywallet.R;

/**
 * Created by Flavien Laurent (flavienlaurent.com) on 23/08/13.
 */
public class MainItemsLayout extends LinearLayout {

	public static final String LOG_TAG = "test drag span";
	private final ViewDragHelper mDragHelper;

	private View mTitle;
    private View mItemsField;
    private ImageView mTitleArrow;

	private float mInitialMotionX;
	private float mInitialMotionY;

	private int mDragRange;
    private int mTop;
	private float mDragOffset;
    

    public MainItemsLayout(Context context) {
		this(context, null);
	}

	public MainItemsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MainItemsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
	}

    @Override
    protected void onFinishInflate() {
		super.onFinishInflate();
        mTitle = findViewById(R.id.home_span_bill_title);
        mItemsField = findViewById(R.id.home_span_bill_content);
        mTitleArrow = (ImageView)findViewById(R.id.home_ic_bill_title_arrow);
        mTop = getPaddingTop() + mItemsField.getHeight();
    }

    public void maximize() {
        smoothSlideTo(0f);
    }

    public void minimize() {
        smoothSlideTo(1f);
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * mDragRange);
        if (mDragHelper.smoothSlideViewTo(mTitle, mTitle.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
            return child == mTitle;
		}

        @Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			mTop = top;
			mDragOffset = (float) (top - getPaddingTop()) / mDragRange;
            requestLayout();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			int top = getPaddingTop();
			if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
				top += mDragRange;
			}
            if (top == getPaddingTop()){
                mTitleArrow.setBackgroundResource(R.drawable.home_bill_title_icon_);
            } else {
                mTitleArrow.setBackgroundResource(R.drawable.home_bill_title_icon);
            }
			mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
			invalidate();
		}

		@Override
		public int getViewVerticalDragRange(View child) {
			return mDragRange;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			final int topBound = getPaddingTop();
			final int bottomBound = getHeight() - mTitle.getHeight() - mTitle.getPaddingBottom();

			return Math.min(Math.max(top, topBound), bottomBound);
		}

	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(LOG_TAG, "on intercept touch event");
		final int action = MotionEventCompat.getActionMasked(ev);

		if (( action != MotionEvent.ACTION_DOWN)) {
			mDragHelper.cancel();
			return super.onInterceptTouchEvent(ev);
		}

		final float x = ev.getX();
		final float y = ev.getY();
		boolean interceptTap = false;

		switch (action) {
			case MotionEvent.ACTION_DOWN: {
                Log.d(LOG_TAG, "action down");
				mInitialMotionX = x;
				mInitialMotionY = y;
                interceptTap = mDragHelper.isViewUnder(mTitle, (int) x, (int) y);
				break;
			}
            default:
                break;
		}

		return mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
        Log.d(LOG_TAG, "on touch event");
		mDragHelper.processTouchEvent(ev);

		final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        boolean isHeaderViewUnder = mDragHelper.isViewUnder(mTitle, (int) x, (int) y);
        switch (action & MotionEventCompat.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
                Log.d(LOG_TAG, "on touch action down");
				mInitialMotionX = x;
				mInitialMotionY = y;
				break;
			}

			case MotionEvent.ACTION_UP: {
                Log.d(LOG_TAG, "on touch action up");
				final float dx = x - mInitialMotionX;
				final float dy = y - mInitialMotionY;
				final int slop = mDragHelper.getTouchSlop();
				if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder) {
					if (mDragOffset == 0) {
                        mTitleArrow.setBackgroundResource(R.drawable.home_bill_title_icon);
						smoothSlideTo(1f);
					} else {
                        mTitleArrow.setBackgroundResource(R.drawable.home_bill_title_icon_);
						smoothSlideTo(0f);
					}
				}
				break;
			}
		}

		return isHeaderViewUnder && isViewHit(mTitle, (int) x, (int) y) || isViewHit(mItemsField, (int) x, (int) y);
	}


    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mDragRange = getHeight() - mTitle.getHeight() - getPaddingTop();
        Log.d(LOG_TAG, "onLayout" + getHeight() + "  " + mTitle.getHeight() +  "  " + getPaddingTop());
        mTitle.layout(
                0,
                mTop,
                r,
                mTop + mTitle.getMeasuredHeight());

        mItemsField.layout(
                0,
                mTop + mTitle.getMeasuredHeight(),
                r,
                mTop  + b);
	}


}
