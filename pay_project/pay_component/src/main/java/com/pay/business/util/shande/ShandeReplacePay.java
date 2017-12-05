package com.pay.business.util.shande;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ShandeReplacePay {
	
	/**
	 * 代付下单
	 * @param orderNum
	 * @param pubKey
	 * @param priKey
	 * @return
	 */
	public static Map<String,String> agentPay(String orderNum,String pubKey,String priKey) 
			{
		Map<String,String> result = new HashMap<>();
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("orderCode", orderNum);
			
			jsonObject.put("version", "01");
			jsonObject.put("productId", "00000004");
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			jsonObject.put("tranTime", df.format(new Date()));

//			jsonObject.put("timeOut", "20161024120000");
			
			jsonObject.put("tranAmt", "000000000001");
			jsonObject.put("currencyCode", "156");
			

			jsonObject.put("accAttr", "0");
			jsonObject.put("accNo", "6216261000000000018");
			
			jsonObject.put("accType", "4");
			jsonObject.put("accName", "全渠道");
			
			jsonObject.put("remark", "pay");
			jsonObject.put("reqReserved", "请求方保留测试");
			System.out.println("json:"+ jsonObject.toJSONString());
			//商户号
			String merchId = "17057042";
			//通过辅助工具发送交易请求，并获取响应报文
			String data = HttpUtil.post(ShandeConfig.AGENT_URL, merchId, ShandeConfig.transCode
					, jsonObject.toJSONString(),pubKey,priKey);
			System.out.println("retData:" + data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	/**
	 * 代付订单查询
	 * @param orderNum
	 * @param pubKey
	 * @param priKey
	 * @return
	 */
	public static Map<String,String> query(String orderNum,String pubKey,String priKey){
		Map<String,String> result = new HashMap<>();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("orderCode", orderNum);
			jsonObject.put("version", "01");
			jsonObject.put("productId", "00000004");
			jsonObject.put("tranTime", "20170110103000");		
			//商户号
			String merchId = "17057042";
			//通过辅助工具发送交易请求，并获取响应报文
			String data = HttpUtil.post(ShandeConfig.AGENT_QUERY_URL, merchId, ShandeConfig.transCode
					, jsonObject.toJSONString(),pubKey,priKey);
			System.out.println("retData:" + data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 商户余额查询
	 * @param orderNum
	 * @param pubKey
	 * @param priKey
	 * @return
	 */
	public static Map<String,String> queryBalance(String orderNum,String pubKey,String priKey){
		Map<String,String> result = new HashMap<>();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("orderCode", orderNum);
			
			jsonObject.put("version", "01");
			jsonObject.put("productId", "00000003");
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			jsonObject.put("tranTime", df.format(new Date()));
			System.out.println(jsonObject.toJSONString());
			//商户号
			String merchId = "17057042";
			//通过辅助工具发送交易请求，并获取响应报文
			String data = HttpUtil.post(ShandeConfig.AGENT_BALANCE_URL, merchId, ShandeConfig.transCode
					, jsonObject.toJSONString(),pubKey,priKey);
			System.out.println("retData:" + data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy06Nc6hNkJVetRauhD9P6WrBlbJWgevQsYMkImtCLdBrEP/aMiPXJqZLIcM0B3mEzSluge4yMrGHW6ZYAq972ja3QZpqE0fKlGYFNqxy6j1rah1kI9K7N1/Hb9SvgzbxO5+vZfYdUQRxDVgeB0yhsohVD2Ce10h+qe8hf3uKmqzwNAP0ZfaSLenHdzk6kHzVUqyts3uesHiOw1xDkjzLie7IPdCyw15czKowXGOgiGc9Ip8+HmInhmq3TOEOkhli7AD89HwSym79ORBWPVJ5GEi7jyFEQ+XXUu4QutfH9jn5XfUcuVk9O4vPb1VjWeh5geWlmQ5IgLljG3WSCC9towIDAQAB";
		String priKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC3JuHwP5EzDVBXGxwwUKdvnOGwR/I+VDTJ7n5VjYQEX2vAK2gzEs7qVnRtHi7xLA7UY9STgeJT9G1D+ArGJ69JcC5bKEMOmLuXFaGyzcz94iH4bGlWso+NTvnyjJYdFltmQlhD/SJDbgRv/fCaLfeuKV1tvaNCHZXVijTOy6MrOiyc19AdEGCz8xgyA1Q+kgHJtoWpWQ8OYXDxUm1tNS5OsT5hPZVS1yNuL3IvMG8gYPfeLfgLNLhysyGLvZ7+yOpstZynK028u4yq8H7w/v5MPGqlMahg2Q+AZkiXTSYajaVakf2ypiTpnT8p+efBmv11pOFldIpjYyqtg6PuXGbXAgMBAAECggEACP2Gn7VvGjNwGyaAhrqezXLE/VM6x+Z4ROVJHEf7D//jGSbIUaF9uLEPu/98TGhePfy8hZUdmANqjaiSVtHB3/f6vozGZeQHaU4thsplYp0ED966eQAA3e3fhRFzmO/tAqMFFClL0kWHQDwV4GubOdhb9rQVXHx5S2ciWnhShR+bqOc9q/pPj2vwplwJ70Nnbp1z+A5wmPdTMPijhUR5LX9dnX7uViFD84/DqHTj3yzQU0dQJ898Ndx2j6YutobqNYcgd2KL1ius2CmhZ3O23CmSJNYlktq2C7JcbqsTClh2G18o3ern1I6pATPYRaIwaysxRUbkWMMM/xMr7mFnqQKBgQDY3dPlt/iCpBXO++bhs6Q2RXE+NwPPFZnU8c86Rir9fHMfCnVEgm8scKGYc+xDQ8vf/8MhCAN4C0sUIniiPD9zTHLviOVmkoqTPdjAAUkmPIS4+ZBqX+iOOmk7Alq82wwY9u5G67wG0VlocoO28gMY0Hi4M8wg0jzIok39Kutv6wKBgQDYM5qW0kZMN9oY4ldPqe/jY2oTrk09ePweKbOz6ntkyZrmiCgPee/0skvxbY4m7DRd+BHq5Z5FT2ZtX1e9HwkMdRAlKNsXur4QRxi8OWOyCIV010ts3+RqzNbs34zQbSS5rpR+6FrmCRs1qclPphjN244BhvOkj6+X6FdkRcMVxQKBgQCi4eGQLSA6xxEWOD7OEIXquTd32gxDUl8LAF97zk3lu74fd1Rik3D6uNG2VoMCdn4/DLM7MPCiDiFiyw0+FPA3IhlFbdWWt8PbGV2dwJl3XYb2A4ODUeuyP47f4kHSjNdGPNj0bYP4vu5fM3tYQecvkQzKlSThFebPbpAS8VSJ5wKBgCDu42p8B2dOzrMhr0kcSsVpfFwZHfzyM/1oPs52Nmuo5iadsPSCj5HHoxfYp2G4c1WpFxmf9pb6PFEGx/ewBZHXNylh6tXXhWI3YkYxu8T/1UxyCzQ/eqzmHQsiFnIdXg3G0SnvvQDzfCiVf2vZkkexXRVQeEal+Ip8QuusUMY9AoGASjlzivH9Mcjxe9aEGjLi1O/Juqs9jPjQdn89XEUGjFE2jVEY4447c8hW/quX65J/iZne8Oy9LlhkTWjyKnSht+92952llc3JjQ9Sr3KUpMcSJQ5HjK3Cx/U1ng0rKEwcKtbeUVp0tLtiVzntD6JaWQ7A8hv5ocek9+yC/BStWDA=";
		agentPay(System.currentTimeMillis()+"", pubKey, priKey);
	}
}
