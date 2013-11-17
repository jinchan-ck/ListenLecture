package tk.sweetvvck.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import tk.sweetvvck.utils.SystemUtil;
import tk.sweetvvck.utils.ViewUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gfan.sdk.statitistics.GFAgent;

/**
 * 用于显示一周讲座信息或者某个校园的讲座信息 按时间排序 若讲座时间与系统时间相同则显示红色【今天】
 * 
 * @author sweetvvck
 */
public class Hot extends Activity {

	// 设置刷新布局，点击刷新按钮式显示
	private RelativeLayout head = null;
	private TextView lastUpdatedTextView;
	private static int stateFlag = Constant.STATE_FLAG_DETAIL_INFO;

	private Thread refreshThread = null;

	private MyListView listView = null;

	// 声明包含一周讲座信息的list
	private List<Lecture> list = new ArrayList<Lecture>();

	private static final int HANDLER_LOAD_MORE = 0x0001;
	private static final int HANDLER_LOAD_COMPLETED = 0x0002;
	// 声明进度条对话框
	private AlertDialog progressDialog = null;

	private String host;

	// 声明自定义刷新图标和刷新进度条，用于接收从主View传来的它们
	private MyImageView refresh = null;
	private MyProgressBar refreshBar = null;
	private String searchContent;
	private FrameLayout loadErrorLayout = null;
	private TextView noLecture = null;
	private Button addLecture = null;
	private ListenLecture listenLecture = null;

	// TODO Added 2013-11-17 用于分页加载功能
	private int currentPage = 1;
	private int pageSize = 10;
	private TextView loadMoreView;
	private LectureAdapter lectureAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		listenLecture = (ListenLecture) getApplicationContext();
		Intent intent = getIntent();
		initView(intent);
		if (host == null)
			setContentView(R.layout.hot);
		else {
			setContentView(R.layout.host_detail);
			noLecture = (TextView) findViewById(R.id.no_lecture);
			addLecture = (Button) findViewById(R.id.add_lecture);
			checkSkin();
		}
		// 得到自定义对话框
		loadErrorLayout = (FrameLayout) findViewById(R.id.load_error);
		MyListView myListView = (MyListView) findViewById(R.id.listview);
		listView = myListView;
		listView.setMinimumHeight(SystemUtil.dip2px(this, 40));
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout loadMoreLayout = (LinearLayout) inflater.inflate(R.layout.load_more_layout, null);
		loadMoreView = (TextView) loadMoreLayout.findViewById(R.id.load_more_text);
		loadMoreView.setText("加载更多...");
		listView.addFooterView(loadMoreLayout);
		loadMoreView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				loadMoreView.setText("正在加载，请稍后...");
				refresh();
				return;
			}
		});
		// 获得一周讲座信息，加载时弹出进度对话框
		getInfoOnViewLoad();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
		if (host != null)
			checkSkin();
		Intent intent = getIntent();
		refresh = (MyImageView) intent.getSerializableExtra("refresh");
		refreshBar = (MyProgressBar) intent.getSerializableExtra("refreshBar");
		refresh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (host != null) {
					noLecture.setVisibility(View.GONE);
					addLecture.setVisibility(View.GONE);
				}
				// 点击刷新图标刷新讲座信息，显示旋转进度条和刷新提示
				getInfoByRefreshImage();
			}
		});
	}

	private void initView(Intent intent) {
		// 从主View获得host，如果为空则是从一周讲座进入，若果不为空，则是点击某个校园进入
		host = intent.getStringExtra("hostTitle");
		searchContent = intent.getStringExtra("searchContent");
		refresh = (MyImageView) intent.getSerializableExtra("refresh");
		refreshBar = (MyProgressBar) intent.getSerializableExtra("refreshBar");
		refresh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (host != null) {
					noLecture.setVisibility(View.GONE);
					addLecture.setVisibility(View.GONE);
				}
				// 点击刷新图标刷新讲座信息，显示旋转进度条和刷新提示
				getInfoByRefreshImage();
			}
		});
	}

	// 用于处理各种getInfo传来的数据
	private Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case HANDLER_LOAD_MORE:
				// 接收handler传来的list
				list = (List<Lecture>) message.obj;
				// 如果list为空，则提示未响应
				if (list == null) {
					loadErrorLayout.setVisibility(View.VISIBLE);
					ImageView loadErrorView = (ImageView) loadErrorLayout
							.findViewById(R.id.load_error_view);
					loadErrorView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							getInfoByRefreshImage();
							loadErrorLayout.setVisibility(View.GONE);
						}
					});
				}
				// 如果list包含的内容为空，则提示无讲座信息
				else if (list.isEmpty()) {
					if (host != null) {
						noLecture.setText("暂无讲座信息~");
						noLecture.setVisibility(View.VISIBLE);
						addLecture.setVisibility(View.VISIBLE);
						addLecture.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								Intent intent = new Intent(Hot.this,
										LectureInfo.class);
								intent.putExtra("stateFlag",
										Constant.STATE_FLAG_UPLOAD);
								startActivity(intent);
								Hot.this.finish();
							}
						});
					}
					list = null;
				}
				// 如果list中讲座信息的主键为空，则提示连接失败
				else if (list.get(0).getSpeaker() == null) {
					list = null;
					loadErrorLayout.setVisibility(View.VISIBLE);
					ImageView loadErrorView = (ImageView) loadErrorLayout
							.findViewById(R.id.load_error_view);
					loadErrorView.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							getInfoByRefreshImage();
							loadErrorLayout.setVisibility(View.GONE);
						}
					});
				} else {
					// 创建自定义adapter处理list中的信息
					
					if(listView.getAdapter() != null){
						lectureAdapter.notifyDataSetChanged();
					}else{
						lectureAdapter = new LectureAdapter(list, Hot.this);
						listView.setAdapter(lectureAdapter);
					}
					currentPage ++;
					loadMoreView.setText("加载更多...");
					loadMoreView.setClickable(true);
					// 创立自定义ListView，实现下拉刷新功能
					listView.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							if(position + 1 == listView.getCount()){
								loadMoreView.setText("正在加载，请稍后...");
								refresh();
								return;
							}
							System.out.println("点击的位置：" + position);
							// 自定义的ListView，空间的位置是从0开始！！
							Lecture lecture = list.get(position - 1);
							// 跳转到主View中显示详细讲座信息
							Intent intent = new Intent();
							intent.setClass(Hot.this, LectureInfo.class);
							intent.putExtra("lecture", lecture);
							intent.putExtra("stateFlag", stateFlag);
							intent.putExtra("host", host);
							startActivity(intent);
						}
					});
					listView.setonRefreshListener(new OnRefreshListener() {
						public void onRefresh() {
							getInfoByPullRefreshListView();
						}
					});
				}
				break;
			case HANDLER_LOAD_COMPLETED:
				loadMoreView.setText("数据加载全部");
				loadMoreView.setClickable(false);
				break;
			}
			// 关闭进度对话框
			if (progressDialog != null)
				progressDialog.dismiss();
			listView.onRefreshComplete();
			if (head != null)
				head.setVisibility(View.GONE);
			if (refreshBar != null)
				refreshBar.setVisibility(View.GONE);
			if (refresh != null)
				refresh.setVisibility(View.VISIBLE);
		}
	};

	private void getInfoOnViewLoad() {
		progressDialog = ViewUtil.initProgressDialog(Hot.this,
				getApplicationContext().getString(R.string.loaddata));
		progressDialog.setOnDismissListener(new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {
				System.out.println("dismessed !! Stop refreshThread");
				refreshThread.interrupt();
				refreshBar.setVisibility(View.GONE);
				refresh.setVisibility(View.VISIBLE);
			}
		});
		refresh();
	}

	private void getInfoByPullRefreshListView() {
		currentPage = 1;
		refresh();
	}

	private void getInfoByRefreshImage() {
		currentPage = 1;
		head = (RelativeLayout) findViewById(R.id.hot_contentLayout);
		lastUpdatedTextView = (TextView) findViewById(R.id.hot_lastUpdatedTextView);
		lastUpdatedTextView.setText("更新于:" + new Date().toLocaleString());
		head.setVisibility(View.VISIBLE);
		refresh();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (refreshBar != null)
				refreshBar.setVisibility(View.GONE);
			if (refresh != null)
				refresh.setVisibility(View.VISIBLE);
			if (head != null)
				head.setVisibility(View.GONE);
			if (listView != null)
				listView.onRefreshComplete();
			System.out.println("is refreshThread alive?:"
					+ refreshThread.isAlive());
			if (refreshThread.isAlive()) {
				System.out.println("Stop refreshThread");
				refreshThread.interrupt();
				return false;
			} else if (!refreshThread.isAlive() && host == null
					&& searchContent == null) {
				ViewUtil.onBackPressed_local(this);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void refresh() {
		refresh.setVisibility(View.GONE);
		refreshBar.setVisibility(View.VISIBLE);
		refreshThread = new Thread() {
			public void run() {
				try {
					long startTime = SystemClock.currentThreadTimeMillis();
					List<Lecture> resultList = null;
					while (!(refreshThread.isInterrupted() || Thread
							.interrupted())) {
						if (searchContent == null) {
							List<NameValuePair> datas = new ArrayList<NameValuePair>();
							NameValuePair currentPageData = new BasicNameValuePair(
									"currentPage", currentPage + "");
							NameValuePair pageSizeData = new BasicNameValuePair(
									"pageSize", pageSize + "");
							datas.add(currentPageData);
							datas.add(pageSizeData);
							if(host != null && !host.isEmpty()){
								NameValuePair hostData = new BasicNameValuePair(
										"host", host);
								datas.add(hostData);
							}
							resultList = GetLectureData.getLectureData(HttpUtil.LOAD_MORE, datas);
						} else {
							NameValuePair lecture = new BasicNameValuePair(
									"lecture", searchContent);
							List<NameValuePair> searchList = new ArrayList<NameValuePair>();
							searchList.add(lecture);
							resultList = GetLectureData.getLectureData(
									HttpUtil.LECTURE_URL, searchList);
						}
						long endTime = SystemClock.currentThreadTimeMillis();
						System.out.println("加载数据用时：" + (endTime - startTime));
						Message message = new Message();
						if(list != null && !list.isEmpty() && (resultList == null || (resultList != null && resultList.isEmpty()))){
							message.what = HANDLER_LOAD_COMPLETED;
						}else{
							if(resultList != null && !resultList.isEmpty()){
								if(list == null){
									list = new ArrayList<Lecture>();
								}
								if(currentPage == 1){
									list.clear();
								}
								list.addAll(resultList);
							}else if(resultList == null){
								list = null;
							}
							message.what = HANDLER_LOAD_MORE;
						}
						message.obj = list;
						handler.sendMessage(message);
						throw new InterruptedException();
					}
					throw new InterruptedException();
				} catch (InterruptedException e) {
					System.out.println("refresh is interruped!!");
				}
			}
		};
		refreshThread.start();
	}

	private void checkSkin() {
		switch (listenLecture.getSkinFlag()) {
		case Constant.BLUE:
			addLecture.setBackgroundResource(R.drawable.btn_blue);
			break;
		case Constant.DARK_RED:
			addLecture.setBackgroundResource(R.drawable.btn_dark_red);
			break;
		case Constant.RED:
			addLecture.setBackgroundResource(R.drawable.btn_red);
			break;
		case Constant.GRAY:
			addLecture.setBackgroundResource(R.drawable.btn_gray);
			break;
		case Constant.GREEN:
			addLecture.setBackgroundResource(R.drawable.btn_green);
			break;
		default:
			addLecture.setBackgroundResource(R.drawable.btn_blue);
			break;
		}
	}

	protected void onPause() {
		super.onPause();
		GFAgent.onPause(this);
	}
}
