package com.pay.business.util.shande;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import cn.com.sand.online.agent.service.sdk.RandomStringGenerator;
import cn.com.sand.online.agent.service.sdk.SDKUtil;
import cn.com.sandpay.cashier.sdk.util.CertUtil;
import cn.com.sandpay.cashier.sdk.util.CryptoUtil;

import com.pay.business.util.StringHandleUtil;
import com.pay.business.util.httpsUtil.HttpsUtil;
import com.thoughtworks.xstream.core.util.Base64Encoder;

public class ShandeSign {
	private static Logger log = Logger.getLogger(ShandeSign.class);
	
	public static String execute(String data, String url, String privateKey,String pubKey) throws Exception{
		Map<String, Object> reqMap = new HashMap<>();
		String reqSign = new String(Base64.encodeBase64(CryptoUtil.digitalSign(data.getBytes()
				, getPrivateKey(privateKey), "SHA1WithRSA")));
		reqMap.put("charset", "UTF-8");
		reqMap.put("data", data);
		reqMap.put("signType", "01");
		reqMap.put("sign", reqSign);
		reqMap.put("extend", "");
		System.out.println("请求衫德参数:"+reqMap.toString());
		String doPost = HttpsUtil.doPostString(url, reqMap, "UTF-8");
		doPost = URLDecoder.decode(doPost, "utf-8");
		System.out.println("衫德返回结果:"+doPost);
		Map<String,Object> map = StringHandleUtil.toMap(doPost);
		String respData = map.get("data").toString();
		String respSign =map.get("sign").toString(); 
		boolean con =CryptoUtil.verifyDigitalSign(respData.getBytes("UTF-8")
				, Base64.decodeBase64(respSign), getPubKey(pubKey), "SHA1WithRSA");
		if(con){
			return respData;
		}
		System.out.println("------------衫德下单验签失败-------------");
		return null;
	}
	
	/**
	 * 读取公钥证书中公钥
	 * @param path
	 * @return
	 */
	public static PublicKey getPublicKey(String path){
		if (null!=path) {
			try {
				InputStream inputStream = new FileInputStream(path);
				PublicKey publicKey = CertUtil.getPublicKey(inputStream);
				return publicKey;
			} catch (Exception e) {
				log.error("加载公钥证书出错", e);
			}
		}
		return null;
	}
	
	/**
	 * 读取私钥证书中私钥
	 * @param path
	 * @param keyPassword
	 * @return
	 */
	public static PrivateKey getPrivateKey(String path, String keyPassword){
		if (null!=path) {
			try {
				InputStream inputStream = new FileInputStream(path);
				PrivateKey privateKey = CertUtil.getPrivateKey(inputStream, keyPassword);
				return privateKey;
			} catch (Exception e) {
				log.error("加载私钥证书出错", e);
			}
		}
		return null;
	}
	
	/**
	 * 公钥字符串转PublicKey
	 * @param pubKey
	 * @return
	 */
	public static PublicKey getPubKey(String pubKey) {
		  PublicKey publicKey = null;
		  try {

		   // 自己的公钥(测试)
		      java.security.spec.X509EncodedKeySpec bobPubKeySpec = new java.security.spec.X509EncodedKeySpec(
		     new BASE64Decoder().decodeBuffer(pubKey));
		   // RSA对称加密算法
		   java.security.KeyFactory keyFactory;
		   keyFactory = java.security.KeyFactory.getInstance("RSA");
		   // 取公钥匙对象
		   publicKey = keyFactory.generatePublic(bobPubKeySpec);
		  
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		  return publicKey;
		 }
	 
	/**
	 * 私钥字符串转PrivateKey
	 * @param priKey
	 * @return
	 */
	public static PrivateKey getPrivateKey(String priKey) {
		  PrivateKey privateKey = null;
		  PKCS8EncodedKeySpec priPKCS8;
		  try {
		   priPKCS8 = new PKCS8EncodedKeySpec(
		     new BASE64Decoder().decodeBuffer(priKey));
		   KeyFactory keyf = KeyFactory.getInstance("RSA");
		   privateKey = keyf.generatePrivate(priPKCS8);
		  } catch (Exception e) {
			   e.printStackTrace();
			  }
		  return privateKey;
	}
	
	/**
	 * 读取公钥证书的公钥字符串
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String getPubKeyString(String path) throws Exception{
		X509Certificate cert = X509Certificate.getInstance(new FileInputStream(path));  
		PublicKey publicKey = cert.getPublicKey();  
		Base64Encoder base64Encoder=new Base64Encoder();  
		String publicKeyString = base64Encoder.encode(publicKey.getEncoded()).replace("\n", "");  
		System.out.println("-----------------公钥--------------------");  
		System.out.println(publicKeyString);  
		System.out.println("-----------------公钥--------------------"); 
		return publicKeyString;
	}
	
	/**
	 * 读取私钥证书的私钥字符串
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKeyString(String path,String keyPassword){
		final String KEYSTORE_FILE     = path;  
        final String KEYSTORE_PASSWORD = keyPassword;  
  
        try  
        {  
            KeyStore ks = KeyStore.getInstance("PKCS12");  
            FileInputStream fis = new FileInputStream(KEYSTORE_FILE);  
  
            // If the keystore password is empty(""), then we have to set  
            // to null, otherwise it won't work!!!  
            char[] nPassword = null;  
            if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals(""))  
            {  
                nPassword = null;  
            }  
            else  
            {  
                nPassword = KEYSTORE_PASSWORD.toCharArray();  
            }  
            ks.load(fis, nPassword);  
            fis.close();  
  
            System.out.println("keystore type=" + ks.getType());  
  
            // Now we loop all the aliases, we need the alias to get keys.  
            // It seems that this value is the "Friendly name" field in the  
            // detals tab <-- Certificate window <-- view <-- Certificate  
            // Button <-- Content tab <-- Internet Options <-- Tools menu  
            // In MS IE 6.  
            Enumeration<String> enumStr = ks.aliases();  
            String keyAlias = null;  
            if (enumStr.hasMoreElements()) // we are readin just one certificate.  
            {  
                keyAlias = enumStr.nextElement();  
                System.out.println("alias=[" + keyAlias + "]");  
            }  
  
            // Now once we know the alias, we could get the keys.  
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));  
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);  
            Certificate cert = ks.getCertificate(keyAlias);  
            PublicKey pubkey = cert.getPublicKey();  
  
            System.out.println("cert class = " + cert.getClass().getName());  
            System.out.println("cert = " + cert);  
            Base64Encoder base64Encoder=new Base64Encoder();  
            System.out.println("public key = " + base64Encoder.encode(pubkey.getEncoded())
            		.replace("\n", ""));  
            System.out.println("private key = " + base64Encoder.encode(prikey.getEncoded())
            		.replace("\n", ""));
            return base64Encoder.encode(prikey.getEncoded()).replace("\n", "");
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        } 
        return "";
	}
	
	public static List genEncryptData(String data,String merchId, String transCode, String pubKey,String priKey)
			throws Exception{
		if (merchId == null || transCode == null || data == null)
		{
			System.out.println("merchId or transCode or data is null");
			return null;
		}
		List formparams = new ArrayList();
		formparams.add(new BasicNameValuePair("merId", merchId));
		formparams.add(new BasicNameValuePair("transCode", transCode));
		try
		{
			byte plainBytes[] = data.getBytes("UTF-8");
			String aesKey = RandomStringGenerator.getRandomStringByLength(16);
			byte aesKeyBytes[] = aesKey.getBytes("UTF-8");
			String encryptData = new String(Base64.encodeBase64(CryptoUtil.AESEncrypt(plainBytes, aesKeyBytes, "AES", "AES/ECB/PKCS5Padding", null)), "UTF-8");
			String sign = new String(Base64.encodeBase64(CryptoUtil.digitalSign(plainBytes, getPrivateKey(priKey), "SHA1WithRSA")), "UTF-8");
			String encryptKey = new String(Base64.encodeBase64(CryptoUtil.RSAEncrypt(aesKeyBytes, getPubKey(pubKey), 2048, 11, "RSA/ECB/PKCS1Padding")), "UTF-8");
			formparams.add(new BasicNameValuePair("encryptData", encryptData));
			formparams.add(new BasicNameValuePair("encryptKey", encryptKey));
			formparams.add(new BasicNameValuePair("sign", sign));
			System.out.println("encryptData:"+encryptData);
			System.out.println("encryptKey:"+encryptKey);
			System.out.println("sign:"+sign);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		return formparams;
	}
	
	public static String decryptRetData(String data, String pubKey,String priKey)
			throws Exception{
		Map responseMap = convertResultStringToMap(data);
		String retEncryptKey = (String)responseMap.get("encryptKey");
		String retEncryptData = (String)responseMap.get("encryptData");
		String retSign = (String)responseMap.get("sign");
		System.out.println("retEncryptKey:{}"+ retEncryptKey);
		System.out.println("retEncryptData:{}"+ retEncryptData);
		System.out.println("retSign:{}"+ retSign);
		byte decodeBase64KeyBytes[] = Base64.decodeBase64(retEncryptKey.getBytes("UTF-8"));
		byte merchantAESKeyBytes[] = CryptoUtil.RSADecrypt(decodeBase64KeyBytes, getPrivateKey(priKey), 2048, 11, "RSA/ECB/PKCS1Padding");
		byte decodeBase64DataBytes[] = Base64.decodeBase64(retEncryptData.getBytes("UTF-8"));
		byte retDataBytes[] = CryptoUtil.AESDecrypt(decodeBase64DataBytes, merchantAESKeyBytes, "AES", "AES/ECB/PKCS5Padding", null);
		System.out.println("retData:{}"+ new String(retDataBytes, "UTF-8"));
		byte signBytes[] = Base64.decodeBase64(retSign.getBytes("UTF-8"));
		boolean isValid = CryptoUtil.verifyDigitalSign(retDataBytes, signBytes, getPubKey(pubKey), "SHA1WithRSA");
		if (!isValid)
		{
			System.out.println("报文验签不通过");
			throw new Exception("报文验签不通过");
		} else
		{
			System.out.println("报文验签通过");
			String ret = new String(retDataBytes, "UTF-8");
			return ret;
		}
	}
	
	private static Map convertResultStringToMap(String result){
		Map map = null;
		if (StringUtils.isNotBlank(result))
		{
			if (result.startsWith("\"") && result.endsWith("\""))
			{
				System.out.println(result.length());
				result = result.substring(1, result.length() - 1);
			}
			map = SDKUtil.convertResultStringToMap(result);
		}
		return map;
	}
	
	public static void main(String[] args) throws Exception {
		//getPubKeyString("D:\\sand.cer");
		
		getPrivateKeyString("D:\\jinfu.pfx","123456");
		//getPubKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy06Nc6hNkJVetRauhD9P6WrBlbJWgevQsYMkImtCLdBrEP/aMiPXJqZLIcM0B3mEzSluge4yMrGHW6ZYAq972ja3QZpqE0fKlGYFNqxy6j1rah1kI9K7N1/Hb9SvgzbxO5+vZfYdUQRxDVgeB0yhsohVD2Ce10h+qe8hf3uKmqzwNAP0ZfaSLenHdzk6kHzVUqyts3uesHiOw1xDkjzLie7IPdCyw15czKowXGOgiGc9Ip8+HmInhmq3TOEOkhli7AD89HwSym79ORBWPVJ5GEi7jyFEQ+XXUu4QutfH9jn5XfUcuVk9O4vPb1VjWeh5geWlmQ5IgLljG3WSCC9towIDAQAB");
		//getPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC3JuHwP5EzDVBXGxwwUKdvnOGwR/I+VDTJ7n5VjYQEX2vAK2gzEs7qVnRtHi7xLA7UY9STgeJT9G1D+ArGJ69JcC5bKEMOmLuXFaGyzcz94iH4bGlWso+NTvnyjJYdFltmQlhD/SJDbgRv/fCaLfeuKV1tvaNCHZXVijTOy6MrOiyc19AdEGCz8xgyA1Q+kgHJtoWpWQ8OYXDxUm1tNS5OsT5hPZVS1yNuL3IvMG8gYPfeLfgLNLhysyGLvZ7+yOpstZynK028u4yq8H7w/v5MPGqlMahg2Q+AZkiXTSYajaVakf2ypiTpnT8p+efBmv11pOFldIpjYyqtg6PuXGbXAgMBAAECggEACP2Gn7VvGjNwGyaAhrqezXLE/VM6x+Z4ROVJHEf7D//jGSbIUaF9uLEPu/98TGhePfy8hZUdmANqjaiSVtHB3/f6vozGZeQHaU4thsplYp0ED966eQAA3e3fhRFzmO/tAqMFFClL0kWHQDwV4GubOdhb9rQVXHx5S2ciWnhShR+bqOc9q/pPj2vwplwJ70Nnbp1z+A5wmPdTMPijhUR5LX9dnX7uViFD84/DqHTj3yzQU0dQJ898Ndx2j6YutobqNYcgd2KL1ius2CmhZ3O23CmSJNYlktq2C7JcbqsTClh2G18o3ern1I6pATPYRaIwaysxRUbkWMMM/xMr7mFnqQKBgQDY3dPlt/iCpBXO++bhs6Q2RXE+NwPPFZnU8c86Rir9fHMfCnVEgm8scKGYc+xDQ8vf/8MhCAN4C0sUIniiPD9zTHLviOVmkoqTPdjAAUkmPIS4+ZBqX+iOOmk7Alq82wwY9u5G67wG0VlocoO28gMY0Hi4M8wg0jzIok39Kutv6wKBgQDYM5qW0kZMN9oY4ldPqe/jY2oTrk09ePweKbOz6ntkyZrmiCgPee/0skvxbY4m7DRd+BHq5Z5FT2ZtX1e9HwkMdRAlKNsXur4QRxi8OWOyCIV010ts3+RqzNbs34zQbSS5rpR+6FrmCRs1qclPphjN244BhvOkj6+X6FdkRcMVxQKBgQCi4eGQLSA6xxEWOD7OEIXquTd32gxDUl8LAF97zk3lu74fd1Rik3D6uNG2VoMCdn4/DLM7MPCiDiFiyw0+FPA3IhlFbdWWt8PbGV2dwJl3XYb2A4ODUeuyP47f4kHSjNdGPNj0bYP4vu5fM3tYQecvkQzKlSThFebPbpAS8VSJ5wKBgCDu42p8B2dOzrMhr0kcSsVpfFwZHfzyM/1oPs52Nmuo5iadsPSCj5HHoxfYp2G4c1WpFxmf9pb6PFEGx/ewBZHXNylh6tXXhWI3YkYxu8T/1UxyCzQ/eqzmHQsiFnIdXg3G0SnvvQDzfCiVf2vZkkexXRVQeEal+Ip8QuusUMY9AoGASjlzivH9Mcjxe9aEGjLi1O/Juqs9jPjQdn89XEUGjFE2jVEY4447c8hW/quX65J/iZne8Oy9LlhkTWjyKnSht+92952llc3JjQ9Sr3KUpMcSJQ5HjK3Cx/U1ng0rKEwcKtbeUVp0tLtiVzntD6JaWQ7A8hv5ocek9+yC/BStWDA=");
	}
}
