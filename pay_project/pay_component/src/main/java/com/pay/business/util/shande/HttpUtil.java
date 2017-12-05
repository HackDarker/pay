package com.pay.business.util.shande;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

// Referenced classes of package cn.com.sand.online.agent.service.sdk:
//			EncyptUtil

public class HttpUtil
{

	public static String post(String url, String merchId, String transCode, String data
			,String pubKey,String priKey)
		throws Exception
	{
		String res = post(url, ShandeSign.genEncryptData(merchId, transCode, data,pubKey,priKey));
		if (res == null)
			return null;
		else
			return ShandeSign.decryptRetData(res,pubKey,priKey);
	}

	private static String post(String url, List formparams){
		CloseableHttpResponse response = null;
		CloseableHttpClient httpclient = null;
		String s="";
		try {
			HttpPost httppost;
			httpclient = HttpClients.createDefault();
			httppost = new HttpPost(url);
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(uefEntity);
			System.out.println("executing request url:{} "+ httppost.getURI());
			response = httpclient.execute(httppost);
			String res;
			HttpEntity entity = response.getEntity();
			res = EntityUtils.toString(entity, "UTF-8");
			res = URLDecoder.decode(res, "UTF-8");
			
			System.out.println("res:{}"+ res);
			s = res;
		}catch (IOException e)
		{
			e.printStackTrace();
		}finally{
			try {
				if(response!=null)
					response.close();
				if(httpclient!=null)
					httpclient.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return s;
	}

}
