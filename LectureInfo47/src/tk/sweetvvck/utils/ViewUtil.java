package tk.sweetvvck.utils;

import tk.sweetvvck.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ViewUtil {
	private static MyThread mThread;

	private static ViewUtil instance;

	private static AlertDialog progressDialog;

	private ViewUtil() {
	}

	public static ViewUtil getInstance() {
		instance = new ViewUtil();
		return instance;
	}

	/**
	 * 设置布局被按下的效果 相应栏目点击后的效果，字体变大，透明滑块移到该处
	 * 
	 * @param one
	 * @param two
	 * @param three
	 * @param four
	 * @param five
	 * @author sweetvvck
	 */
	public static void setPressBackGround(TextView one, TextView two,
			TextView three, TextView four, TextView five) {
		one.setTextSize(20);
		two.setTextSize(16);
		three.setTextSize(16);
		four.setTextSize(16);
		five.setTextSize(16);
	}

	/**
	 * 开始移动滑块透明滑块移到该处
	 * 
	 * @param v
	 * @param 滑块起点位置
	 *            position
	 * @param 被移动滑块的布局layout
	 * @author sweetvvck
	 */
	public static void startMove(View v, int position, MoveLayout layout) {
		stopThread(layout);// 停止之前的线程
		mThread = new MyThread(v, position, layout);
		mThread.start();
	}

	private static void stopThread(MoveLayout layout) {
		if (mThread != null) {
			try {
				layout.mIsStop = true;
				mThread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static class MyThread extends Thread {
		private View mView;
		private int postion;
		private MoveLayout layout;

		public MyThread(View v, int postion, MoveLayout layout) {
			this.mView = v;
			this.postion = postion;
			this.layout = layout;
		}

		public void run() {
			layout.doWork(this.mView, postion);
		}

	}

	/**
	 * 点击后弹出对话框提示是否退出程序
	 * 
	 * @param context
	 */
	public static void onBackPressed_local(final Activity context) {
		Dialog dialog = new AlertDialog.Builder(context)
				.setTitle("退出应用")
				// 设置标题
				.setMessage("您确定要退出系统吗?")
				// 设置内容
				.setIcon(R.drawable.logo_mini)
				.setPositiveButton("确定", // 设置确定按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ExitApplication.getInstance().exit();
							}
						})
				.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) { // 点击"退出"按钮之后推出程序
						return;
					}
				}).create(); // 创建按钮
		dialog.show(); // 显示对话框
	}

	/**
	 * 
	 *//*
	public static void initProgressDialog(Context context, CharSequence title,
			CharSequence message) {
		progressDialog = new ProgressDialog(context, R.style.MyDialog);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				progressDialog.dismiss();
			}
		});
	}*/
	/**
	 * 
	 */
	public static AlertDialog initProgressDialog(Context context, String text) {
		LayoutInflater factory = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 得到自定义对话框
		View progressDialogView = factory.inflate(R.layout.progress_dialog, null);
		progressDialog = new AlertDialog.Builder(context).setView(progressDialogView)// 设置自定义对话框样式
				.create();// 创建对话框
		progressDialog.setView(progressDialogView, 0, 0, 0, 0);
		progressDialog.show();
		TextView progressText = (TextView) progressDialog.findViewById(R.id.progress_text);
		progressText.setText(text);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				progressDialog.dismiss();
			}
		});
		return progressDialog;
	}
	
	public static void closeProgressDialog(){
		if (progressDialog != null)
			progressDialog.dismiss();
	}
}
