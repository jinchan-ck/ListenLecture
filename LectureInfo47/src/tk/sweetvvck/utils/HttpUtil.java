package tk.sweetvvck.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

public class HttpUtil {//10.11.75.238:8080  http://www.listenlecture.tk/   http://10.11.75.238:8080/LectureInfo03
	public final static String BASE_URL = "http://listenlecture.duapp.com/lecture/JsonAction.action";
	public final static String HOST_URL = "http://listenlecture.duapp.com/lecture/JsonAction!getLectureByHost.action";
	public final static String LECTURE_URL = "http://listenlecture.duapp.com/lecture/JsonAction!getLectureByLecture.action";
	public final static String ADD_LECTURE_URL = "http://listenlecture.duapp.com/lecture/JsonAction!addLectureInfo.action";
	public final static String LOGIN_URL = "http://listenlecture.duapp.com/lecture/JsonAction!login.action";
	public final static String GET_FAVORITE_URL = "http://listenlecture.duapp.com/lecture/JsonAction!getFavoreateLecture.action";
	public final static String FAVORITE_URL = "http://listenlecture.duapp.com/lecture/JsonAction!favoreate.action";
	public final static String REGISTER_URL = "http://listenlecture.duapp.com/lecture/JsonAction!register.action";
	public final static String GET_TALKS_URL = "http://listenlecture.duapp.com/lecture/JsonAction01.action";
	public final static String ADD_TALKS_URL = "http://listenlecture.duapp.com/lecture/JsonAction!addTalks.action";
	public final static String DELETE_ALL_FAVOTITE_LECTURE = "http://listenlecture.duapp.com/lecture/JsonAction!deleteAllFavorite.action";
	public final static String DELETE_ONE_FAVOTITE_LECTURE = "http://listenlecture.duapp.com/lecture/JsonAction!deleteOneFavorite.action";
	public final static String GET_FAVORITE_NUMBER = "http://listenlecture.duapp.com/lecture/JsonAction!getFavoriteNumber.action";

	public static String getData(String url, List<NameValuePair> nameValuePairs) {
		if (nameValuePairs == null) {
			nameValuePairs = new ArrayList<NameValuePair>();
		}
		String data = "";
		// 创建StringBuffer，
		StringBuffer stringBuffer = new StringBuffer();
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs,
					"utf-8");
			System.out.println("访问的接口是： ----》" + url);
			HttpPost post = new HttpPost(url);
			post.setEntity(httpEntity);
			System.out.println("HttpEntity is : "
					+ new BufferedReader(new InputStreamReader(httpEntity
							.getContent())).readLine());
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line);
				}
			} else
				return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ConnectTimeoutException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		data = stringBuffer.toString();
		return data;
	}

	private static HttpClient getHttpClient() {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				20000); // 超时设置
		client.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 20000);// 连接超时
		return client;
	}
}
