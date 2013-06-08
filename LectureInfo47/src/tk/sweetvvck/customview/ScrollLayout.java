package tk.sweetvvck.customview;


import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollLayout extends ViewGroup {

	private static final String TAG = "ScrollLayout";
	// 用于滑动的类
	private Scroller mScroller;
	// 用来跟踪触摸速度的类
	private VelocityTracker mVelocityTracker;
	// 当前的屏幕视图
	private int mCurScreen;
	// 默认的显示视图
	private int mDefaultScreen = 0;
	// 无事件的状态
	private static final int TOUCH_STATE_REST = 0;
	// 处于拖动的状态
	private static final int TOUCH_STATE_SCROLLING = 1;
	// 滑动的速度
	private static final int SNAP_VELOCITY = 600;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	// 用来处理立体效果的类
	private Camera mCamera;
	private Matrix mMatrix;
	// 旋转的角度
	private float angle = 100;

	public ScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	// 在构造器中初始化
	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);

		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

		mCamera = new Camera();
		mMatrix = new Matrix();
	}

	/*
	 * 
	 * 为子View指定位置
	 */
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.e(TAG, "onLayout");

		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();

			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	// 重写此方法用来计算高度和宽度
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.e(TAG, "onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// Exactly：width代表的是精确的尺寸
		// AT_MOST：width代表的是最大可获得的空间
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		// 得到多少页(子View)并设置他们的宽和高
		final int count = getChildCount();
		System.out.println("count----->" + count);
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * width, 0);
	}

	/*
	 * 当进行View滑动时，会导致当前的View无效，该函数的作用是对View进行重新绘制 调用drawScreen函数
	 */
	protected void dispatchDraw(Canvas canvas) {
		final long drawingTime = getDrawingTime();
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			drawScreen(canvas, i, drawingTime);
		}
	}

	/*
	 * 立体效果的实现函数 ,screen为哪一个子View
	 */
	public void drawScreen(Canvas canvas, int screen, long drawingTime) {
		// 得到当前子View的宽度
		final int width = getWidth();
		final int scrollWidth = screen * width;
		System.out.println("scrollWidth--->" + scrollWidth);
		final int scrollX = this.getScrollX();
		System.out.println("scrollX---->" + scrollX);
		if (scrollWidth > scrollX + width || scrollWidth + width < scrollX) {
			return;
		}
		final View child = getChildAt(screen);
		final int faceIndex = screen;
		final float currentDegree = getScrollX() * (angle / getMeasuredWidth());
		System.out.println("getMeasuredWidth---->" + getMeasuredWidth() + "getScrollX()----->" + getScrollX() + "currentDegree--->" + currentDegree);
		final float faceDegree = currentDegree - faceIndex * angle;
		System.out.println("faceDegree--->" + faceDegree);
		if (faceDegree > 90 || faceDegree < -90) {
			return;
		}
		final float centerX = (scrollWidth < scrollX) ? scrollWidth + width
				: scrollWidth;
		final float centerY = getHeight() / 2;
		final Camera camera = mCamera;
		final Matrix matrix = mMatrix;
		canvas.save();
		camera.save();
		camera.rotateY(-faceDegree);
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
		canvas.concat(matrix);
		drawChild(canvas, child, drawingTime);
		canvas.restore();
	}

	/**
	 * 根据目前的位置滚动到下一个视图位置.
	 */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		// 根据View的宽度以及滑动的值来判断是哪个View
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		System.out.println("destScreen---->" + destScreen);
		System.out.println("getScrollX()----->" + getScrollX());
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		System.out.println("whichScreen<------" + whichScreen);
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		System.out.println("whichScreen------>" + whichScreen);
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate(); // 重新布局
		}
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			System.out.println(mScroller.computeScrollOffset());
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker == null) {
			// 使用obtain方法得到VelocityTracker的一个对象
			mVelocityTracker = VelocityTracker.obtain();
		}
		// 将当前的触摸事件传递给VelocityTracker对象
		mVelocityTracker.addMovement(event);
		// 得到触摸事件的类型
		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG, "event down!");
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;

			scrollBy(deltaX, 0);
			break;

		case MotionEvent.ACTION_UP:
			Log.e(TAG, "event : up");
			// if (mTouchState == TOUCH_STATE_SCROLLING) {
			final VelocityTracker velocityTracker = mVelocityTracker;
			// 计算当前的速度
			velocityTracker.computeCurrentVelocity(1000);
			// 获得当前的速度
			int velocityX = (int) velocityTracker.getXVelocity();

			Log.e(TAG, "velocityX:" + velocityX);

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				Log.e(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				Log.e(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// }
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.e(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			if (xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;

			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return mTouchState != TOUCH_STATE_REST;
	}

}
