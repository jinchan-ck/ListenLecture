package tk.sweetvvck.activities;

import com.gfan.sdk.statitistics.GFAgent;

import tk.sweetvvck.R;
import tk.sweetvvck.utils.ExitApplication;
import tk.sweetvvck.utils.LoginDialog;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AboutUs extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
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
			LoginDialog.getInstance().login(AboutUs.this);
		}
		return super.onOptionsItemSelected(item);
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
