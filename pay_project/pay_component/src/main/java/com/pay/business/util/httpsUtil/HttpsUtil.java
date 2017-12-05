package com.pay.business.util.httpsUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.core.teamwork.base.util.http.HttpUtil;

//import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: HttpTest
 * @Description: 自己开始封装的HTTP连接工具,http连接传递的参数封装到一个对象里面，
 *               http中get请求时，是把参数拼接到url后面的，而post请求直接输出即可
 * @author: LUCKY
 * @date:2016年1月6日 下午3:49:28
 */
public class HttpsUtil {
	private static final Logger LOGGER = Logger.getLogger(HttpUtil.class);
	
	private static class MyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class MyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
	
	/**
	 * httpClient post请求
	 * @param url
	 * @return 状态
	 */
	public static int doPost(String url,Map<String,Object> map,String charset){  
        HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
            httpClient = new SSLClient();  
            httpPost = new HttpPost(url);  
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  
            // 设置连接超时时间
         	HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
         					20000);
         	// 设置读数据超时时间
            HttpConnectionParams.setSoTimeout(httpPost.getParams(), 20000);
            HttpResponse response = httpClient.execute(httpPost);  
            
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info(url+"访问http状态码："+statusCode);
            /*if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            }  */
            return statusCode;
        }catch(Exception ex){  
            ex.printStackTrace();  
            LOGGER.info(url+"访问http状态码："+500);
            return 500;
        } finally {
			if (null != httpClient) {
				httpClient.getConnectionManager().shutdown();
			}
		} 
    }  
	
	/**
	 * httpClient post请求
	 * @param url
	 * @return 字符串
	 */
	public static String doPostString(String url,Map<String,Object> map,String charset){  
        HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
            httpClient = new SSLClient();  
            httpPost = new HttpPost(url);  
            //设置参数  
            List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  
            // 设置连接超时时间
         	HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
         					20000);
         	// 设置读数据超时时间
            HttpConnectionParams.setSoTimeout(httpPost.getParams(), 20000);
            HttpResponse response = httpClient.execute(httpPost);  
            
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info(url+"访问http状态码："+statusCode);
            if (statusCode != 200) {
				return "";
			} else {
	            if(response != null){  
	                HttpEntity resEntity = response.getEntity();  
	                if(resEntity != null){  
	                    result = EntityUtils.toString(resEntity,charset);  
	                    return result;
	                }  
	            }
	            return "";
			}
        }catch(Exception ex){  
            ex.printStackTrace();  
            return "";
        }finally {
			if (null != httpClient) {
				httpClient.getConnectionManager().shutdown();
			}
		}  
    } 

	/**
	 * httpClient get请求
	 * @param url
	 * @return 字符串
	 */
	public static String doGetString(String url){
		HttpClient httpClient = null;
        String result="";
        try {
            // 根据地址获取请求
            HttpGet request = new HttpGet(url);//这里发送get请求
            // 获取当前客户端对象
            httpClient = new SSLClient();
            
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000);//设置请求超时10秒  
            HttpConnectionParams.setSoTimeout(request.getParams(), 10*1000); //设置等待数据超时10秒  
            
            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info(url+"访问http状态码："+statusCode);
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (statusCode == HttpStatus.SC_OK) {
                result= EntityUtils.toString(response.getEntity(),"utf-8");
            } 
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
			if (null != httpClient) {
				httpClient.getConnectionManager().shutdown();
			}
		} 
        return result;
    }
	
	/**
	 * httpClient get请求
	 * @param url
	 * @return 状态码
	 */
	public static int doGet(String url){
		HttpClient httpClient = null;
        try {
            // 根据地址获取请求
            HttpGet request = new HttpGet(url);//这里发送get请求
            // 获取当前客户端对象
            httpClient = new SSLClient();
            
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10*1000);//设置请求超时10秒  
            HttpConnectionParams.setSoTimeout(request.getParams(), 10*1000); //设置等待数据超时10秒  
            
            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            LOGGER.info(url+"访问http状态码："+statusCode);
            return statusCode;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGGER.info(url+"访问http状态码："+500);
            return 500;
        }finally {
			if (null != httpClient) {
				httpClient.getConnectionManager().shutdown();
			}
		} 
    }
	
	/**
     * HttpURLConnection 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendHttpGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            connection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            System.out.println("--->遍历所有的响应头字段<---");
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));//防止乱码
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if(connection!=null){
                	connection.disconnect();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
    /** 
     * URLConnection 向指定URL发送GET方法的请求 
     *  
     * @param url 
     *            发送请求的URL 
     * @param param 
     *            请求参数，请求参数应该是name1=value1&name2=value2的形式。 
     * @return URL所代表远程资源的响应 
     */  
    public static String sendGet(String url, String param) {  
        String result = "";  
        BufferedReader in = null;  
        try {  
            String urlName = url + "?" + param;  
            URL realUrl = new URL(urlName);  
            // 打开和URL之间的连接  
            URLConnection conn = realUrl.openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
            // 建立实际的连接  
            conn.connect();  
            // 获取所有响应头字段  
            Map<String, List<String>> map = conn.getHeaderFields();  
            // 遍历所有的响应头字段  
            for (String key : map.keySet()) {  
                System.out.println(key + "--->" + map.get(key));  
            }  
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += line;  
            }  
        } catch (Exception e) {  
            System.out.println("发送GET请求出现异常！" + e);  
            e.printStackTrace();  
        }  
        // 使用finally块来关闭输入流  
        finally {  
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
    
    /**
     * URLConnection 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        URLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            
            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            System.out.println("--->遍历所有的响应头字段<---");
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
    }  
    
    /**
	 * 
	 * HTTP协议POST请求方法
	 */
	public static String sendHttpPost(String url, String params, String gb) {
		if (null == gb || "".equals(gb)) {
			gb = "UTF-8";
		}
		StringBuffer sb = new StringBuffer();
		URL urls;
		HttpURLConnection uc = null;
		BufferedReader in = null;
		try {
			urls = new URL(url);
			uc = (HttpURLConnection) urls.openConnection();
			uc.setRequestMethod("POST");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setUseCaches(false);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			uc.connect();
			DataOutputStream out = new DataOutputStream(uc.getOutputStream());
			out.write(params.getBytes(gb));
			out.flush();
			out.close();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream(),
					gb));
			String readLine = "";
			while ((readLine = in.readLine()) != null) {
				sb.append(readLine);
			}
			if (in != null) {
				in.close();
			}
			if (uc != null) {
				uc.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (uc != null) {
					uc.disconnect();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
    
    /**
     * HttpsURLConnection 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendHttpsPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        HttpsURLConnection conn = null;
        try {
        	SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new java.security.SecureRandom());
			
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpsURLConnection)realUrl.openConnection();
            
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new MyHostnameVerifier());
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            
            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            System.out.println("--->遍历所有的响应头字段<---");
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
                if(conn!=null){
                	conn.disconnect();
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return result;
    }  
}