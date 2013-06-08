package tk.sweetvvck.fromserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import tk.sweetvvck.lecture.Lecture;
import tk.sweetvvck.utils.HttpUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 进行网络操作，获得服务端的Json数据
 * @author sweetvvck
 *
 */
public class GetLectureData {
	
	/**
	 * 将从服务器端获得的数据转化为存放对象的List
	 * @return
	 */
	public static List<Lecture> getLectureData(String url,List<NameValuePair> nameValuePairs) {
		List<Lecture> list = new ArrayList<Lecture>();
		String data = HttpUtil.getData(url, nameValuePairs);
		if(data == null){
			list.add(new Lecture());
			return list;
		}
		else if(data != null && !(data.startsWith("<"))){
			System.out.println(data);
			Gson gson = new Gson();			
			list = gson.fromJson(data, new TypeToken<List<Lecture>>() {
			}.getType());
			return list;
		}
		else
			return null;		
	}
}
