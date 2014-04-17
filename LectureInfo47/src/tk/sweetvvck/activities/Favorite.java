package tk.sweetvvck.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.adapters.LectureAdapter;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.customview.MyImageView;
import tk.sweetvvck.customview.MyListView;
import tk.sweetvvck.customview.MyListView.OnRefreshListener;
import tk.sweetvvck.customview.MyProgressBar;
import tk.sweetvvck.fromserver.GetLectureData;
import tk.sweetvvck.lecture.Lecture;
import tk.sweetvvck.utils.Constant;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.HttpUtil;
import tk.sweetvvck.utils.LoginDialog;
import tk.sweetvvck.utils.ViewUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Favorite extends Activity {

	private static int stateFlag = Constant.STATE_FLAG_DETAIL_INFO;
	private RelativeLayout head = null;
	private TextView lastUpdatedTextView;
	private MyImageView refresh = null;
	private MyProgressBar refreshBar = null;
	private List<Lecture> list = null;
	private Thread refreshThread = null;
	private MyListView listView = null;
	private static final int MESSAGETYPE_01 = 0x0001;
	private String username = "";
	private ListenLecture listenLecture = null;
	private FrameLayout loadErrorLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite);
		loadErrorLayout = (FrameLayout) findViewById(R.id.load_error_favorite);
		listenLecture = (ListenLecture) Favorite.this.getApplicationContext();
		Intent intent = getIntent();
		refresh = (MyImageView) intent.getSerializableExtra("refresh");
		refreshBar = (MyProgressBar) intent.getSerializableExtra("refreshBar");
		refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 点击刷新图标刷新讲座信息，显示旋转进度条和刷新提示
				getInfoByRefreshImage();
			}
		});
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
		Intent intent = getIntent();
		refresh = (MyImageView) intent.getSerializableExtra("refresh");
		refreshBar = (MyProgressBar) intent.getSerializableExtra("refreshBar");
		if (username != listenLecture.getUserName())
			getLoginState();
		else {
			refresh.setVisibility(View.VISIBLE);
			refresh.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// 点击刷新图标刷新讲座信息，显示旋转进度条和刷新提示
					getInfoByRefreshImage();
				}
			});
		}
	}

	private void getInfoByRefreshImage() {
		head = (RelativeLayout) findViewById(R.id.favorite_contentLayout);
		lastUpdatedTextView = (TextView) findViewById(R.id.favorite_lastUpdatedTextView);
		lastUpdatedTextView.setText("更新于:" + new Date().toLocaleString());
		head.setVisibility(View.VISIBLE);
		refresh();
	}

	private void initView() {
		final MyListView myListView = (MyListView) findViewById(R.id.favorite_listview);
		listView = myListView;
		// 得到自定义对话框
		LoginDialog.LoadUserDate(this);
		getLoginState();
	}

	private void getLoginState() {
		if (!listenLecture.getLoginFlag()) {
			LoginDialog.getInstance().login(this);
		} else {
			username = listenLecture.getUserName();
			getFavoriteLecture();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ViewUtil.onBackPressed_local(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler getfavoriteLectureHandler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				// 接收handler传来的list
				list = (List<Lecture>) message.obj;
				// 如果list为空，则提示未响应
				if (list == null) {
					loadErrorLayout.setVisibility(View.VISIBLE);
					ImageView loadErrorView = (ImageView) loadErrorLayout
							.findViewById(R.id.load_error_view);
					loadErrorView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							refresh();
							loadErrorLayout.setVisibility(View.GONE);
						}
					});
				}
				// 如果list包含的内容为空，则提示无讲座信息
				else if (list.isEmpty()) {
					Toast.makeText(getApplication(), "未收藏任何讲座",
							Toast.LENGTH_LONG).show();
					list = null;
				}
				// 如果list中讲座信息的主键为空，则提示连接失败
				else if (list.get(0).getSpeaker() == null) {
					loadErrorLayout.setVisibility(View.VISIBLE);
					ImageView loadErrorView = (ImageView) loadErrorLayout
							.findViewById(R.id.load_error_view);
					loadErrorView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							refresh();
							loadErrorLayout.setVisibility(View.GONE);
						}
					});
					list = null;
				}
				// 创建自定义adapter处理list中的信息
				final LectureAdapter lectureAdapter = new LectureAdapter(list,
						Favorite.this);

				// 创立自定义ListView，实现下拉刷新功能
				listView.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						System.out.println("点击的位置：" + position);
						// 自定义的ListView，空间的位置是从0开始！！
						Lecture lecture = list.get(position - 1);
						// 跳转到主View中显示详细讲座信息
						Intent intent = new Intent();
						intent.setClass(Favorite.this, LectureInfo.class);
						intent.putExtra("lecture", lecture);
						intent.putExtra("stateFlag", stateFlag);
						startActivity(intent);
					}
				});

				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						final Lecture lecture = list.get(position - 1);
						LayoutInflater factory = (LayoutInflater) Favorite.this
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						// 得到自定义对话框
						View dialogView = factory.inflate(
								R.layout.delete_one_favorite_lecture, null);
						Button button = (Button) dialogView
								.findViewById(R.id.delete_one);
						final AlertDialog dlg = new AlertDialog.Builder(
								Favorite.this).setView(dialogView).create();// 创建对话框
						dlg.setView(dialogView, 0, 0, 0, 0);
						dlg.show();// 显示对话框
						dlg.setCancelable(true);
						dlg.setOnCancelListener(new OnCancelListener() {
							public void onCancel(DialogInterface dialog) {
								dlg.dismiss();
							}
						});
						button.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								dlg.dismiss();
								ViewUtil.initProgressDialog(
										Favorite.this,
										getApplicationContext().getString(
												R.string.deleting_this));
								deleteThisFavoriteLecture(lecture);
							}
						});
						return true;
					}
				});

				listView.setonRefreshListener(new OnRefreshListener() {
					public void onRefresh() {
						getInfoByPullRefreshListView();
					}
				});
				listView.setAdapter(lectureAdapter);
				ViewUtil.closeProgressDialog();
				listView.onRefreshComplete();
				if (head != null)
					head.setVisibility(View.GONE);
				if (refreshBar != null)
					refreshBar.setVisibility(View.GONE);
				if (refresh != null)
					refresh.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
	protected Handler deleteThisFavoriteLectureHandler = new Handler() {
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
								Toast.makeText(getApplication(), "删除成功！",
										Toast.LENGTH_LONG).show();
								ViewUtil.closeProgressDialog();
								getFavoriteLecture();
							} else {
								Toast.makeText(getApplication(),
										"删除失败，请检查网络连接", Toast.LENGTH_LONG)
										.show();
							}
						} else {
							Toast.makeText(getApplication(), "删除失败，请检查网络连接",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getApplication(), "删除失败，请检查网络连接",
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

	private void deleteThisFavoriteLecture(Lecture lecture) {
		final String username = listenLecture.getUserName();
		final String speaker = lecture.getSpeaker();
		final String date = lecture.getDate();
		new Thread() {
			public void run() {
				NameValuePair usernameNameValuePair = new BasicNameValuePair(
						"username", username);
				NameValuePair speakerNameValuePair = new BasicNameValuePair(
						"speaker", speaker);
				NameValuePair dateNameValuePair = new BasicNameValuePair(
						"date", date);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(usernameNameValuePair);
				nameValuePairs.add(speakerNameValuePair);
				nameValuePairs.add(dateNameValuePair);
				List<Lecture> list = GetLectureData.getLectureData(
						HttpUtil.DELETE_ONE_FAVOTITE_LECTURE, nameValuePairs);

				Message message = new Message();
				message.what = MESSAGETYPE_01;
				message.obj = list;
				deleteThisFavoriteLectureHandler.sendMessage(message);
			}
		}.start();
	}

	protected void getFavoriteLecture() {
		ViewUtil.initProgressDialog(Favorite.this, getApplicationContext()
				.getString(R.string.loaddata));
		refresh();
	}

	private void refresh() {
		refresh.setVisibility(View.GONE);
		refreshBar.setVisibility(View.VISIBLE);
		refreshThread = new Thread() {
			public void run() {

				NameValuePair lecture = new BasicNameValuePair("username",
						username);
				List<NameValuePair> searchList = new ArrayList<NameValuePair>();
				searchList.add(lecture);
				list = GetLectureData.getLectureData(HttpUtil.GET_FAVORITE_URL,
						searchList);
				Message message = new Message();
				message.what = MESSAGETYPE_01;
				message.obj = list;
				getfavoriteLectureHandler.sendMessage(message);
			}
		};
		refreshThread.start();
	}

	private void getInfoByPullRefreshListView() {
		refresh();
	}

	protected void onPause() {
		super.onPause();
		GFAgent.onPause(this);
	}
}
