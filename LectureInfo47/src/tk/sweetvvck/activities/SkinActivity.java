package tk.sweetvvck.activities;

import java.util.ArrayList;
import java.util.List;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.adapters.SkinListAdapter;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.model.SkinListItemData;
import tk.sweetvvck.utils.Constant;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.LoginDialog;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SkinActivity extends Activity {

	private ListenLecture listenLecture = null;

	private final int skinIconID[] = { R.drawable.titlebar_red,
			R.drawable.titlebar_green, R.drawable.titlebar_gray,
			R.drawable.titlebar_dark_red, R.drawable.titlebar_blue };

	private final String text[] = { "纹理红", "橄榄绿", "沟鼠灰", "玻璃棕", "天际蓝" };

	private ListView mListView;

	private SkinListAdapter mSkinListAdapter;

	private TextView mTitleTextView;

	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skin_main_layout);
		init();
	}

	private void init() {
		listenLecture = (ListenLecture) getApplicationContext();
		mListView = (ListView) findViewById(R.id.themelist);

		mSkinListAdapter = new SkinListAdapter(this, getItemList());

		mListView.setAdapter(mSkinListAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSkinListAdapter.setSelect(position);
				mTitleTextView.setBackgroundResource(skinIconID[position]);
				Toast.makeText(SkinActivity.this, "已选择" + text[position],
						Toast.LENGTH_LONG).show();
			}
		});
		mTitleTextView = (TextView) findViewById(R.id.skinTitle);
		checkSkin();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
		checkSkin();
	}

	private List<SkinListItemData> getItemList() {
		List<SkinListItemData> list = new ArrayList<SkinListItemData>();

		for (int i = 0; i < 5; i++) {
			SkinListItemData data = new SkinListItemData();
			data.mImageViewLeftID = skinIconID[i];
			data.mTextView = text[i];
			list.add(data);
		}
		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "退出应用").setIcon(R.drawable.exit_menu_icon);
		menu.add(0, 1, 0, "切换用户").setIcon(R.drawable.menu_logout);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			ExitApplication.getInstance().exit();
		case 1:
			LoginDialog.getInstance().login(SkinActivity.this);
		}
		return super.onOptionsItemSelected(item);
	}

	private void checkSkin() {
		switch (listenLecture.getSkinFlag()) {
		case Constant.BLUE:
			mTitleTextView.setBackgroundResource(R.drawable.titlebar_blue);
			break;
		case Constant.DARK_RED:
			mTitleTextView.setBackgroundResource(R.drawable.titlebar_dark_red);
			break;
		case Constant.RED:
			mTitleTextView.setBackgroundResource(R.drawable.titlebar_red);
			break;
		case Constant.GRAY:
			mTitleTextView.setBackgroundResource(R.drawable.titlebar_gray);
			break;
		case Constant.GREEN:
			mTitleTextView.setBackgroundResource(R.drawable.titlebar_green);
			break;
		default:
			mTitleTextView.setBackgroundResource(R.drawable.titlebar_blue);
			break;
		}
	}

	protected void onPause() {
		super.onPause();
		GFAgent.onPause(this);
	}
}
