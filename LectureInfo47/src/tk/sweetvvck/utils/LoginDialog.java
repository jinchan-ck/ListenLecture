package tk.sweetvvck.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import tk.sweetvvck.R;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.fromserver.GetLectureData;
import tk.sweetvvck.lecture.Lecture;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginDialog {
	private static ListenLecture listenLecture = null;
	private static final String PREFS_NAME = "tk.sweetvvck";
	private static final int MESSAGETYPE_01 = 0x0001;

	private static EditText loginUsername = null;
	private static EditText loginPassword = null;
	private static LoginDialog loginDialog = null;

	private LoginDialog() {
	}

	public static LoginDialog getInstance() {
		loginDialog = new LoginDialog();
		return loginDialog;
	}

	public void login(Context context) {
		listenLecture = (ListenLecture) context.getApplicationContext();
		LayoutInflater factory = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 得到自定义对话框
		View dialogView = factory.inflate(R.layout.dialog, null);
		loginUsername = (EditText) dialogView.findViewById(R.id.login_username);
		loginPassword = (EditText) dialogView.findViewById(R.id.login_password);
		showLoginDialog(dialogView, context);
	}

	private void showLoginDialog(final View dialogView, final Context context) {
		// 创建对话框
		final AlertDialog dlg = new AlertDialog.Builder(context).setView(dialogView)// 设置自定义对话框样式
				.create();// 创建对话框
		dlg.setView(dialogView, 0, 0, 0, 0);
		dlg.show();// 显示对话框
		dlg.findViewById(R.id.dialog_button_login).setOnClickListener(
				new android.view.View.OnClickListener() {

					public void onClick(View v) {
						loginResponse(context);
						dlg.dismiss();
					}
				});
		dlg.findViewById(R.id.dialog_button_regist).setOnClickListener(
				new android.view.View.OnClickListener() {

					public void onClick(View v) {
						registerResponse(context);
						dlg.dismiss();
					}
				});
		dlg.setCancelable(true);
		dlg.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				ViewUtil.closeProgressDialog();
			}
		});
	}

	/**
	 * 保存用户信息
	 */
	private static void saveUserDate(Context context) {
		// 载入配置文件
		SharedPreferences sp = context.getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		// 写入配置文件
		Editor spEd = sp.edit();
		spEd.putBoolean("isSave", true);
		spEd.putString("name", loginUsername.getText().toString());
		spEd.putString("password", loginPassword.getText().toString());
		spEd.commit();
	}

	public static void saveSkinColor(Context context, int skinFlag) {
		// 载入配置文件
		SharedPreferences sp = context.getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		// 写入配置文件
		Editor spEd = sp.edit();
		spEd.putBoolean("isSave", true);
		spEd.putInt("skinColor", listenLecture.getSkinFlag());
		spEd.commit();
	}

	public static void saveUseFlag(Context context, boolean isFirstUse) {
		// 载入配置文件
		SharedPreferences sp = context.getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		// 写入配置文件
		Editor spEd = sp.edit();
		spEd.putBoolean("isSave", true);
		spEd.putBoolean("isFirstUse", isFirstUse);
		spEd.commit();
	}

	public static void saveFromSetFlag(Context context, boolean isFromSet) {
		// 载入配置文件
		SharedPreferences sp = context.getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		// 写入配置文件
		Editor spEd = sp.edit();
		spEd.putBoolean("isSave", true);
		spEd.putBoolean("isFromSet", isFromSet);
		spEd.commit();
	}

	/**
	 * 载入已记住的用户信息
	 */
	public static void LoadUserDate(Context context) {
		listenLecture = (ListenLecture) context.getApplicationContext();
		SharedPreferences sp = context.getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		listenLecture.setSkinFlag(sp.getInt("skinColor", Constant.BLUE));
		listenLecture.setFirstUse(sp.getBoolean("isFirstUse", true));
		listenLecture.setFromSet(sp.getBoolean("isFromSet", false));
		if (!listenLecture.isLogoutFlag()) {
			if (sp.getBoolean("isSave", false)) {
				String username = sp.getString("name", "");
				String userpassword = sp.getString("password", "");
				if (!("".equals(username) && "".equals(userpassword))) {
					listenLecture.setUserName(username);
					listenLecture.setLoginFlag(true);
				} else
					listenLecture.setLoginFlag(false);
			} else
				listenLecture.setLoginFlag(false);
		} else {
			listenLecture.setLoginFlag(false);
		}
	}

	protected String username;

	protected String password;

	private void loginResponse(final Context context) {
		ViewUtil.initProgressDialog(context, context.getString(R.string.logining));
		final Handler loginHandler = new Handler() {
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
								if (list.get(0).getSpeaker() != null && list.get(0).getSpeaker()
										.equals(Constant.LOGIN_SUCCESSFULLY)) {
									System.out.println("login successfully!!");

									Toast.makeText(
											context.getApplicationContext(),
											"登录成功！", Toast.LENGTH_LONG).show();
									listenLecture.setLoginFlag(true);
									listenLecture.setUserName(username);
									saveUserDate(context);
								} else {
									Toast.makeText(
											context.getApplicationContext(),
											"用户名或密码错误！", Toast.LENGTH_LONG)
											.show();
									listenLecture.setLoginFlag(false);
									login(context);
								}
							} else {
								Toast.makeText(context.getApplicationContext(),
										"用户名或密码错误！", Toast.LENGTH_LONG).show();
								System.out.println("login failedly!!");
								listenLecture.setLoginFlag(false);
								login(context);
							}
						} else {
							Toast.makeText(context.getApplicationContext(),
									"用户名或密码错误！", Toast.LENGTH_LONG).show();
							System.out.println("login failedly!!");
							listenLecture.setLoginFlag(false);
							login(context);
						}
					else if (list == null) {
						Toast.makeText(context.getApplicationContext(),
								"登录失败，请检查网络连接！", Toast.LENGTH_LONG).show();
						System.out.println("login failedly!!");
						listenLecture.setLoginFlag(false);
						login(context);
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

		username = loginUsername.getText().toString().trim();
		password = loginPassword.getText().toString().trim();
		if (!(username.length() == 0 || password.length() == 0)) {
			new Thread() {
				public void run() {
					NameValuePair usernameNameValuePair = new BasicNameValuePair(
							"username", username);
					NameValuePair passwordNameValuePair = new BasicNameValuePair(
							"password", password);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(usernameNameValuePair);
					nameValuePairs.add(passwordNameValuePair);
					List<Lecture> list = GetLectureData.getLectureData(
							HttpUtil.LOGIN_URL, nameValuePairs);

					Message message = new Message();
					message.what = MESSAGETYPE_01;
					message.obj = list;
					loginHandler.sendMessage(message);
				}
			}.start();
		} else {
			Toast.makeText(context.getApplicationContext(), "用户名或密码不能为空！",
					Toast.LENGTH_LONG).show();
			System.out.println("login failedly!!");
			listenLecture.setLoginFlag(false);
			login(context);
			ViewUtil.closeProgressDialog();
		}

	}

	private void registerResponse(final Context context) {
		ViewUtil.initProgressDialog(context, context.getString(R.string.registering));
		final Handler registerHandler = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(Message message) {
				switch (message.what) {
				case MESSAGETYPE_01:
					long startTime = SystemClock.currentThreadTimeMillis();
					List<Lecture> list = (List<Lecture>) message.obj;
					if (list != null)
						if (!list.isEmpty()) {
							if (list.get(0) != null) {
								if (list.get(0).getSpeaker() != null && list.get(0).getSpeaker()
										.equals(Constant.REGISTER_SUCCESSFULLY)) {
									System.out
											.println("register successfully!!");

									Toast.makeText(
											context.getApplicationContext(),
											"注册成功！", Toast.LENGTH_LONG).show();
									listenLecture.setLoginFlag(true);
									listenLecture.setUserName(username);
									saveUserDate(context);
								} else {
									Toast.makeText(
											context.getApplicationContext(),
											"注册失败，用户名已存在！", Toast.LENGTH_LONG)
											.show();
									System.out.println("register failedly!!");
									listenLecture.setLoginFlag(false);
									login(context);
								}
							} else {
								Toast.makeText(context.getApplicationContext(),
										"注册失败，用户名已存在！", Toast.LENGTH_LONG)
										.show();
								System.out.println("register failedly!!");
								listenLecture.setLoginFlag(false);
								login(context);

							}
						} else {
							Toast.makeText(context.getApplicationContext(),
									"注册失败，用户名已存在！", Toast.LENGTH_LONG).show();
							System.out.println("register failedly!!");
							listenLecture.setLoginFlag(false);
							login(context);
						}
					else if (list == null) {
						Toast.makeText(context.getApplicationContext(),
								"注册失败，用户名已存在！", Toast.LENGTH_LONG).show();
						System.out.println("register failedly!!");
						listenLecture.setLoginFlag(false);
						login(context);
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

		username = loginUsername.getText().toString().trim();
		password = loginPassword.getText().toString().trim();
		if (!(username.length() == 0 || password.length() == 0)) {
			new Thread() {
				public void run() {
					NameValuePair usernameNameValuePair = new BasicNameValuePair(
							"username", username);
					NameValuePair passwordNameValuePair = new BasicNameValuePair(
							"password", password);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(usernameNameValuePair);
					nameValuePairs.add(passwordNameValuePair);
					List<Lecture> list = GetLectureData.getLectureData(
							HttpUtil.REGISTER_URL, nameValuePairs);

					Message message = new Message();
					message.what = MESSAGETYPE_01;
					message.obj = list;
					registerHandler.sendMessage(message);
				}
			}.start();
		} else {
			Toast.makeText(context.getApplicationContext(), "用户名或密码不能为空！",
					Toast.LENGTH_LONG).show();
			System.out.println("register failedly!!");
			listenLecture.setLoginFlag(false);
			login(context);
			ViewUtil.closeProgressDialog();
		}
	}

}
