package tk.sweetvvck.utils;

import java.io.Serializable;

import tk.sweetvvck.R;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MoveLayout extends LinearLayout implements Serializable{

	private static final long serialVersionUID = 6007410702804543471L;
	private static double SPEED = 16;
	//规定移动时间为6次
	private static final int MOVE_TIME = 6;

	private Context mContext;
	private Rect mNowRect;// 当前的区域
	private Rect mEndRect;// 结束的区域
	private BitmapDrawable mSelecter;// 移动的半透明背景bitmaip
	private boolean mSyn = false;// 循环和onDraw同步
	public boolean mIsStop = false;// 是否到达指定区域

	public MoveLayout(Context context) {
		super(context);
		init(context);
	}

	public MoveLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		mSelecter = new BitmapDrawable(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.selected05));
		mNowRect = new Rect();

		mEndRect = new Rect();
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
	}

	/**
	 * 
	 * @param v 目标控件
	 * @param position滑块当前的位置
	 * @author sweetvvck
	 */
	public void doWork(View v, int position) {
		//通过position获得滑块当前的位置，并以此获得滑块的矩形四个顶点坐标
		this.getChildAt(position).getHitRect(mNowRect);
		v.getHitRect(this.mEndRect);

		if (this.mNowRect.right < this.mEndRect.right) {
			//移动的时间不变，移动速度可变化，通过移动距离计算
			SPEED = (short) ((mEndRect.right - mNowRect.right)/MOVE_TIME);
			work(new RunForword() {
				public void run() {					
					mNowRect.left += SPEED;
					mNowRect.right += SPEED;
					if (mNowRect.right >= mEndRect.right)// 如果移动超出或等于目标区域
						ReachRect();
				}
			});
		} else if (this.mNowRect.right > this.mEndRect.right) {
			SPEED = (short) ((mNowRect.right - mEndRect.right)/MOVE_TIME);
			work(new RunForword() {
				public void run() {					
					mNowRect.left -= SPEED;
					mNowRect.right -= SPEED;

					if (mNowRect.right <= mEndRect.right)// 如果移动超出或等于目标区域
						ReachRect();
				}
			});
		}
	}

	private void work(RunForword run) {
		this.mIsStop = false;
		while (!this.mIsStop) {
			if (this.mSyn)// 画图与循环同步
			{
				run.run();
				this.mSyn = false;
				this.postInvalidate();
			}
		}
	}

	/**
	 * 到达目的地
	 */
	private void ReachRect() {
		mNowRect.left = mEndRect.left;
		mNowRect.right = mEndRect.right;
		mIsStop = true;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mSelecter.setBounds(mNowRect);
		mSelecter.draw(canvas);
		this.mSyn = true;
	}

	public interface RunForword {
		void run();
	}
}