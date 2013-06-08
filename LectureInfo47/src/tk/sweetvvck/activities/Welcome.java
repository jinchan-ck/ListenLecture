package tk.sweetvvck.activities;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.utils.LoginDialog;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class Welcome extends Activity {
	private Window welcome = null;
	private ListenLecture listenLecture = null;

	// private MediaController ctrl = null;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		welcome = getWindow();
		welcome.setFormat(PixelFormat.RGBA_8888);
		/** 使欢迎界面全屏显示 **/
		welcome.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		listenLecture = (ListenLecture) getApplicationContext();
		setContentView(R.layout.about_us);
		welcome.addFlags(WindowManager.LayoutParams.FLAG_DITHER);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				/* Create an Intent that will start the Main WordPress Activity. */
				LoginDialog.LoadUserDate(Welcome.this);
				if (!listenLecture.isFirstUse()) {
					Intent intent = new Intent();
					intent.setClass(Welcome.this, LectureInfo.class);
					startActivity(intent);
					Welcome.this.finish();
				} else {
					Intent mainIntent = new Intent(Welcome.this,
							GuideView.class);
					Welcome.this.startActivity(mainIntent);
					Welcome.this.finish();
				}
			}
		}, 1500);
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
