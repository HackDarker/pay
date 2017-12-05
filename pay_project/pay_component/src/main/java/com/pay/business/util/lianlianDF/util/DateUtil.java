package com.pay.business.util.lianlianDF.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Title: DateUtil.java
 * @Package com.pay.business.util.lianlianDF.util
 * @Description: 时间格式化工具类：解决SimpleDateFormat线程不安全问题
 * @author ZHOULIBO
 * @date 2017年10月19日 上午11:49:21
 * @version V1.0
 */
public class DateUtil {
	private static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>();
	public static void set(SimpleDateFormat sdf){
		local.set(sdf);
	}
	public static void get(){
		local.get();
	}
	public static void remove(){
		local.remove();
	}
	/**
	 * parse 
	 * @param str			时间
	 * @param dateFormat	时间格式化 格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws Exception    设定文件 
	 * Date    返回类型
	 */
    public static Date parse(String str,String dateFormat) throws Exception {  
        SimpleDateFormat sdf = local.get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat(dateFormat, Locale.US);  
            local.set(sdf);  
        }  
        return sdf.parse(str);  
    }  
     /**
      * 
      * format 
      * @param date			时间
      * @param dateFormat	时间格式化 格式 ：yyyy-MM-dd HH:mm:ss
      * @return
      * @throws Exception    设定文件 
      * String    返回类型
      */
    public static String format(Date date,String dateFormat) throws Exception {  
        SimpleDateFormat sdf = local.get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat(dateFormat, Locale.US);  
            local.set(sdf);  
        }  
        return sdf.format(date);  
    }  
   public static void main(String[] args) throws Exception{
//	  String time= format(new Date(), "yyyy-MM-dd HH:mm:ss");
//	  System.out.println(time);
//	  Date date=  parse(time, "yyyy-MM-dd HH:mm:ss");
//	  System.out.println(date);
	  String time1= format(new Date(), "yyyyMMddHHmmss");
	  System.out.println(time1);
}
}
