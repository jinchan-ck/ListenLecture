package tk.sweetvvck.adapters;

import java.util.List;

import tk.sweetvvck.R;
import tk.sweetvvck.application.ListenLecture;
import tk.sweetvvck.model.SkinListItemData;
import tk.sweetvvck.utils.LoginDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SkinListAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;

	private List<SkinListItemData> mItemDataList;

	private ListenLecture listenLecture = null;

	private int mCurSelect = -1;

	public SkinListAdapter(Context context, List<SkinListItemData> itemDatalist) {
		listenLecture = (ListenLecture) context.getApplicationContext();
		mLayoutInflater = LayoutInflater.from(context);
		mItemDataList = itemDatalist;
	}

	public int getCount() {
		if (mItemDataList == null) {
			return 0;
		}

		return mItemDataList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public void setSelect(int pos) {
		if (pos >= 0 && pos < mItemDataList.size()) {
			mCurSelect = pos;
			notifyDataSetChanged();
		}

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.skin_listview_item,
					null);
		}
		ImageView imageViewLeft = (ImageView) convertView
				.findViewById(R.id.imageLeft);
		TextView textView = (TextView) convertView.findViewById(R.id.skinname);

		if (mItemDataList != null) {
			imageViewLeft
					.setImageResource(mItemDataList.get(position).mImageViewLeftID);
			textView.setText(mItemDataList.get(position).mTextView);
			if (position == mCurSelect) {
				System.out.println(position);
				listenLecture.setSkinFlag(position);
				LoginDialog.saveSkinColor(listenLecture, position);
			}
		}
		return convertView;
	}
}
