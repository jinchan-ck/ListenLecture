package tk.sweetvvck.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import tk.sweetvvck.utils.ViewUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class Upload extends Activity {
	private ListenLecture listenLecture = null;
	private static final int SHOW_DATE_DIALOG = 1;
	private static final int SHOW_START_TIME_DIALOG = 2;
	private static final int SHOW_END_TIME_DIALOG = 3;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int startHour;
	private int endHour;
	private int startMinute;
	private int endMinute;
	private DatePickerDialog myDateDialog = null;
	private TimePickerDialog startTimeDialog = null;
	private TimePickerDialog endTimeDialog = null;
	private String onlyDate = null;
	private String startTime = null;
	private String endTime = null;
	private Calendar calendar = null;

	private static final int MESSAGETYPE_01 = 0x0001;

	private AlertDialog progressDialog;

	private EditText lectureNameEdit = null;
	private EditText hostNameEdit = null;
	private EditText dateEdit = null;
	private EditText addressEdit = null;
	private EditText speakerEdit = null;
	private EditText contentEdit = null;

	private Button uploadButton = null;
	private Button resetButton = null;

	private String lectureNameEditContent = null;
	private String dateEditContent = null;
	private String hostNameEditContent = null;
	private String addressEditContent = null;
	private String speakerEditContent = null;
	private String contentEditContent = null;

	private String uploadState = Constant.UPLOAD_FAILEDLLY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ExitApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);
		initView();
		checkSkin();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GFAgent.onResume(this);
		checkSkin();
	}

	private void initView() {
		listenLecture = (ListenLecture) getApplicationContext();
		lectureNameEdit = (EditText) findViewById(R.id.upload_lecture_content);
		dateEdit = (EditText) findViewById(R.id.upload_date_content);
		addressEdit = (EditText) findViewById(R.id.upload_address_content);
		hostNameEdit = (EditText) findViewById(R.id.upload_host_content);
		speakerEdit = (EditText) findViewById(R.id.upload_speaker_content);
		contentEdit = (EditText) findViewById(R.id.upload_content_content);

		uploadButton = (Button) findViewById(R.id.upload_upload_btn);
		resetButton = (Button) findViewById(R.id.upload_reset_btn);
		calendar = Calendar.getInstance();
		dateEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mYear = calendar.get(Calendar.YEAR);
				mMonth = calendar.get(Calendar.MONTH);
				mDay = calendar.get(Calendar.DAY_OF_MONTH);
				startHour = calendar.get(Calendar.HOUR_OF_DAY);
				endHour = calendar.get(Calendar.HOUR_OF_DAY);
				startMinute = calendar.get(Calendar.MINUTE);
				endMinute = calendar.get(Calendar.MINUTE);
				showDialog(SHOW_DATE_DIALOG);
			}
		});

		uploadButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getContent();
				validate();
			}
		});

		resetButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				clearData();
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result = null;
		if (id == SHOW_DATE_DIALOG) {
			myDateDialog = new DatePickerDialog(this, datelistener, mYear,
					mMonth, mDay);
			myDateDialog.setButton(DialogInterface.BUTTON_POSITIVE, "选择开始时间",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							myDateDialog.onClick(dialog, which);
							onlyDate = updateDateDisplay();
							if (checkDate()) {
								showDialog(SHOW_START_TIME_DIALOG);
							} else {
								showDialog(SHOW_DATE_DIALOG);
								Toast.makeText(Upload.this.getApplication(),
										"日期不能小于今日日期!", Toast.LENGTH_LONG)
										.show();
							}
						}
					});
			result = myDateDialog;
		} else if (id == SHOW_START_TIME_DIALOG) {
			startTimeDialog = new TimePickerDialog(this, startTimeSetListener,
					startHour, startMinute, true);
			startTimeDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					"选择结束时间", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							startTimeDialog.onClick(dialog, which);
							startTime = updateStartTimeDisplay();
							if (checkStartTime()) {
								showDialog(SHOW_END_TIME_DIALOG);
							} else {
								Toast.makeText(getApplication(), "时间不能小于此刻时间!",
										Toast.LENGTH_LONG).show();
							}
						}
					});
			result = startTimeDialog;
		} else if (id == SHOW_END_TIME_DIALOG) {
			endTimeDialog = new TimePickerDialog(this, endTimeSetListener,
					endHour, endMinute, true);
			endTimeDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							endTimeDialog.onClick(dialog, which);
							endTime = updateEndTimeDisplay();
							if (checkEndTime()) {
								updateDateEdit();
							} else if (!checkEndTime()) {
								Toast.makeText(getApplication(),
										"结束时间不能小于开始时间!", Toast.LENGTH_LONG)
										.show();
							}
						}
					});
			result = endTimeDialog;
		}
		return result;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SHOW_DATE_DIALOG:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case SHOW_START_TIME_DIALOG:
			((TimePickerDialog) dialog).updateTime(startHour, startMinute);
			break;
		case SHOW_END_TIME_DIALOG:
			((TimePickerDialog) dialog).updateTime(endHour, endMinute);
			break;
		}
	}

	protected boolean checkEndTime() {
		boolean stateFlag = false;
		int sHour = Integer.parseInt(startTime.split(":")[0]);
		int sMinute = Integer.parseInt(startTime.split(":")[1]);
		int eHour = Integer.parseInt(endTime.split(":")[0]);
		int eMinute = Integer.parseInt(endTime.split(":")[1]);
		if (eHour > sHour)
			stateFlag = true;
		else if (eHour == sHour) {
			if (eMinute > sMinute)
				stateFlag = true;
			else
				stateFlag = false;
		} else if (eHour < sHour)
			stateFlag = false;
		return stateFlag;
	}

	protected boolean checkStartTime() {
		System.out.println("startTime is : " + startTime + ", month is :"
				+ mMonth + ", Sys Month is :" + calendar.get(Calendar.MONTH));
		boolean stateFlag = false;
		int sHour = Integer.parseInt(startTime.split(":")[0]);
		int sMinute = Integer.parseInt(startTime.split(":")[1]);
		if (mYear == calendar.get(Calendar.YEAR)) {
			if (mMonth == calendar.get(Calendar.MONDAY)) {
				if (mDay == calendar.get(Calendar.DAY_OF_MONTH)) {
					if (sHour > calendar.get(Calendar.HOUR_OF_DAY))
						stateFlag = true;
					else if (sHour == calendar.get(Calendar.HOUR_OF_DAY)) {
						if (sMinute >= calendar.get(Calendar.MINUTE))
							stateFlag = true;
						else
							stateFlag = false;
					} else if (sHour < calendar.get(Calendar.HOUR_OF_DAY))
						stateFlag = false;
				} else
					stateFlag = true;
			} else
				stateFlag = true;
		} else
			stateFlag = true;
		return stateFlag;
	}

	protected boolean checkDate() {
		if (mYear > calendar.get(Calendar.YEAR))
			return true;
		else if (mYear == calendar.get(Calendar.YEAR)) {
			if (mMonth > calendar.get(Calendar.MONDAY))
				return true;
			else if (mMonth == calendar.get(Calendar.MONDAY)) {
				if (mDay >= calendar.get(Calendar.DAY_OF_MONTH))
					return true;
				else
					return false;
			} else
				return false;
		} else
			return false;
	}

	protected void updateDateEdit() {

		String dateResult = new StringBuilder().append(onlyDate).append(",")
				.append(startTime).append("-").append(endTime).toString();
		dateEdit.setText(dateResult);
	}

	/**
	 * 更新时间显示
	 */
	private String updateStartTimeDisplay() {
		String time = new StringBuilder().append(startHour).append(":")
				.append((startMinute < 10) ? "0" + startMinute : startMinute)
				.toString();
		return time;
	}

	/**
	 * 更新时间显示
	 */
	private String updateEndTimeDisplay() {
		String time = new StringBuilder().append(endHour).append(":")
				.append((endMinute < 10) ? "0" + endMinute : endMinute)
				.toString();
		return time;
	}

	/**
	 * 开始时间控件事件
	 */
	private TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			startHour = hourOfDay;
			startMinute = minute;
			startTime = updateStartTimeDisplay();
		}
	};

	/**
	 * 结束时间控件事件
	 */
	private TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			endHour = hourOfDay;
			endMinute = minute;
			endTime = updateEndTimeDisplay();
		}
	};

	// 设置日期对话框的回调方法
	private OnDateSetListener datelistener = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			onlyDate = updateDateDisplay();
		}
	};

	/**
	 * 更新日期显示
	 */
	private String updateDateDisplay() {
		// 系统的月份比我们期望的月份小一
		String date = new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay).toString();
		return date;
	}

	private void clearData() {
		lectureNameEdit.setText("");
		dateEdit.setText("");
		hostNameEdit.setText("");
		addressEdit.setText("");
		speakerEdit.setText("");
		contentEdit.setText("");
	}

	private void getContent() {
		lectureNameEditContent = lectureNameEdit.getText().toString().trim();
		dateEditContent = dateEdit.getText().toString().trim();
		hostNameEditContent = hostNameEdit.getText().toString().trim();
		addressEditContent = addressEdit.getText().toString().trim();
		speakerEditContent = speakerEdit.getText().toString().trim();
		contentEditContent = contentEdit.getText().toString().trim();
	}

	private void validate() {
		if (lectureNameEditContent.length() == 0
				|| dateEditContent.length() == 0
				|| hostNameEditContent.length() == 0
				|| addressEditContent.length() == 0
				|| speakerEditContent.length() == 0
				|| contentEditContent.length() == 0) {
			uploadState = "内容均不能为空！";
			Toast.makeText(getApplication(), uploadState, Toast.LENGTH_LONG)
					.show();
		}

		else
			uploadLectureInfo();
	}

	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap = (Map<String, Object>) message.obj;
				uploadState = (String) resultMap.get("result");
				Lecture lecture = (Lecture) resultMap.get("lecture");
				if (progressDialog != null)
					progressDialog.dismiss();
				if (uploadState.equals(Constant.UPLOAD_FAILEDLLY)) {
					System.out.println("adgdgad");
					Toast.makeText(getApplicationContext(), "发布失败，该讲座信息以存在！",
							Toast.LENGTH_LONG).show();
				} else if (uploadState.equals(Constant.UPLOAD_SUCCESSFULLY))
					showSuccessDialog(lecture);
				break;
			}
		}
	};

	private void uploadLectureInfo() {
		progressDialog = ViewUtil.initProgressDialog(Upload.this,
				getApplicationContext().getString(R.string.uploading));
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				progressDialog.dismiss();
			}
		});
		new Thread() {
			public void run() {

				long startTime = SystemClock.currentThreadTimeMillis();
				NameValuePair lectureInfoLecture = new BasicNameValuePair(
						"lecture", lectureNameEditContent);
				NameValuePair lectureInfoDate = new BasicNameValuePair("date",
						dateEditContent);
				NameValuePair lectureInfoHost = new BasicNameValuePair("host",
						hostNameEditContent);
				NameValuePair lectureInfoAddress = new BasicNameValuePair(
						"address", addressEditContent);
				NameValuePair lectureInfoSpeaker = new BasicNameValuePair(
						"speaker", speakerEditContent);
				NameValuePair lectureInfoContent = new BasicNameValuePair(
						"content", contentEditContent);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(lectureInfoLecture);
				nameValuePairs.add(lectureInfoDate);
				nameValuePairs.add(lectureInfoHost);
				nameValuePairs.add(lectureInfoAddress);
				nameValuePairs.add(lectureInfoSpeaker);
				nameValuePairs.add(lectureInfoContent);
				Lecture lecture = new Lecture();
				lecture.setAddress(addressEditContent);
				lecture.setContent(contentEditContent);
				lecture.setDate(dateEditContent);
				lecture.setHost(hostNameEditContent);
				lecture.setLecture(lectureNameEditContent);
				lecture.setSpeaker(speakerEditContent);

				List<Lecture> list = GetLectureData.getLectureData(
						HttpUtil.ADD_LECTURE_URL, nameValuePairs);

				String result = list.get(0).getSpeaker();
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("result", result);
				resultMap.put("lecture", lecture);
				long endTime = SystemClock.currentThreadTimeMillis();
				try {
					if ((endTime - startTime) < 2000)
						Thread.sleep(2000 - (endTime - startTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("sleep time is "
						+ (2000 - (endTime - startTime)) + "毫秒");
				Message message = new Message();
				message.what = MESSAGETYPE_01;
				message.obj = resultMap;
				handler.sendMessage(message);

			}
		}.start();
	}

	private void showSuccessDialog(final Lecture lecture) {
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("发布成功")
				// 设置标题
				.setMessage("系统提示")
				.setIcon(R.drawable.logo_mini)
				// 设置内容
				.setPositiveButton("查看已发讲座", // 设置确定按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								clearData();
								Intent intent = new Intent();
								intent.setClass(Upload.this, LectureInfo.class);
								intent.putExtra("lecture", lecture);
								intent.putExtra("stateFlag",
										Constant.STATE_FLAG_DETAIL_INFO);
								intent.putExtra("host", "您刚才发的讲座");
								startActivity(intent);
							}
						})
				.setNeutralButton("再发一条",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								clearData();
								return;
							}
						}).create(); // 创建按钮
		dialog.show(); // 显示对话框
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
			uploadButton.setBackgroundResource(R.drawable.btn_blue);
			resetButton.setBackgroundResource(R.drawable.btn_blue);
			break;
		case Constant.DARK_RED:
			uploadButton.setBackgroundResource(R.drawable.btn_dark_red);
			resetButton.setBackgroundResource(R.drawable.btn_dark_red);
			break;
		case Constant.RED:
			uploadButton.setBackgroundResource(R.drawable.btn_red);
			resetButton.setBackgroundResource(R.drawable.btn_red);
			break;
		case Constant.GRAY:
			uploadButton.setBackgroundResource(R.drawable.btn_gray);
			resetButton.setBackgroundResource(R.drawable.btn_gray);
			break;
		case Constant.GREEN:
			uploadButton.setBackgroundResource(R.drawable.btn_green);
			resetButton.setBackgroundResource(R.drawable.btn_green);
			break;
		default:
			uploadButton.setBackgroundResource(R.drawable.btn_blue);
			resetButton.setBackgroundResource(R.drawable.btn_blue);
			break;
		}
	}

	protected void onPause() {
		super.onPause();
		GFAgent.onPause(this);
	}
}
