package tk.sweetvvck.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.sweetvvck.R;
import tk.sweetvvck.talks.Talks;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 自定义ListViewAdapter 用于显示Recommend界面的信息
 * 
 * @author sweetvvck
 * 
 */
public class TalksAdapter extends BaseAdapter {

	private List<Talks> list = null;
	// 作为View的缓存，如果该行已经存在，则无需再次创建
	private Map<Integer, View> viewRows = new HashMap<Integer, View>();
	private Context context = null;

	public TalksAdapter(List<Talks> list, Context context) {
		// 若获得的list为空，则创建一个list对象，防止空指针异常
		if (list == null) {
			list = new ArrayList<Talks>();
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
		if (view == null) {
			// 在传入的context界面中通过xml文件获得一个View
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			view = layoutInflater.inflate(R.layout.talks, null);
			TextView notes = (TextView) view.findViewById(R.id.talks_notes);
			TextView username = (TextView) view
					.findViewById(R.id.talks_username);
			Talks talks = list.get(position);
			username.setTextColor(Color.BLUE);
			username.setText(talks.getUsername());
			notes.setText(talks.getNotes());
			viewRows.put(position, view);
		}
		return view;
	}
}
