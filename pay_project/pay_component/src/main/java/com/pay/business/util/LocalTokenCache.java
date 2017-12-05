package com.pay.business.util;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.core.teamwork.base.util.date.DateStyle;
import com.core.teamwork.base.util.date.DateUtil;

/**
 * @ClassName: LocalTokenCache 
 * @Description: 本地缓存,单例,支持并发,重启失效
 * @author yangyu
 * @date 2016年11月2日 下午4:53:45
 */
public class LocalTokenCache {
	
	private static Logger logger = Logger.getLogger(LocalTokenCache.class);
	
	private final ReentrantLock locks = new ReentrantLock();
	
	volatile private static LocalTokenCache instanceCahce = null;
	
	private final ConcurrentHashMap<String,Object> cacheMap = new ConcurrentHashMap<String,Object>();
	
	//私有构造方法,使用双检查锁机制单例模式
	private LocalTokenCache(){
		
	};
	
	public static LocalTokenCache getInstance(){
		if(instanceCahce==null){
			synchronized(LocalTokenCache.class){
				if(instanceCahce==null){
					instanceCahce = new LocalTokenCache();
				}
			}
		}
		return instanceCahce;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object getCache(String key){
		return cacheMap.get(key);
	}
	
	/**
	 * 先添加再取出
	 * @param key
	 * @param value 为空直接取
	 * @return
	 */
	public Object getPutCache(String key,Object value){
		if(value!=null && StringUtils.isNotBlank(value.toString())){
			putCache(key, value);
		}
		return cacheMap.get(key);
	}
	
	/**
	 * 获取内层key的Value
	 * @param outKey
	 * @param innerKey
	 * @return
	 */
	public Object getCache(String outKey,String innerKey){
		Object obj = cacheMap.get(outKey);
		if(obj instanceof Map){
			return ((Map) obj).get(innerKey);
		}else{
			return obj;
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void putCache(String key,Object value){
		locks.lock();
		try {
			cacheMap.put(key, value);
		} catch (Exception e) {
			logger.error("向本地缓存放入数据异常:"+key, e);
		}finally{
			locks.unlock();
		}
	}
	
	/**
	 * 判断token是否过期(用于一级)
	 * @param timeKey 缓存的那个token时存入的时间key
	 * @param hours 缓存失效时间(小时)
	 * @return key只在不过期情况下返回假,其他情况 如:key不存在 或 真的过期都返回true
	 * @throws Exception 
	 */
	public boolean tokenIsExpire(String timeKey,int hours) throws Exception{
		if(cacheMap.containsKey(timeKey)){
			//存在判断是否过期
			int oldTime = DateUtil.getIntervalHours(new Date(),DateUtil.StringToDate(String.valueOf(cacheMap.get(timeKey)),DateStyle.YYYY_MM_DD_HH_MM_SS));
			if(oldTime>hours){
				//大于 过期
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * 判断token是否过期(用于二级,二级对象为map)
	 * @param timeKey
	 * @param hours
	 * @param outerKey
	 * @return key只在不过期情况下返回假,其他情况 如:key不存在 或 真的过期都返回true
	 * @throws Exception 
	 */
	public boolean tokenIsExpire(String timeKey,int hours,String outerKey) throws Exception{
		if(cacheMap.containsKey(outerKey)){
			Object obj = cacheMap.get(outerKey);
			if(obj instanceof Map){
				Map outerMap = (Map)obj;
				if(outerMap.containsKey(timeKey)){
					int oldTime = DateUtil.getIntervalHours(new Date(),DateUtil.StringToDate(String.valueOf(outerMap.get(timeKey)),DateStyle.YYYY_MM_DD_HH_MM_SS));
					if(oldTime>hours){
						//大于 过期
						return true;
					}else{
						return false;
					}
				}
			}
			return true;
		}else{
			return true;
		}
	}
	
}
