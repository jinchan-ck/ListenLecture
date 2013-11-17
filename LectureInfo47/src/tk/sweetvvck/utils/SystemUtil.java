package tk.sweetvvck.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

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
	
	/**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
	public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
