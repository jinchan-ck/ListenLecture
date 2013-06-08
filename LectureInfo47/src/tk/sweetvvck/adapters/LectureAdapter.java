package tk.sweetvvck.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.sweetvvck.R;
import tk.sweetvvck.lecture.Lecture;
import tk.sweetvvck.utils.SystemUtil;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 自定义ListViewAdapter
 * 用于显示Recommend界面的信息
 * @author sweetvvck
 *
 */
public class LectureAdapter extends BaseAdapter {
	
	private List<Lecture> list = null;
	//作为View的缓存，如果该行已经存在，则无需再次创建
	private Map<Integer,View> viewRows = new HashMap<Integer, View>();
	private Context context = null;
	
	public LectureAdapter(List<Lecture> list,Context context){
		//若获得的list为空，则创建一个list对象，防止空指针异常
		if(list == null){
			list = new ArrayList<Lecture>();
		}
		this.list = list;
		this.context = context;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = viewRows.get(position);
		if(view == null){
			//在传入的context界面中通过xml文件获得一个View
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			view = layoutInflater.inflate(R.layout.lectureinfo, null);
			TextView lectureName = (TextView)view.findViewById(R.id.lectureName);
			//TextView host = (TextView)view.findViewById(R.id.host);
			TextView date = (TextView)view.findViewById(R.id.info_date);
			Lecture lecture = list.get(position);
			System.out.println("lecture:"+lecture);
			if(lecture.getDate()==null){
				date.setText("");
			}
			lectureName.setText(lecture.getLecture());
			//host.setText("By:"+lecture.getHost());
			String sysDate = lecture.getDate().split(",")[0];
			System.out.println(sysDate.contains(SystemUtil.getSystemDate()));
			if(sysDate.contains(SystemUtil.getSystemDate())){
				date.setTextColor(Color.RED);
				date.setText("【今天】");
			}
			else {
				date.setText("【"+sysDate+"】");
			}
			viewRows.put(position, view);
		}
		return view;
	}
}
