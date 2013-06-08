package tk.sweetvvck.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.fromserver.GetLectureData;
import tk.sweetvvck.lecture.Lecture;
import tk.sweetvvck.utils.Constant;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.HttpUtil;
import tk.sweetvvck.utils.LoginDialog;
import tk.sweetvvck.utils.ViewUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Set extends Activity {
	private static final int MESSAGETYPE_01 = 0x0001;
	private ListenLecture listenLecture = null;
	private LinearLayout userInfoLayout = null;
	private LinearLayout deleteFavoriteLayout = null;
	private LinearLayout changeLayout = null;
	private LinearLayout aboutUsLayout = null;
	private Button logoutButton = null;
	private Button startGuide = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
		checkSkin();
	}

	private void initView() {
		LoginDialog.LoadUserDate(Set.this);
		listenLecture = (ListenLecture) getApplicationContext();

		userInfoLayout = (LinearLayout) findViewById(R.id.user_info);
		deleteFavoriteLayout = (LinearLayout) findViewById(R.id.delete_favorite);
		changeLayout = (LinearLayout) findViewById(R.id.change_th);
		aboutUsLayout = (LinearLayout) findViewById(R.id.about_us);
		logoutButton = (Button) findViewById(R.id.zhuxiao);
		startGuide = (Button) findViewById(R.id.start_guide);
		checkSkin();
		setListener();
	}

	private void setListener() {
		userInfoLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showUserInfo(Set.this);
			}
		});

		changeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				System.out.println("换肤！");
				Intent intent = new Intent();
				intent.setClass(Set.this, SkinActivity.class);
				startActivity(intent);
			}
		});

		aboutUsLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Set.this, AboutUs.class);
				startActivity(intent);
			}
		});

		logoutButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				listenLecture.setLoginFlag(false);
				listenLecture.setUserName(null);
				logout(Set.this);
				Toast.makeText(listenLecture, "已退出登录", Toast.LENGTH_LONG)
						.show();
			}
		});

		deleteFavoriteLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(Set.this)
						.setTitle("清空收藏")
						// 设置标题
						.setMessage("您确定要清空收藏吗?")
						// 设置内容
						.setIcon(R.drawable.logo_mini)
						.setPositiveButton("确定", // 设置确定按钮
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										ViewUtil.initProgressDialog(
												Set.this,
												getApplicationContext()
														.getString(
																R.string.deleting));
										deleteAllFavoriteLecture();
									}
								})
						.setNeutralButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) { // 点击"退出"按钮之后推出程序
										return;
									}
								}).create(); // 创建按钮
				dialog.show(); // 显示对话框
			}
		});

		startGuide.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				LoginDialog.saveUseFlag(Set.this, true);
				LoginDialog.saveFromSetFlag(Set.this, true);
				Intent intent = new Intent();
				intent.setClass(Set.this, GuideView.class);
				startActivity(intent);
			}
		});
	}

	protected Handler deleteAllFavoriteLectureHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				// 接收handler传来的list
				long startTime = SystemClock.currentThreadTimeMillis();
				List<Lecture> list = (List<Lecture>) message.obj;
				if (list != null)
					if (!list.isEmpty()) {
						if (list.get(0) != null) {
							if (list.get(0).getSpeaker()
									.equals(Constant.LOGIN_SUCCESSFULLY)) {
								Toast.makeText(getApplication(), "清空成功！",
										Toast.LENGTH_LONG).show();

							} else {
								Toast.makeText(getApplication(), "您为收藏任何讲座",
										Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(getApplication(), "清空失败，请检查网络连接",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getApplication(), "清空失败，请检查网络连接",
								Toast.LENGTH_LONG).show();
					}
				else if (list == null) {
					Toast.makeText(getApplication(), "清空失败，请检查网络连接",
							Toast.LENGTH_LONG).show();
				}
				long endTime = SystemClock.currentThreadTimeMillis();
				if (2000 - (endTime - startTime) > 0)
					try {
						Thread.sleep(2000 - (endTime - startTime));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				ViewUtil.closeProgressDialog();
				break;
			}
		}
	};

	protected void deleteAllFavoriteLecture() {
		LoginDialog.LoadUserDate(Set.this);
		if (listenLecture.getLoginFlag()) {
			final String username = listenLecture.getUserName();
			new Thread() {
				public void run() {
					NameValuePair usernameNameValuePair = new BasicNameValuePair(
							"username", username);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(usernameNameValuePair);
					List<Lecture> list = GetLectureData.getLectureData(
							HttpUtil.DELETE_ALL_FAVOTITE_LECTURE,
							nameValuePairs);

					Message message = new Message();
					message.what = MESSAGETYPE_01;
					message.obj = list;
					deleteAllFavoriteLectureHandler.sendMessage(message);
				}
			}.start();
		} else {
			ViewUtil.closeProgressDialog();
			Toast.makeText(getApplication(), "您还未登录", Toast.LENGTH_LONG).show();
		}

	}

	private void logout(Context context) {
		// 载入配置文件
		SharedPreferences sp = context.getApplicationContext()
				.getSharedPreferences("tk.sweetvvck", 0);
		// 写入配置文件
		Editor spEd = sp.edit();
		spEd.putBoolean("isSave", false);
		spEd.putString("name", "");
		spEd.putString("password", "");

		spEd.commit();
	}

	private void showUserInfo(final Context context) {
		LoginDialog.LoadUserDate(Set.this);
		String username = listenLecture.getUserName();
		if (username == null) {
			username = "未登录";
			showUserInfoDialog(context, username, "登录");
		} else if (username != null && username.length() == 0) {
			username = "未登录";
			showUserInfoDialog(context, username, "登录");
		}

		else {
			showUserInfoDialog(context, username, "切换用户");
		}

	}

	private void showUserInfoDialog(final Context context, String username,
			String postiveButton) {
		// 创建对话框
		final AlertDialog dlg = new AlertDialog.Builder(context)
				.setTitle("用户信息")
				.setMessage("用户名：" + username)
				.setIcon(R.drawable.logo_mini)
				.setPositiveButton(postiveButton,
						new DialogInterface.OnClickListener() {// 设置监听事件
							public void onClick(DialogInterface dialog,
									int which) {
								LoginDialog.getInstance().login(context);
							}
						}).setNegativeButton("取消",// 设置取消按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).create();// 创建对话框
		dlg.show();// 显示对话框
		dlg.setCancelable(true);
		dlg.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				dlg.dismiss();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ViewUtil.onBackPressed_local(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void checkSkin() {
		switch (listenLecture.getSkinFlag()) {
		case Constant.BLUE:
			logoutButton.setBackgroundResource(R.drawable.btn_blue);
			startGuide.setBackgroundResource(R.drawable.btn_blue);
			break;
		case Constant.DARK_RED:
			logoutButton.setBackgroundResource(R.drawable.btn_dark_red);
			startGuide.setBackgroundResource(R.drawable.btn_dark_red);
			break;
		case Constant.RED:
			logoutButton.setBackgroundResource(R.drawable.btn_red);
			startGuide.setBackgroundResource(R.drawable.btn_red);
			break;
		case Constant.GRAY:
			logoutButton.setBackgroundResource(R.drawable.btn_gray);
			startGuide.setBackgroundResource(R.drawable.btn_gray);
			break;
		case Constant.GREEN:
			logoutButton.setBackgroundResource(R.drawable.btn_green);
			startGuide.setBackgroundResource(R.drawable.btn_green);
			break;
		default:
			logoutButton.setBackgroundResource(R.drawable.btn_blue);
			startGuide.setBackgroundResource(R.drawable.btn_blue);
			break;
		}
	}

	protected void onPause() {
		super.onPause();
		GFAgent.onPause(this);
	}
}
