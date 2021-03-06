package com.pay.business.util.pinganbank.pay;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.commons.lang.RandomStringUtils;

import com.pay.business.util.pinganbank.util.HttpUtil;
import com.pay.business.util.pinganbank.util.HttpsUtil;
import com.pay.business.util.pinganbank.util.TLinxAESCoder;
import com.pay.business.util.pinganbank.util.TLinxMapUtil;
import com.pay.business.util.pinganbank.util.TLinxRSACoder;
import com.pay.business.util.pinganbank.util.TLinxSHA1;
/**
* @Title: AddConfig.java 
* @Package com.pay.business.util.pinganbank.pay 
* @Description: 商户配置公众号支付相关的配置：授权目录
* @author ZHOULIBO   
* @date 2017年8月19日 上午11:42:16 
* @version V1.0
*/
public class AddConfig {
	public static Map<String,Object> addConfig(String ctt_code, String sub_appid, String jsapi_path, String OPEN_ID, String OPEN_KEY, String PRIVATE_KEY, String PUBLICKEY) {
		Map<String,Object> returnMap=new HashMap<String, Object>();
		String timestamp = new Date().getTime() / 1000 + ""; // 时间
		try {

			// 固定参数
			TreeMap<String, String> postmap = new TreeMap<String, String>(); // post请求参数的map
			TreeMap<String, String> getmap = new TreeMap<String, String>(); // get请求参数的map

			getmap.put("open_id", OPEN_ID);
			getmap.put("lang", "zh-cn");
			getmap.put("timestamp", timestamp);
			getmap.put("randStr", RandomStringUtils.randomAlphanumeric(32));

			TreeMap<String, String> datamap = new TreeMap<String, String>(); // data参数的map
			datamap.put("ctt_code", ctt_code);
			if(sub_appid!=null){
			datamap.put("sub_appid", sub_appid);
			}
			if(jsapi_path!=null){
				datamap.put("jsapi_path", jsapi_path);
			}
			/**
			 * 1 data字段内容进行AES加密，再二进制转十六进制(bin2hex)
			 */
			String data = handleEncrypt(datamap, OPEN_KEY);

			postmap.put("data", data);
			System.out.println("=====data=====" + data);

			/**
			 * 2 请求参数签名 按A~z排序，串联成字符串，先进行sha1加密(小写)，再进行RSA加密(小写),二进制转十六进制，得到签名
			 */
			String sign = handleSign(getmap, postmap, PRIVATE_KEY);

			postmap.put("sign", sign);
			System.out.println("=====sign=====" + sign);

			/**
			 * 3 请求、响应
			 */
			String uri = "contract/addconfig" + "?open_id=" + getmap.get("open_id") + "&lang=" + getmap.get("lang") + "&timestamp=" + getmap.get("timestamp") + "&randStr=" + getmap.get("randStr");
			String rspStr = handlePost(postmap, uri);

			/**
			 * 4 验签 有data节点时才验签
			 */
			JSONObject respObject = JSONObject.fromObject(rspStr);
			System.out.println("===响应错误码：" + respObject.get("errcode"));
			System.out.println("===响应错误提示：" + respObject.get("msg"));
			returnMap.put("code", respObject.get("errcode"));
			returnMap.put("msg", respObject.get("msg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	 /**
     * AES加密，再二进制转十六进制(bin2hex)
     * @param datamap 说明：
     *
     * @return 返回值说明：
     * @throws Exception
     */
    public static String handleEncrypt(TreeMap<String, String> datamap,String open_key) throws Exception {
        JSONObject dataobj = JSONObject.fromObject(datamap);

        return TLinxAESCoder.encrypt(dataobj.toString(),open_key);    // AES加密，并bin2hex
    }
    /**
     * 签名
     * @param getmap
     * @param datamap 说明：
     *
     * @return 返回值说明：
     */
    public static String handleSign(TreeMap<String, String> getmap, TreeMap<String, String> datamap,String privatekey) {
        Map<String, String> veriDataMap = new HashMap<String, String>();

        veriDataMap.putAll(getmap);
        veriDataMap.putAll(datamap);

        // 签名
        return sign(veriDataMap,privatekey);
    }
    /**
     * 签名
     * @param postMap
     * @param privatekey 说明：
     * @return
     */
    public static String sign(Map<String, String> postMap, String privatekey) {
        String sortStr = null;
        String sign    = null;

        try {

            /**
             * 1 A~z排序
             */
            sortStr = sort(postMap);
            System.out.println("=======排序后的明文：" + sortStr);

            /**
             * 2 sha1加密(小写)
             */
            String sha1 = TLinxSHA1.SHA1(sortStr);

            System.out.println("=======sha1：" + sha1);

            /**
             * 3 RSA加密(小写),二进制转十六进制
             */
            sign = TLinxRSACoder.sign(sha1.getBytes("utf-8"), privatekey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sign;
    }
 // 排序
    public static String sort(Map paramMap) throws Exception {
        String sort = "";
        TLinxMapUtil signMap = new TLinxMapUtil();
        if (paramMap != null) {
            String key;
            for (Iterator it = paramMap.keySet().iterator(); it.hasNext();) {
                key = (String) it.next();
                String value = ((paramMap.get(key) != null) && (!(""
                        .equals(paramMap.get(key).toString())))) ? paramMap
                        .get(key).toString() : "";
                signMap.put(key, value);
            }
            signMap.sort();
            for (Iterator it = signMap.keySet().iterator(); it.hasNext();) {
                key = (String) it.next();
                sort = sort + key + "=" + signMap.get(key).toString() + "&";
            }
            if ((sort != null) && (!("".equals(sort)))) {
                sort = sort.substring(0, sort.length() - 1);
            }
        }
        return sort;
    }
    /**
     * 请求接口
     * @param postmap
     * @param uri 说明：
     * @return      响应字符串
     */
    public static String handlePost(TreeMap<String, String> postmap, String uri) {
        String url = "https://api.orangebank.com.cn/org1/" + uri;
//        url = "http://openapi.tlinx.cn/org1/order?open_id=txaalQ4ae3fde16fab071bb1bc452dfb&lang=zh-cn&timestamp=1493791966&randStr=lAMUR5ALaxopwkTZSTrUQ4MSXEid9GdJ";
        if (url.contains("https")) {
            return HttpsUtil.httpMethodPost(url, postmap, "UTF-8");
        } else {
            return HttpUtil.httpMethodPost(url, postmap, "UTF-8");
        }
    }
    /**
     * 验签
     * @param respObject
     * @param publickey 说明：
     * @return
     */
    public static Boolean verifySign(JSONObject respObject, String publickey) {
        boolean verify = false;
        try {
            String respSign = respObject.getString("sign");

            respObject.remove("sign");                          // 删除sign节点

            String rspparm = sortjson(respObject);    // ȥsign json
            String sha1    = TLinxSHA1.SHA1(rspparm);           //

            verify = TLinxRSACoder.verify(sha1.getBytes(), publickey, respSign);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return verify;
    }
    // 排序
    public static String sortjson(JSONObject jsonMap) throws Exception {
        Map<String ,String > paramMap=new HashMap<String, String>();
        // 将json字符串转换成jsonObject
        JSONObject jsonObject = JSONObject.fromObject(jsonMap);
        Iterator ite = jsonObject.keys();
        // 遍历jsonObject数据,添加到Map对象
        while (ite.hasNext()) {
            String key = ite.next().toString();
            String value = jsonObject.get(key).toString();
            paramMap.put(key, value);
        }
        String sort = "";
        TLinxMapUtil signMap = new TLinxMapUtil();
        if (paramMap != null) {
            String key1;
            for (Iterator it = paramMap.keySet().iterator(); it.hasNext();) {
                key1 = (String) it.next();
                String value = ((paramMap.get(key1) != null) && (!(""
                        .equals(paramMap.get(key1).toString())))) ? paramMap
                        .get(key1).toString() : "";
                signMap.put(key1, value);
            }
            signMap.sort();
            for (Iterator it = signMap.keySet().iterator(); it.hasNext();) {
                key1 = (String) it.next();
                sort = sort + key1 + "=" + signMap.get(key1).toString() + "&";
            }
            if ((sort != null) && (!("".equals(sort)))) {
                sort = sort.substring(0, sort.length() - 1);
            }
        }
        return sort;
    }
    public static void main(String[] args) {
    	//https://testpayapi.aijinfu.cn/GateWay/ :测试环境授权地址
    	//https://payapi.aijinfu.cn/GateWay/	:生产环境配置授权地址
    	
//    	addConfig("880000000-880000784-1502248683", "wxe93071f39bf91508",null, "txa42lIMd7ce80cd811a69d9ab2319c4", "QI89Zau8dSFaZhi4hMFynTKp10uu0mvQ","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");
    	addConfig("880000000-880000784-1502248683", null, "https://pay.iquxun.cn/GateWay/", "txa42lIMd7ce80cd811a69d9ab2319c4", "QI89Zau8dSFaZhi4hMFynTKp10uu0mvQ","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");
//    	addConfig("880000000-880000904-15030246663821", null, "https://payapi.aijinfu.cn/GateWay/", "txaRILPXc98f38883cc9a2773897cca7", "05l58KTAx2ZbNnyvx5JSdOi6IAHoGgNG","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");
//    	addConfig("880000000-880000904-15030246663821", "wxc3d694c7766e655a", null, "txaRILPXc98f38883cc9a2773897cca7", "05l58KTAx2ZbNnyvx5JSdOi6IAHoGgNG","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");
    }
}
