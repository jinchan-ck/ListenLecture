package tk.sweetvvck.utils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListHeightUtils {


        public static void setListViewHeightBasedOnChildren(ListView listView) {
                ListAdapter listAdapter = listView.getAdapter();
                if (listAdapter == null) {
                        // pre-condition
                        return;
                }
                int totalHeight = 0;
                for (int i = 0; i < listAdapter.getCount(); i++) {
                        View listItem = listAdapter.getView(i, null, listView);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                //这里后边应该是减一的，加十的原因是listview添加背景后listview再次显示不完全，原因不明，不过这样能够解决问题
                params.height = totalHeight + ((listView.getDividerHeight()+25) * (listAdapter.getCount()));
                listView.setLayoutParams(params);
        }
}

