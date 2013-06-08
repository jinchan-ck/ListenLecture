package tk.sweetvvck.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.adapters.TalksAdapter;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.customview.MyListView;
import tk.sweetvvck.fromserver.GetLectureData;
import tk.sweetvvck.fromserver.GetTalksData;
import tk.sweetvvck.lecture.Lecture;
import tk.sweetvvck.talks.Talks;
import tk.sweetvvck.utils.Constant;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.HttpUtil;
import tk.sweetvvck.utils.ListHeightUtils;
import tk.sweetvvck.utils.LoginDialog;
import tk.sweetvvck.utils.ViewUtil;
import tk.sweetvvck.views.animation.ComposerButtonAnimation;
import tk.sweetvvck.views.animation.InOutAnimation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用于显示讲座的详细信息，供用户查看 附加功能采用模仿path的方式，提供四种功能，分别为：分享，收藏，留言和路线
 * 
 * @author sweetvvck
 * 
 */
public class DetailInfo extends Activity {
	private static final int MESSAGETYPE_01 = 0x0001;
	private TextView dateView = null;
	private TextView addressView = null;
	private TextView lecutreView = null;
	private TextView speakerView = null;
	private TextView contentView = null;
	private TextView hostView = null;
	private TextView favoriteNumber = null;
	private MyListView talksListView = null;
	private ImageView composerButton = null;

	private boolean areButtonsShowing = false;
	private View composerButtonShare;
	private View composerButtonPlace;
	private View composerButtonFavorite;
	private View composerButtonTalk;
	private View composerButtonsShowHideButton;//
	private View composerButtonsShowHideButtonIcon;//
	private ViewGroup composerButtonsWrapper;

	private Animation rotateStoryAddButtonIn;
	private Animation rotateStoryAddButtonOut;

	private Lecture lecture = null;

	private ScrollView detailBody = null;

	private ListenLecture listenLecture = null;
	private EditText notesContent = null;
	private ProgressBar wait = null;
	private FrameLayout loadTalksError;
	private ImageView noTalks = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		listenLecture = (ListenLecture) this.getApplicationContext();
		Intent intent = getIntent();
		// 获得上个Activity传来的讲座对象
		initLectureInfo(intent);
		loadTalksError = (FrameLayout) findViewById(R.id.load_error);
		noTalks = (ImageView) findViewById(R.id.no_talks);
		getTalks();
		setUpViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
		checkSkin();
	}

	protected Handler showTalksHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				if (wait != null)
					wait.setVisibility(View.GONE);
				// 接收handler传来的list
				List<Talks> talksList = (List<Talks>) message.obj;
				if (talksList == null) {
					loadTalksError.setVisibility(View.VISIBLE);
					ImageView loadErrorView = (ImageView) loadTalksError
							.findViewById(R.id.load_error_view);
					loadErrorView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							getTalks();
							loadTalksError.setVisibility(View.GONE);
						}
					});
				}
				// 如果list包含的内容为空，则提示无讲座信息
				else if (talksList.isEmpty()) {
					talksList = null;
					noTalks.setVisibility(View.VISIBLE);
				}
				// 如果list中讲座信息的主键为空，则提示连接失败
				else if (talksList.get(0).getUsername() == null) {
					talksList = null;
					loadTalksError.setVisibility(View.VISIBLE);
					ImageView loadErrorView = (ImageView) loadTalksError
							.findViewById(R.id.load_error_view);
					loadErrorView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							getTalks();
							loadTalksError.setVisibility(View.GONE);
						}
					});
				} else {
					talksListView.setVisibility(View.VISIBLE);
					TalksAdapter talksAdapter = new TalksAdapter(talksList,
							DetailInfo.this);
					talksListView.setAdapter(talksAdapter);
					ListHeightUtils
							.setListViewHeightBasedOnChildren(talksListView);
				}
			}
		}
	};

	protected Handler addTalksHandler = new Handler() {
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
								Toast.makeText(getApplication(), "发布成功！",
										Toast.LENGTH_LONG).show();
								getTalks();

							} else {
								Toast.makeText(getApplication(),
										"发布失败，请检查网络连接", Toast.LENGTH_LONG)
										.show();
							}
						} else {
							Toast.makeText(getApplication(), "发布失败，请检查网络连接",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getApplication(), "发布失败，请检查网络连接",
								Toast.LENGTH_LONG).show();
					}
				else if (list == null) {
					Toast.makeText(getApplication(), "发布失败，请检查网络连接",
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
	protected Handler showFavoriteHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				// 接收handler传来的list
				List<Lecture> lectureList = (List<Lecture>) message.obj;
				if (lectureList == null) {
					favoriteNumber.setText("收藏人数加载失败");
				}
				// 如果list包含的内容为空，则提示无讲座信息
				else if (lectureList.isEmpty()) {
					favoriteNumber.setText("收藏人数加载失败");
					lectureList = null;
				}
				// 如果list中讲座信息的主键为空，则提示连接失败
				else if (lectureList.get(0).getDate() == null) {
					favoriteNumber.setText("收藏人数加载失败");
					lectureList = null;
				} else if (lectureList.get(0).getDate() != null) {
					favoriteNumber.setText("已有" + lectureList.get(0).getDate()
							+ "人收藏");
				}
			}
		}
	};

	private void getTalks() {
		noTalks.setVisibility(View.GONE);
		wait = (ProgressBar) findViewById(R.id.talks_progressBar);
		wait.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				long startTime = SystemClock.currentThreadTimeMillis();
				List<Talks> talksList = new ArrayList<Talks>();
				NameValuePair speakerNameValuePair = new BasicNameValuePair(
						"speaker", lecture.getSpeaker());
				NameValuePair dateNameValuePair = new BasicNameValuePair(
						"date", lecture.getDate());
				List<NameValuePair> nvList = new ArrayList<NameValuePair>();
				nvList.add(dateNameValuePair);
				nvList.add(speakerNameValuePair);
				talksList = GetTalksData.getTalksData(nvList);
				long endTime = SystemClock.currentThreadTimeMillis();
				if (2000 - (endTime - startTime) > 0)
					try {
						Thread.sleep(2000 - (endTime - startTime));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				Message message = new Message();
				message.what = MESSAGETYPE_01;
				message.obj = talksList;
				showTalksHandler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * 初始化要显示的讲座信息
	 * 
	 * @param intent
	 */
	private void initLectureInfo(Intent intent) {
		lecture = (Lecture) intent.getSerializableExtra("lecture");
		dateView = (TextView) findViewById(R.id.datecontent);
		addressView = (TextView) findViewById(R.id.addresscontent);
		lecutreView = (TextView) findViewById(R.id.lecturecontent);
		speakerView = (TextView) findViewById(R.id.speakercontent);
		contentView = (TextView) findViewById(R.id.contentscontent);
		hostView = (TextView) findViewById(R.id.host);
		favoriteNumber = (TextView) findViewById(R.id.shoucang);
		talksListView = (MyListView) findViewById(R.id.talks);
		talksListView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					if (areButtonsShowing == true)
						toggleComposerButtons();
				}
				return false;
			}
		});

		dateView.setText(lecture.getDate());
		addressView.setText(lecture.getAddress());
		lecutreView.setText(lecture.getLecture());
		speakerView.setText(lecture.getSpeaker());
		contentView.setText(lecture.getContent());
		hostView.setText(lecture.getHost());
		getFavoriteNumber();
	}

	private void getFavoriteNumber() {
		new Thread() {
			public void run() {
				List<Lecture> lectureList = new ArrayList<Lecture>();
				NameValuePair speakerNameValuePair = new BasicNameValuePair(
						"speaker", lecture.getSpeaker());
				NameValuePair dateNameValuePair = new BasicNameValuePair(
						"date", lecture.getDate());
				List<NameValuePair> nvList = new ArrayList<NameValuePair>();
				nvList.add(dateNameValuePair);
				nvList.add(speakerNameValuePair);
				lectureList = GetLectureData.getLectureData(
						HttpUtil.GET_FAVORITE_NUMBER, nvList);
				Message message = new Message();
				message.what = MESSAGETYPE_01;
				message.obj = lectureList;
				showFavoriteHandler.sendMessage(message);
			}
		}.start();
	}

	private void setUpViews() {
		rotateStoryAddButtonIn = AnimationUtils.loadAnimation(this,
				R.anim.rotate_story_add_button_in);
		rotateStoryAddButtonOut = AnimationUtils.loadAnimation(this,
				R.anim.rotate_story_add_button_out);

		composerButtonsWrapper = (ViewGroup) findViewById(R.id.composer_buttons_wrapper);
		composerButtonsShowHideButton = findViewById(R.id.composer_buttons_show_hide_button);
		composerButtonsShowHideButtonIcon = findViewById(R.id.composer_buttons_show_hide_button_icon);
		composerButton = (ImageView) composerButtonsShowHideButton
				.findViewById(R.id.composer_button);
		checkSkin();

		detailBody = (ScrollView) findViewById(R.id.detail_body);
		// 设置触摸body时，如果功能处于显示状态则隐藏那些功能图标
		detailBody.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					if (areButtonsShowing == true)
						toggleComposerButtons();
				}
				return false;
			}
		});

		// 分享功能图标
		composerButtonShare = findViewById(R.id.composer_button_share);
		composerButtonShare.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (areButtonsShowing == true)
					toggleComposerButtons();
				onClickShare(v);
			}
		});

		// 查询路线功能图标
		composerButtonPlace = findViewById(R.id.composer_button_place);
		composerButtonPlace.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (areButtonsShowing == true)
					toggleComposerButtons();
				if (!checkGoogleMap()) {
					showNoGMap();
				} else {
					Intent i = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://maps.google.com/maps?f=d&saddr=&daddr="
									+ lecture.getHost())); // http://maps.google.com/maps?f=d&saddr=北四环东路97号&daddr=乡渝府&hl=tw
					startActivity(i); // "http://ditu.google.cn/maps?hl=zh&mrt=loc&q=北四环东路97号"
				}
			}
		});

		composerButtonFavorite = findViewById(R.id.composer_button_favorate);
		composerButtonFavorite.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (areButtonsShowing == true)
					toggleComposerButtons();
				ViewUtil.initProgressDialog(DetailInfo.this, getApplicationContext().getString(R.string.set_favorite));
				setFavoriteLecture();
			}
		});

		composerButtonTalk = findViewById(R.id.composer_button_chat);
		composerButtonTalk.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (areButtonsShowing == true)
					toggleComposerButtons();
				ViewUtil.initProgressDialog(DetailInfo.this, getApplicationContext().getString(R.string.add_talk));
				showAddTalksDialog();
			}
		});

		registerComposerButtonListeners();
	}

	private void checkSkin() {
		switch (listenLecture.getSkinFlag()) {
		case Constant.BLUE:
			composerButton.setImageResource(R.drawable.composer_button_blue);
			break;
		case Constant.DARK_RED:
			composerButton
					.setImageResource(R.drawable.composer_button_dark_red);
			break;
		case Constant.RED:
			composerButton.setImageResource(R.drawable.composer_button_red);
			break;
		case Constant.GRAY:
			composerButton.setImageResource(R.drawable.composer_button_gray);
			break;
		case Constant.GREEN:
			composerButton.setImageResource(R.drawable.composer_button_green);
			break;
		default:
			composerButton.setImageResource(R.drawable.composer_button_blue);
			break;
		}
	}

	protected void showAddTalksDialog() {
		LayoutInflater factory = LayoutInflater.from(DetailInfo.this);
		// 得到自定义对话框
		final View dialogView = factory.inflate(R.layout.notes, null);
		notesContent = (EditText) dialogView.findViewById(R.id.notes_content);
		LoginDialog.LoadUserDate(DetailInfo.this);
		if (listenLecture.getLoginFlag()) {
			// 创建对话框
			final AlertDialog dlg = new AlertDialog.Builder(DetailInfo.this)
					.setTitle("留言")
					.setView(dialogView)
					// 设置自定义对话框样式
					.setPositiveButton("发布",
							new DialogInterface.OnClickListener() {// 设置监听事件
								public void onClick(DialogInterface dialog,
										int which) {
									// 输入完成后点击"确定"开始登录
									addTalks();
								}
							}).setNegativeButton("取消",// 设置取消按钮
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									ViewUtil.closeProgressDialog();
								}
							}).create();// 创建对话框

			dlg.show();// 显示对话框
			dlg.setCancelable(true);
			dlg.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					ViewUtil.closeProgressDialog();
					dlg.dismiss();
				}
			});
		} else {
			LoginDialog.getInstance().login(DetailInfo.this);
			if (listenLecture.getLoginFlag())
				showAddTalksDialog();
			ViewUtil.closeProgressDialog();
		}

	}

	protected void addTalks() {
		final String username = listenLecture.getUserName();
		final String notes = notesContent.getText().toString().trim();
		final String speaker = lecture.getSpeaker();
		final String date = lecture.getDate();
		if (notes != null) {
			if (notes.length() != 0) {
				new Thread() {
					public void run() {
						NameValuePair usernameNameValuePair = new BasicNameValuePair(
								"username", username);
						NameValuePair notesNameValuePair = new BasicNameValuePair(
								"notes", notes);
						NameValuePair speakerNameValuePair = new BasicNameValuePair(
								"speaker", speaker);
						NameValuePair dateNameValuePair = new BasicNameValuePair(
								"date", date);
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(usernameNameValuePair);
						nameValuePairs.add(notesNameValuePair);
						nameValuePairs.add(speakerNameValuePair);
						nameValuePairs.add(dateNameValuePair);
						List<Lecture> list = GetLectureData.getLectureData(
								HttpUtil.ADD_TALKS_URL, nameValuePairs);

						Message message = new Message();
						message.what = MESSAGETYPE_01;
						message.obj = list;
						addTalksHandler.sendMessage(message);
					}
				}.start();

			} else {
				Toast.makeText(getApplication(), "留言内容不能为空！", Toast.LENGTH_LONG)
						.show();
				ViewUtil.closeProgressDialog();
			}
		} else {
			Toast.makeText(getApplication(), "留言内容不能为空！", Toast.LENGTH_LONG)
					.show();
			ViewUtil.closeProgressDialog();
		}
	}

	Handler favoriteHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				long startTime = SystemClock.currentThreadTimeMillis();
				List<Lecture> list = (List<Lecture>) message.obj;
				ViewUtil.closeProgressDialog();
				if (list != null)
					if (!list.isEmpty()) {
						if (list.get(0) != null) {
							if (list.get(0).getSpeaker() != null) {
								if (list.get(0).getSpeaker()
										.equals(Constant.FAVORITE_SUCCESSFULLY)) {
									Toast.makeText(getApplication(), "收藏成功！",
											Toast.LENGTH_LONG).show();
									getFavoriteNumber();
								} else {
									Toast.makeText(getApplication(), "收藏失败！",
											Toast.LENGTH_LONG).show();
								}
							} else {
								Toast.makeText(getApplication(), "收藏失败！",
										Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(getApplication(), "收藏失败！",
									Toast.LENGTH_LONG).show();

						}
					} else {
						Toast.makeText(getApplication(), "收藏失败！",
								Toast.LENGTH_LONG).show();
					}
				else if (list == null)
					Toast.makeText(getApplication(), "收藏失败,请检查网络！",
							Toast.LENGTH_LONG).show();
				long endTime = SystemClock.currentThreadTimeMillis();
				if (2000 - (endTime - startTime) > 0)
					try {
						Thread.sleep(2000 - (endTime - startTime));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				break;
			}
		}
	};

	protected void setFavoriteLecture() {
		LoginDialog.LoadUserDate(DetailInfo.this);
		if (listenLecture.getLoginFlag()) {
			new Thread() {
				public void run() {
					String username = listenLecture.getUserName();
					String speaker = lecture.getSpeaker();
					String date = lecture.getDate();
					NameValuePair usernameNameValuePair = new BasicNameValuePair(
							"username", username);
					NameValuePair speakerNameValuePair = new BasicNameValuePair(
							"speaker", speaker);
					NameValuePair dateNameValuePair = new BasicNameValuePair(
							"date", date);
					List<NameValuePair> nvList = new ArrayList<NameValuePair>();
					nvList.add(dateNameValuePair);
					nvList.add(speakerNameValuePair);
					nvList.add(usernameNameValuePair);
					List<Lecture> list = GetLectureData.getLectureData(
							HttpUtil.FAVORITE_URL, nvList);
					Message message = new Message();
					message.what = MESSAGETYPE_01;
					message.obj = list;
					favoriteHandler.sendMessage(message);
				}
			}.start();
		} else {
			LoginDialog.getInstance().login(DetailInfo.this);
			if (listenLecture.getLoginFlag())
				setFavoriteLecture();
			ViewUtil.closeProgressDialog();
		}
	}

	protected void showNoGMap() {
		// 创建对话框
		AlertDialog dlg = new AlertDialog.Builder(this)
				.setTitle("未检测到google map，是否使用网页版地图")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {// 设置监听事件
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://maps.google.com/maps?f=d&saddr=&daddr="
												+ lecture.getHost())); // http://maps.google.com/maps?f=d&saddr=北四环东路97号&daddr=乡渝府&hl=tw
								startActivity(i); // "http://ditu.google.cn/maps?hl=zh&mrt=loc&q=北四环东路97号"
							}
						}).setNegativeButton("取消",// 设置取消按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).create();// 创建对话框
		dlg.show();// 显示对话框
		dlg.setCancelable(true);
	}

	protected boolean checkGoogleMap() {
		boolean isInstallGMap = false;
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if (p.versionName == null) { // system packages
				continue;
			}
			if ("com.google.android.apps.maps".equals(p.packageName)
					|| ("brut.googlemaps").equals(p.packageName)) {
				isInstallGMap = true;
				break;
			}
		}
		return isInstallGMap;
	}

	/**
	 * 分享功能实现
	 * 
	 * @param view
	 * @author sweetvvck
	 */
	private void onClickShare(View view) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		String str = "我看到一个很好的讲座，要不要一起去听听：\r\n" + "讲座：\r\n"
				+ lecture.getLecture() + "\r\n时间：\r\n" + lecture.getDate()
				+ "\r\n地点：\r\n" + lecture.getAddress();
		intent.putExtra("android.intent.extra.STREAM",
				Uri.parse("file://mnt/sdcard/tk.sweetvvck/share_img.jpg"));
		intent.setType("image/*");
		intent.putExtra("sms_body", str);
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, str);
		// intent.setFlags(intent.getFlags());
		intent.addFlags(1);
		startActivity(Intent.createChooser(intent, "分享给..."));

	}

	private void registerComposerButtonListeners() {
		composerButtonsShowHideButton
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						toggleComposerButtons();
					}
				});
	}

	/**
	 * 
	 * 设置动画效果
	 * 
	 * @author sweetvvck
	 */
	private void toggleComposerButtons() {
		if (areButtonsShowing) {
			ComposerButtonAnimation.startAnimations(composerButtonsWrapper,
					InOutAnimation.Direction.OUT);
			composerButtonsShowHideButtonIcon
					.startAnimation(rotateStoryAddButtonOut);
		} else {
			ComposerButtonAnimation.startAnimations(composerButtonsWrapper,
					InOutAnimation.Direction.IN);
			composerButtonsShowHideButtonIcon
					.startAnimation(rotateStoryAddButtonIn);
		}
		areButtonsShowing = !areButtonsShowing;
	}
	
	protected void onPause() {
    	super.onPause();
    	GFAgent.onPause(this);
    }
}
