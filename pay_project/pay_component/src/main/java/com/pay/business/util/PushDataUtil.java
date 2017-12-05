package com.pay.business.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.core.teamwork.base.util.push.AndroidPush;
import com.core.teamwork.base.util.push.ApplePush;

/**
 * 推送需要使用的数据,以及异步推送
 * @ClassName: PushDataUtil 
 * @Description: 
 * @author yangyu
 * @date 2016年11月14日 下午2:37:18
 */
public class PushDataUtil {

	private static Logger logger = Logger.getLogger(PushDataUtil.class);
	
	protected static Properties properties = new Properties();
	
	protected static final String IOS_PATH_KEY = "ios_push_path_key";//缓存配置文件路径
	
	protected static final String IOS_PATH_FILENAME = "ios-push-path.p12";//ios签名文件名称
	
	protected static ExecutorService excutorService = Executors.newCachedThreadPool();//使用线程池 多线程发送消息
	
	static{
		try {
			properties = PropertiesLoaderUtils.loadAllProperties("push-config.properties", Thread.currentThread().getContextClassLoader());
		} catch (IOException e) {
			logger.error("读取推送配置文件异常:",e);
		}
	}
	
	public static String getAndroidAppKey(){
		return properties.getProperty("jiguang_android_key");
	}
	
	public static String getAndroidAppMasterSecret(){
		return properties.getProperty("jiguang_android_masterSecret");
	}

	public static void main(String[] args) {
		String filePath = getFilePath("ios-push-path.p12");
		System.out.println(filePath);
	}
	
	public static String getIosPassword(){
		return properties.getProperty("ios_password");
	}
	
	public static String getCacheIosPath() {
		Object cache = LocalTokenCache.getInstance().getCache(IOS_PATH_KEY);
		if(cache==null || "null".equals(cache.toString())){
			synchronized(PushDataUtil.class){
				if(cache==null || "null".equals(cache.toString())){
					return LocalTokenCache.getInstance().getPutCache(IOS_PATH_KEY, getFilePath(IOS_PATH_FILENAME)).toString();
				}else{
					return LocalTokenCache.getInstance().getCache(IOS_PATH_KEY).toString();
				}
			}
		}else{
			return cache.toString();
		}
	}
	
	/**
	 * 获取文件绝对路径 (windows 和 linux 通用)
	 * @param fileName
	 * @return
	 */
	public static String getFilePath(final String fileName){
		String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
		if ("\\".equals(File.separator)) {
			path = path.replace("file:/", "");
		}
		if("/".equals(File.separator)){
			path = path.replace("file:/", "/");
		}
		path += fileName;
		return path;
	}
	
	/**
	 * 异步安卓通知推送
	 * @param paramMap 自定义参数
	 * @param title 标题
	 * @param content 内容(仅适用于安卓)
	 * @param receiver 接收者List<String> 或者 String 类型
	 */
	public static void asyncPoolAndroidPush(Map<String,String> paramMap,String title,String content,Object receiver,int type){
		excutorService.execute(new PushDataUtil().new asyncAndroidPush(paramMap, title, content, receiver,type));
	}
	
	/**
	 * 异步IOS通知推送
	 * @param paramMap 自定义参数
	 * @param tokens	接收者
	 * @param message 标题
	 */
	public static void asyncPoolIosPush(Map<String,String> paramMap,List<String> tokens,String message){
		excutorService.execute(new PushDataUtil().new asyncIosPush(paramMap, tokens, message));
	}
	
	protected class asyncAndroidPush implements Runnable{
		private Map<String,String> map;
		private String title;
		private String content;
		private Object receiver;
		private Integer type;
		public asyncAndroidPush(Map<String,String> paramMap,String title,String content
				,Object receiver,int type){
			this.map = paramMap;
			this.title = title;
			this.content = content;
			this.receiver = receiver;
			this.type=type;
		}
		
		@Override
		public void run() {
			if(receiver instanceof List){
				List<String> receiveList = (List)receiver;
				AndroidPush.pushAlertByMore(title,content, receiveList,getAndroidAppMasterSecret(),getAndroidAppKey(),map,type);
			}else{
				System.out.println(receiver);
				AndroidPush.pushAlertByOne(title,content, String.valueOf(receiver),getAndroidAppMasterSecret(),getAndroidAppKey(),map,type);
			}
		}
	}
	
	protected class asyncIosPush implements Runnable{
		private Map<String,String> map;
		private List<String> tokens;
		private String message;
		public asyncIosPush(Map<String,String> paramMap,List<String> token,String message){
			this.map = paramMap;
			this.tokens = token;
			this.message = message;
		}
		@Override
		public void run() {
			ApplePush.push(tokens, message,getCacheIosPath(),getIosPassword(), map,true);
		}
	}
}
