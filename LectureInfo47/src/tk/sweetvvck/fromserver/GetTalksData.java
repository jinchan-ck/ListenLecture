package tk.sweetvvck.fromserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import tk.sweetvvck.talks.Talks;
import tk.sweetvvck.utils.HttpUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetTalksData {

	/**
	 * 将从服务器端获得的数据转化为存放对象的List
	 * 
	 * @return
	 */
	public static List<Talks> getTalksData(List<NameValuePair> nameValuePairs) {
		List<Talks> list = new ArrayList<Talks>();
		String data = HttpUtil.getData(HttpUtil.GET_TALKS_URL, nameValuePairs);
		if (data == null) {
			list.add(new Talks());
			return list;
		} else if (data != null && !(data.startsWith("<"))) {
			System.out.println(data);
			Gson gson = new Gson();
			list = gson.fromJson(data, new TypeToken<List<Talks>>() {
			}.getType());
			return list;
		} else
			return null;
	}
}
