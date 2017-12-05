package com.pay.business.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class StringHandleUtil {
	
	/**
	 * 参数串转map
	 * @param paramString(key1=value1&key2=value2&key3=value3&key4=value4)
	 * @return
	 */
	public static Map<String,Object> toMap(String paramString){
		Map<String,Object> map = new HashMap<>();
		if(StringUtils.isNotBlank(paramString)){
			String [] paramArr = paramString.split("&");
			if(paramArr.length>0){
				for (String param : paramArr) {
					String [] keyValueArr = param.split("=",2);
					if(keyValueArr.length==2){
						map.put(keyValueArr[0], keyValueArr[1]);
					}else if(keyValueArr.length==1){
						map.put(keyValueArr[0], "");
					}else{
						new HashMap<>();
					}
				}
			}
		}
		return map;
	}
}
