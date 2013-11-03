package tk.sweetvvck.activities;

import tk.sweetvvck.R;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.customview.MyImageView;
import tk.sweetvvck.customview.MyProgressBar;
import tk.sweetvvck.customview.MyTextView;
import tk.sweetvvck.lecture.Lecture;
import tk.sweetvvck.utils.Constant;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.LoginDialog;
import tk.sweetvvck.utils.MoveLayout;
import tk.sweetvvck.utils.ViewUtil;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class LectureInfo extends ActivityGroup {

	private ListenLecture listenLecture = null;

	// 声明自定义ImageView和ProgressBar，将自定义的刷新按钮与刷新进度条通过intent传给下一个activity
	private MyProgressBar refreshBar = null;
	private MyImageView refresh = null;
	// bodylayout用于动态加载View
	private LinearLayout bodyLayout;
	// movelayout用于底部栏目的滑动效果
	private MoveLayout layout;
	private RelativeLayout bottomLayout;
	private TextView favorites, hot, host, upload, set;
	private MyTextView title;
	// 定义两个成员变量，接收intent传来的信息，用于验证和表示
	private Lecture lecture = null;
	private String hostTitle = null;
	private String searchTitle = null;
	private String searchContent = null;

	// 表示View的状态，
	private int stateFlag = Constant.STATE_FLAG_HOST;
	// 表示动态滑块当前的位置，当做透明滑块移动的起始位置
	private int position = Constant.POSITION_HOST;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 将activity加入到栈中，点击退出时全部退出程序
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 获得从其他Activity中传过来的各种信息
		Intent intent = getIntent();
		getInfoByIntent(intent);

		// 初始化ViewGroup
		initMainView();
		
		showView(stateFlag);

		if (stateFlag == Constant.STATE_FLAG_HOST)
			host.setBackgroundResource(R.drawable.selected05);
		if (stateFlag == Constant.STATE_FLAG_FAVORATE)
			favorites.setBackgroundResource(R.drawable.selected05);
		if (stateFlag == Constant.STATE_FLAG_UPLOAD) {
			upload.setBackgroundResource(R.drawable.selected05);
			position = Constant.POSITION_UPLOAD;
		}
		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkSkin();
	}

	/**
	 * 初始化四个上层布局以及Body布局
	 */
	public void initMainView() {
		listenLecture = (ListenLecture) getApplicationContext();
		// 刷新图标以及刷新进度条，通过intent传给相应的子View
		refresh = (MyImageView) findViewById(R.id.main_fresh);
		refreshBar = (MyProgressBar) findViewById(R.id.refresh_bar);

		layout = (MoveLayout) findViewById(R.id.layout_bottom);
		// bodyLayout 用于加载子View，显示在主界面
		bodyLayout = (LinearLayout) findViewById(R.id.layout_body);
		// 底部的用于切换的栏目
		bottomLayout = (RelativeLayout) findViewById(R.id.layout_parent);
		// 栏目的选项
		favorites = (TextView) findViewById(R.id.favorite);
		hot = (TextView) findViewById(R.id.hot);
		host = (TextView) findViewById(R.id.style);
		upload = (TextView) findViewById(R.id.upload);
		set = (TextView) findViewById(R.id.set);
		title = (MyTextView) findViewById(R.id.title);

		checkSkin();

	}

	/**
	 * 检测用户选择皮肤的颜色
	 */
	private void checkSkin() {
		switch (listenLecture.getSkinFlag()) {
		case Constant.BLUE:
			title.setBackgroundResource(R.drawable.titlebar_blue);
			layout.setBackgroundResource(R.drawable.titlebar_blue);
			break;
		case Constant.DARK_RED:
			title.setBackgroundResource(R.drawable.titlebar_dark_red);
			layout.setBackgroundResource(R.drawable.titlebar_dark_red);
			break;
		case Constant.RED:
			title.setBackgroundResource(R.drawable.titlebar_red);
			layout.setBackgroundResource(R.drawable.titlebar_red);
			break;
		case Constant.GRAY:
			title.setBackgroundResource(R.drawable.titlebar_gray);
			layout.setBackgroundResource(R.drawable.titlebar_gray);
			break;
		case Constant.GREEN:
			title.setBackgroundResource(R.drawable.titlebar_green);
			layout.setBackgroundResource(R.drawable.titlebar_green);
			break;
		default:
			title.setBackgroundResource(R.drawable.titlebar_blue);
			layout.setBackgroundResource(R.drawable.titlebar_blue);
			break;
		}
	}

	/**
	 * 根据flag来选择展示的View
	 * 
	 * @param flag
	 * @throws InterruptedException
	 */
	public void showView(int flag) {
		host.setBackgroundResource(R.drawable.frame_button_nopressbg);
		favorites.setBackgroundResource(R.drawable.frame_button_nopressbg);
		upload.setBackgroundResource(R.drawable.frame_button_nopressbg);
		switch (flag) {
		case Constant.STATE_FLAG_FAVORATE:
			refresh.setVisibility(View.GONE);
			title.setText("收藏");
			// Call this method to remove all child views from the ViewGroup
			bodyLayout.removeAllViews();
			bodyLayout.addView(getLocalActivityManager().startActivity(
					"one",
					new Intent(LectureInfo.this, Favorite.class).putExtra(
							"refresh", refresh).putExtra("refreshBar",
							refreshBar)).getDecorView());

			// 相应栏目点击后的效果，字体变大
			ViewUtil.setPressBackGround(favorites, hot, host, upload, set);
			break;
		case Constant.STATE_FLAG_HOT:
			refresh.setVisibility(View.VISIBLE);
			title.setText("热门讲座");
			bodyLayout.removeAllViews();
			bodyLayout.addView(getLocalActivityManager().startActivity(
					"two",
					new Intent(LectureInfo.this, Hot.class).putExtra("refresh",
							refresh).putExtra("refreshBar", refreshBar))
					.getDecorView());
			ViewUtil.setPressBackGround(hot, favorites, host, upload, set);
			break;
		case Constant.STATE_FLAG_HOST:
			refresh.setVisibility(View.GONE);
			title.setText("校园");
			bodyLayout.removeAllViews();
			ViewUtil.setPressBackGround(host, favorites, hot, upload, set);
			bodyLayout.addView(getLocalActivityManager().startActivity("three",
					new Intent(LectureInfo.this, Host.class)).getDecorView());
			break;
		case Constant.STATE_FLAG_UPLOAD:
			refresh.setVisibility(View.GONE);
			title.setText("发布");
			bodyLayout.removeAllViews();
			bodyLayout.addView(getLocalActivityManager().startActivity("four",
					new Intent(LectureInfo.this, Upload.class)).getDecorView());
			ViewUtil.setPressBackGround(upload, favorites, hot, host, set);
			break;
		case Constant.STATE_FLAG_DETAIL_INFO:
			refresh.setVisibility(View.GONE);
			title.setText("讲座信息");
			bodyLayout.removeAllViews();
			bodyLayout.addView(getLocalActivityManager().startActivity(
					"five",
					new Intent(LectureInfo.this, DetailInfo.class).putExtra(
							"lecture", lecture)).getDecorView());
			bottomLayout.removeView(findViewById(R.id.layout_bottom));
			break;
		case Constant.STATE_FLAG_HOST_INFO:
			refresh.setVisibility(View.VISIBLE);
			if (hostTitle != null)
				title.setText(hostTitle);
			else if (searchTitle != null)
				title.setText(searchTitle);
			bodyLayout.removeAllViews();
			bodyLayout.addView(getLocalActivityManager().startActivity(
					"three",
					new Intent(LectureInfo.this, Hot.class)
							.putExtra("refresh", refresh)
							.putExtra("refreshBar", refreshBar)
							.putExtra("hostTitle", hostTitle)
							.putExtra("searchContent", searchContent))
					.getDecorView());
			ViewUtil.setPressBackGround(host, favorites, hot, upload, set);
			position = 2;
			bottomLayout.removeView(findViewById(R.id.layout_bottom));
			break;
		case Constant.STATE_FLAG_SET:
			refresh.setVisibility(View.GONE);
			title.setText("设置");
			bodyLayout.removeAllViews();
			bodyLayout.addView(getLocalActivityManager().startActivity(
					"four",
					new Intent(LectureInfo.this, Set.class).putExtra("title",
							title).putExtra("moveLayout", layout))
					.getDecorView());
			ViewUtil.setPressBackGround(set, favorites, hot, host, upload);
			break;

		default:
			break;
		}
	}

	/**
	 * 重写点击back键，效果为弹出对话框提示是否退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (stateFlag != 4 && stateFlag != 5) {
				System.out.println("----------->stateFlag : " + stateFlag);
				System.out.println(" I am in LectureInfo !!");
				if (stateFlag == 1)
					return false;
				else
					ViewUtil.onBackPressed_local(this);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getInfoByIntent(Intent intent) {
		// 从Host中传来的Lecture和学校名称以及要显示View，即bodylayout要加载的View
		lecture = (Lecture) intent.getSerializableExtra("lecture");
		hostTitle = intent.getStringExtra("hostTitle");
		searchTitle = intent.getStringExtra("searchTitle");
		searchContent = intent.getStringExtra("searchContent");
		stateFlag = intent.getIntExtra("stateFlag", Constant.STATE_FLAG_HOST); // 其期望值是STATE_FLAG_DETAIL_INTO
																				// 即4
		position = intent.getIntExtra("position", 0); // 告诉MainView虽然跳转了activity，但当前页面还是校园的子页面，当前的position是
														// POSITION_HOST,即0
		// 判断是从校园进入详细信息的还是从一周讲座进入详细信息的
		if ((stateFlag == Constant.STATE_FLAG_DETAIL_INFO && intent
				.getStringExtra("host") != null)
				|| stateFlag == Constant.STATE_FLAG_HOST) {
			position = Constant.POSITION_HOST;
		} else
			position = Constant.POSITION_HOT;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "退出应用").setIcon(R.drawable.exit_menu_icon);// .setIcon(R.drawable.info)
		menu.add(0, 1, 0, "切换用户").setIcon(R.drawable.menu_logout);
		// setMenuBackground();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			ExitApplication.getInstance().exit();
		case 1:
			LoginDialog.getInstance().login(LectureInfo.this);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 设置各个栏目点击的监听器
	 * 
	 * @author sweetvvck
	 */
	private void setListener() {
		favorites.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LoginDialog.LoadUserDate(LectureInfo.this);
				if (!listenLecture.getLoginFlag()) {
					LoginDialog.getInstance().login(LectureInfo.this);
				}
				if (listenLecture.getLoginFlag()) {
					// 透明滑块移到该处
					ViewUtil.startMove(v, position, layout);
					position = Constant.POSITION_FAVORATE;
					stateFlag = Constant.STATE_FLAG_FAVORATE;
					LoginDialog.LoadUserDate(LectureInfo.this);
					showView(stateFlag);
				}
			}
		});

		hot.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ViewUtil.startMove(v, position, layout);
				position = Constant.POSITION_HOT;
				stateFlag = Constant.STATE_FLAG_HOT;
				showView(stateFlag);
			}
		});

		host.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ViewUtil.startMove(v, position, layout);
				position = Constant.POSITION_HOST;
				stateFlag = Constant.STATE_FLAG_HOST;
				showView(stateFlag);
			}
		});

		upload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ViewUtil.startMove(v, position, layout);
				position = Constant.POSITION_UPLOAD;
				stateFlag = Constant.STATE_FLAG_UPLOAD;
				showView(stateFlag);
			}
		});

		set.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ViewUtil.startMove(v, position, layout);
				position = Constant.POSITION_SET;
				stateFlag = Constant.STATE_FLAG_SET;
				showView(stateFlag);
			}
		});
	}
}