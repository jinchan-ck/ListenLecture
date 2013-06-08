package tk.sweetvvck.activities;

import java.util.ArrayList;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.LoginDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class GuideView extends Activity {

	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ViewGroup guide, group;
	private ImageView imageView;
	private ImageView[] imageViews;
	private TextView start = null;
	private ListenLecture listenLecture = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		listenLecture = (ListenLecture) this.getApplicationContext();
		LoginDialog.LoadUserDate(this);
		if (!listenLecture.isFirstUse()) {
			Intent intent = new Intent();
			intent.setClass(GuideView.this, LectureInfo.class);
			startActivity(intent);
		}
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LoginDialog.saveUseFlag(this, false);
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		FrameLayout last = (FrameLayout) inflater
				.inflate(R.layout.item05, null);
		pageViews.add(inflater.inflate(R.layout.item01, null));
		pageViews.add(inflater.inflate(R.layout.item02, null));
		pageViews.add(inflater.inflate(R.layout.item03, null));
		pageViews.add(inflater.inflate(R.layout.item04, null));
		pageViews.add(last);

		imageViews = new ImageView[pageViews.size()];
		guide = (ViewGroup) inflater.inflate(R.layout.guide, null);

		// group是R.layou.main中的负责包裹小圆点的LinearLayout.
		group = (ViewGroup) guide.findViewById(R.id.viewGroup);

		viewPager = (ViewPager)

		guide.findViewById(R.id.guidePages);

		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(GuideView.this);

			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			imageViews[i] = imageView;
			if (i ==

			0) {
				// 默认选中第一张图片
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {

				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}
			group.addView(imageViews[i]);
		}

		setContentView(guide);

		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
		start = (TextView) last.findViewById(R.id.guide_start);
		start.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GuideView.this, LectureInfo.class);
				startActivity(intent);
			}
		});
	}

	/** 指引页面Adapter */

	class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int

		getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView

			(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub

			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

		@Override
		public void restoreState

		(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {

			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	/** 指引页面改监听器 */
	class GuidePageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		public void onPageScrolled(int arg0,

		float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		public void onPageSelected(int arg0) {

			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0]
						.setBackgroundResource(R.drawable.page_indicator_focused);

				if (arg0 != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.page_indicator);
				}

			}

		}

	}

	/**
	 * 重写点击back键，效果为弹出对话框提示是否退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LoginDialog.LoadUserDate(this);
			System.out.println(listenLecture.isFromSet());
			if (listenLecture.isFromSet())
				finish();
			else
				return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		GFAgent.onPause(this);
	}
}