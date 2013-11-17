package tk.sweetvvck.utils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListHeightUtils {


	/**
	 * 设置listview的高度。解决listview嵌套listview的时候，无法完整显示里边listview的高度问题。
	 * 使用条件：item必须为LinearLayout
	 * 用法：在setAdapter之后调用。
	 * @param listView 需要被计算高度的listview（子listview）
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if(listItem != null)
            	listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}

