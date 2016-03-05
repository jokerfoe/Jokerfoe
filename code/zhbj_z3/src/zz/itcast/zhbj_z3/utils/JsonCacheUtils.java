package zz.itcast.zhbj_z3.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 缓存 josn 串的工具类
 * @author Administrator
 *
 */
public class JsonCacheUtils {

	
	private static final String SP_NAME = "json_cache";

	/**
	 * 存储json 数据
	 * @param ctx
	 * @param key json 串的唯一标示
	 * @param value
	 */
	public static void saveCache(Context ctx,String key,String value){
		
		SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		
		sp.edit().putString(key, value).commit();
		
	}
	
	/**
	 * 取缓存的json 串
	 * @param ctx 
	 * @param key json 串的唯一标示
	 * @return
	 * 可能是null 
	 */
	public static String getCache(Context ctx , String key){
		SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		
		return sp.getString(key, null);
	}
	
}
