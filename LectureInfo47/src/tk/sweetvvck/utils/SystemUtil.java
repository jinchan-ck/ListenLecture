package tk.sweetvvck.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemUtil {
	private static SystemUtil instance = new SystemUtil();
	
	private SystemUtil(){}
	
	public static SystemUtil getInstance(){
		return instance;
	}
	public static String getSystemDate(){
		String date = null;
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		date = formatter.format(curDate);  
		System.out.println(date);
		return date;
	}
}
