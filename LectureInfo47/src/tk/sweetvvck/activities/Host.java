package tk.sweetvvck.activities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.adapters.HostInfoAdapter;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.customview.ScrollLayout;
import tk.sweetvvck.model.HostItemId;
import tk.sweetvvck.utils.Constant;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.LoginDialog;
import tk.sweetvvck.utils.ViewUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 用于显示校园分类讲座信息 采用九宫格方式，水平滑动显示
 * 
 * @author sweetvvck
 * 
 */
public class Host extends Activity implements OnClickListener,
		android.content.DialogInterface.OnClickListener {

	private int isFirst = 0;
	private ListenLecture listenLecture;
	private Bitmap bitmap;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private Button btnSpeech;
	private Button btnSearch;
	private Button btnClearEdit;
	private EditText edtSearch;
	private String[] arrSpeechKeyword;
	private String lectureContentFromSearchBar;

	// 校园图标Id
	private static final int[] imageResId = { R.drawable.qinghuadaxue,
			R.drawable.beijingdaxue, R.drawable.renmindaxue,
			R.drawable.beihang, R.drawable.beijingshifandaxue,
			R.drawable.zhongyangcaida, R.drawable.beijinglinyedaxue,
			R.drawable.zhongguonongyedaxue, R.drawable.zhongkeyuan,
			R.drawable.lianda, R.drawable.beiyou,
			R.drawable.zhongguonongyedaxue, R.drawable.beigongshang,
			R.drawable.beijinghuagongdaxue, R.drawable.zhongguozhengfadaxue,
			R.drawable.beijingligongdaxue, R.drawable.beijiao,
			R.drawable.chuanmei, R.drawable.zhongguodizhidaxue,
			R.drawable.shoudushifandaxue, R.drawable.beijingkejidaxue,
			R.drawable.duiwaijingmao, R.drawable.zhongshiyoudaxue,
			R.drawable.beijingkejidaxue, R.drawable.guobo, R.drawable.guotu,
			R.drawable.shoutu };

	// 校园名称Id
	private static final int[] stringId = { R.string.qinghua, R.string.beida,
			R.string.renda, R.string.beihang, R.string.beijingshifandaxue,
			R.string.zhongyangcaida, R.string.beijinglinyedaxue,
			R.string.zhongguonongyedaxue, R.string.zhongkeyuan,
			R.string.lianda, R.string.beiyou, R.string.zhongguonongyedaxue,
			R.string.beigongshang, R.string.beijinghuagongdaxue,
			R.string.zhongguozhengfadaxue, R.string.beijingligongdaxue,
			R.string.beijiao, R.string.chuanmei, R.string.zhongguodizhidaxue,
			R.string.shoudushifandaxue, R.string.beijingkejidaxue,
			R.string.duiwaijingmao, R.string.zhongshiyoudaxue,
			R.string.zhongguokuangyedaxue, R.string.guobo, R.string.guotu,
			R.string.shoutu };

	// 一个页面显示的校园数量
	private static final float APP_PAGE_SIZE = 9.0f;

	private Context mContext;

	private ScrollLayout mScrollLayout = null;

	private ProgressBar progressBar = null;

	private static final int MESSAGETYPE_01 = 0x0001;

	private List<HostItemId> list = new ArrayList<HostItemId>();

	private int pageCount = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.host);
		LoginDialog.LoadUserDate(Host.this);
		listenLecture = (ListenLecture) getApplicationContext();
		mContext = this;
		// 初始化搜索框
		initSearchBar();

		mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayoutTest);
		getHost();
		isFirst = listenLecture.getSkinFlag();
	}

	private void initSearchBar() {
		btnSpeech = (Button) findViewById(R.id.searchBtnSpeech);

		btnSpeech.setBackgroundDrawable(newSelector(this,
				android.R.drawable.ic_btn_speak_now,
				android.R.drawable.ic_btn_speak_now,
				android.R.drawable.ic_btn_speak_now));

		btnSpeech.setOnClickListener(this);
		btnSearch = (Button) findViewById(R.id.searchButton);
		btnSearch.setOnClickListener(this);
		btnSearch.setBackgroundDrawable(newSelector(this,
				R.drawable.search_button, R.drawable.search_button_onfocus_sel,
				R.drawable.search_button_onfocus));
		btnClearEdit = (Button) findViewById(R.id.btnClearEdit);
		btnClearEdit.setOnClickListener(this);
		btnClearEdit.setBackgroundDrawable(newSelector(this,
				R.drawable.login_delete, R.drawable.login_delete,
				R.drawable.login_delete));
		edtSearch = (EditText) findViewById(R.id.searchEdit);
		edtSearch.setBackgroundDrawable(newSelector(this,
				R.drawable.search_edittext, R.drawable.search_edittext_onclick,
				R.drawable.search_edittext));
		edtSearch.setOnClickListener(this);
		edtSearch.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (s == null || s.length() == 0) {
					btnClearEdit.setVisibility(View.GONE);
				} else {
					btnClearEdit.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				list = (ArrayList<HostItemId>) message.obj;
				pageCount = (int) Math.ceil(list.size() / APP_PAGE_SIZE);
				System.out.println(">>>>>>>>>>>>>>>>" + pageCount);
				for (int i = 0; i < pageCount; i++) {
					GridView hostPage = new GridView(mContext);
					checkSkin(hostPage);
					// get the "i" page data
					hostPage.setAdapter(new HostInfoAdapter(mContext, list, i));
					// 子页面用GridView，每页三列
					hostPage.setNumColumns(3);
					hostPage.setVerticalSpacing(10);
					hostPage.setOnItemClickListener(new OnItemClickListener() {

						// 注意这个方法的各个参数的含义!!!!!!!!!!!
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {

							// 取到被点击的Item
							HostItemId item = (HostItemId) arg0
									.getItemAtPosition(arg2);

							String hostTitle = null;
							hostTitle = getApplication().getString(
									item.gethostNameId());
							// 跳转到主View中，并将校园的名称传给它显示
							Intent intent = new Intent();
							intent.setClass(Host.this, LectureInfo.class);
							intent.putExtra("stateFlag",
									Constant.STATE_FLAG_HOST_INFO);
							intent.putExtra("hostTitle", hostTitle);
							startActivity(intent);
						}
					});
					// 将GridView 加入到水平滑动的布局中去
					mScrollLayout.addView(hostPage);
				}
				if (progressBar != null)
					progressBar.setVisibility(View.GONE);
			}
		}
	};

	public void getHost() {
		progressBar = (ProgressBar) findViewById(R.id.host_progressBar);
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {
				long startTime = System.currentTimeMillis();
				for (int i = 0; i < imageResId.length; i++) {
					HostItemId hostItemId = new HostItemId();
					hostItemId.sethostImageID(imageResId[i]);
					hostItemId.sethostNameId(stringId[i]);

					list.add(hostItemId);
				}
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.share_img);
				try {
					saveFile(bitmap, "share_img.jpg");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				long endTime = System.currentTimeMillis();
				Message message = new Message();
				message.what = MESSAGETYPE_01;
				message.obj = list;
				try {
					if (2000 > (endTime - startTime))
						Thread.sleep(2000 - (endTime - startTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendMessage(message);
			}

		}).start();
	}

	private void hostBackGroundChanged() {
		progressBar = (ProgressBar) findViewById(R.id.host_progressBar);
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {
				long startTime = System.currentTimeMillis();
				list = null;
				list = new ArrayList<HostItemId>();
				for (int i = 0; i < imageResId.length; i++) {
					HostItemId hostItemId = new HostItemId();
					hostItemId.sethostImageID(imageResId[i]);
					hostItemId.sethostNameId(stringId[i]);

					list.add(hostItemId);
				}
				long endTime = System.currentTimeMillis();
				Message message = new Message();
				message.what = MESSAGETYPE_01;
				message.obj = list;
				try {
					if (2000 > (endTime - startTime))
						Thread.sleep(2000 - (endTime - startTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendMessage(message);
			}

		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
		LoginDialog.LoadUserDate(Host.this);
		if(isFirst != listenLecture.getSkinFlag()){
			mScrollLayout.removeAllViews();
			mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayoutTest);
			hostBackGroundChanged();
			isFirst = listenLecture.getSkinFlag();
		}
	}

	@Override
	protected void onDestroy() {
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ViewUtil.onBackPressed_local(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onClick(DialogInterface dialog, int which) {
		if (arrSpeechKeyword != null && arrSpeechKeyword.length > which) {
			edtSearch.setText(arrSpeechKeyword[which]);
		}
	}

	public void onClick(View view) {
		if (view == edtSearch) {
			edtSearch.setFocusable(true);
		} else if (view == btnSearch) {
			lectureContentFromSearchBar = edtSearch.getText().toString().trim();
			if (lectureContentFromSearchBar.length() == 0) {
				Toast.makeText(getApplication(), "讲座名称不能为空", Toast.LENGTH_LONG)
						.show();
			} else {
				Intent intent = new Intent();
				intent.setClass(this, LectureInfo.class);
				intent.putExtra("stateFlag", Constant.STATE_FLAG_HOST_INFO);
				intent.putExtra("searchTitle", "搜索结果");
				intent.putExtra("searchContent", lectureContentFromSearchBar);
				startActivity(intent);
			}
		} else if (view == btnSpeech) {
			startVoiceRecognitionActivity();
		} else if (view == btnClearEdit) {
			edtSearch.setText("");
		}
	}

	/** 设置Selector。 */
	public static StateListDrawable newSelector(Context context, int idNormal,
			int idPressed, int idFocused) {
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = idNormal == -1 ? null : context.getResources()
				.getDrawable(idNormal);
		Drawable pressed = idPressed == -1 ? null : context.getResources()
				.getDrawable(idPressed);
		Drawable focused = idFocused == -1 ? null : context.getResources()
				.getDrawable(idFocused);
		// View.PRESSED_ENABLED_STATE_SET
		bg.addState(new int[] { 16842910, 16842919 }, pressed);
		// View.ENABLED_FOCUSED_STATE_SET
		bg.addState(new int[] { 16842908, 16842910 }, focused);
		// View.ENABLED_STATE_SET
		bg.addState(new int[] { 16842910 }, normal);
		// View.FOCUSED_STATE_SET
		bg.addState(new int[] { 16842908 }, focused);
		// View.EMPTY_STATE_SET
		bg.addState(new int[] {}, normal);
		return bg;
	}

	/**
	 * Fire an intent to start the speech recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// Optional text prompt to show to the user when asking them to speak
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speech recognition demo");

		PackageManager pkgManager = getPackageManager();
		List<ResolveInfo> listResolveInfo = pkgManager.queryIntentActivities(
				intent, 0);
		if (listResolveInfo == null || listResolveInfo.size() == 0) {
			// 不支持语音识别，弹对话框提示
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提醒");
			builder.setMessage("未安装google voice.");
			builder.setPositiveButton("取消", null);
			builder.create().show();
		} else {
			// 正常显示语音识别界面
			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		}
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> arrResults = intent
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			int size = arrResults.size();
			arrSpeechKeyword = new String[size];
			for (int i = 0; i < size; i++) {
				arrSpeechKeyword[i] = arrResults.get(i);
			}
			arrResults.clear();
			arrResults = null;
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle("search...");
			builder.setItems(arrSpeechKeyword, this);
			builder.create().show();
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

	public void saveFile(Bitmap bm, String fileName) throws IOException {
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(ALBUM_PATH + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/tk.sweetvvck/";

	private void checkSkin(GridView hostPage) {
		switch (listenLecture.getSkinFlag()) {
		case Constant.BLUE:
			hostPage.setBackgroundResource(R.drawable.background_blue);
			break;
		case Constant.DARK_RED:
			hostPage.setBackgroundResource(R.drawable.background_darkred);
			break;
		case Constant.RED:
			hostPage.setBackgroundResource(R.drawable.background_red);
			break;
		case Constant.GRAY:
			hostPage.setBackgroundResource(R.drawable.background_gray);
			break;
		case Constant.GREEN:
			hostPage.setBackgroundResource(R.drawable.background_green);
			break;
		default:
			hostPage.setBackgroundResource(R.drawable.background_blue);
			break;
		}
	}
	
    protected void onPause() {
    	super.onPause();
    	GFAgent.onPause(this);
    }
}
