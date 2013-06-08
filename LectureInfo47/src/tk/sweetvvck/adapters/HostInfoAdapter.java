package tk.sweetvvck.adapters;

import java.util.ArrayList;
import java.util.List;

import tk.sweetvvck.R;
import tk.sweetvvck.model.HostItem;
import tk.sweetvvck.model.HostItemId;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HostInfoAdapter extends BaseAdapter {
	private List<HostItemId> mList;
	private Context mContext;
	public static final int APP_PAGE_SIZE = 9;

	public HostInfoAdapter(Context context, List<HostItemId> list, int page) {
		mContext = context;

		mList = new ArrayList<HostItemId>();
		int i = page * APP_PAGE_SIZE;
		int iEnd = i + APP_PAGE_SIZE;
		while ((i < list.size()) && (i < iEnd)) {
			mList.add(list.get(i));
			i++;
		}
	}

	public int getCount() {
		return mList.size();
	}

	public Object getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		HostItemId hostItemId = mList.get(position);
		HostItem hostItem;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(R.layout.grid_item,
					null);

			hostItem = new HostItem();
			hostItem.hostImage = (ImageView) v.findViewById(R.id.itemimage);
			hostItem.hostName = (TextView) v.findViewById(R.id.itemtext);

			//将生成的hostItem存在View中，供下次使用，作为缓存
			v.setTag(hostItem);
			convertView = v;
		} else {
			hostItem = (HostItem) convertView.getTag();
		}
		hostItem.hostImage.setImageResource(hostItemId.gethostImageID());
		
		/*TextPaint tp = hostItem.hostName.getPaint(); 
		tp.setFakeBoldText(true);*/  
		hostItem.hostName.setText(hostItemId.gethostNameId());

		return convertView;
	}
}
