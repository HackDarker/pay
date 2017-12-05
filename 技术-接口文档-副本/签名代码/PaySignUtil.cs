using System;
using System.Collections.Generic;
using System.Text;
using System.Security.Cryptography;  

namespace com.pay.business.util
{


	public class PaySignUtil
	{

		/// <summary>
		/// 参数加密 </summary>
		/// <param name="map"> </param>
		/// <param name="keyValue">  商户密钥
		/// @return </param>
		/// <exception cref="Exception">  </exception>
//JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
//ORIGINAL LINE: public static String getSign(java.util.Map<String,Object> map,String keyValue) throws Exception
		public static string getSign(IDictionary<string, object> map, string keyValue)
		{
			//密文
			StringBuilder buffer = new StringBuilder();
			buffer.Append("keyValue=" + keyValue+"&");

			buffer.Append(getParamStr(map));
			string sNewString = getSign(buffer.ToString().ToUpper(), "MD5");

			return sNewString;
		}

		/**
		 * 参数签名串拼接
		 * @param map
		 * @return
		 */
		public static string getParamStr(IDictionary<string, object> map){
			//密文
			StringBuilder buffer = new StringBuilder();
			List<string> keys = new List<string>(map.Keys);
			//排序
			keys.Sort();
			//参数值拼接
			for (int i = 0; i < keys.Count; i++) {
				string key = keys[i];
				if(!"sign".Equals(key)&&!"keyValue".Equals(key)){
					string value = map[key]==null?"":map[key].ToString();
					if(i==0){
						buffer.Append(key + "=" + value);
					}else{
						buffer.Append("&"+key + "=" + value);
					}
				}
			}
			return buffer.ToString();
		}

		/// <summary>
		/// 获取加密签名 </summary>
		/// <param name="str"> 字符 </param>
		/// <param name="type"> 加密类型 </param>
		/// <returns> </returns>
		/// <exception cref="Exception"> </exception>
//JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
//ORIGINAL LINE: public static String getSign(String str, String type) throws Exception
		public static string getSign(string str, string type)
		{
			MD5 md5 = new MD5CryptoServiceProvider();
            byte[] data = md5.ComputeHash(Encoding.UTF8.GetBytes(str));
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < data.Length; i++)
            {
                sBuilder.Append(data[i].ToString("x2"));
            }
            return sBuilder.ToString();
            
		
		}

		/// <summary>
		/// 验签 </summary>
		/// <param name="map"> </param>
		/// <param name="keyValue">  商户密钥
		/// @return </param>
		/// <exception cref="Exception">  </exception>
//JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
//ORIGINAL LINE: public static boolean checkSign(java.util.Map<String,Object> map,String keyValue) throws Exception
		public static bool checkSign(IDictionary<string, object> map, string keyValue)
		{
			if (map["sign"] == null)
			{
				return false;
			}
			//密文
			string sign = map["sign"].ToString();
			map.Remove("sign");
			string sNewString = getSign(map, keyValue);

			return sNewString.Equals(sign);
		}


//JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
//ORIGINAL LINE: public static void main(String[] args) throws Exception
		public static void Main(string[] args)
		{
			//**************************参数按照文档传入***********************************
			IDictionary<string, object> map = new Dictionary<string, object>();

			
			//支付签名示例
			map["payMoney"] = "0.01";
			map["bussOrderNum"] = "1458794654162";
			map["notifyUrl"] = "http://www.aijinfu.cn/Alipay_Notify_Url.do";
			map["appKey"] = "6413f866b558d3e2b6ccf4f0d865f9df"; //测试appKey
			map["orderName"] = "测试支付功能"; 
			string sign = getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j"); //测试密钥
            string paramStr = getParamStr(map)+"&sign="+sign;

			
			/*//查询订单签名示例
			map["appKey"] = "6413f866b558d3e2b6ccf4f0d865f9df";
			map["bussOrderNum"] = "145800654162564";
			map["orderNum"] = "DD2017536545662646";
			string sign = getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j"); //测试密钥
            string paramStr = getParamStr(map)+"&sign="+sign;*/


			/*//退款签名示例
			map["appKey"] = "6413f866b558d3e2b6ccf4f0d865f9df";
			map["bussOrderNum"] = "145800654162564";
			map["orderNum"] = "DD2017536545662646";
			map["refundMoney"] = "0.01"; //测试appKey
			map["refundType"] = "Y"; 
			string sign = getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j"); //测试密钥
            string paramStr = getParamStr(map)+"&sign="+sign;*/
			
			
			/*//获取对账单签名示例
			map["appKey"] = "6413f866b558d3e2b6ccf4f0d865f9df";
			map["billTime"] = "20170206";
			string sign = getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j"); //测试密钥
            string paramStr = getParamStr(map)+"&sign="+sign;*/
			

			/*//异步通知回调参数
			map["result_code"] = "200"; // 成功
			map["order_num"] = "DD2017124512345661";// 订单
			map["buss_order_num"] = "21564561561456";// 商户订单
			map["pay_money"] = "0.01"; // 支付金额
			map["pay_discount_money"] = "0.01"; // 最终支付金额
			map["pay_time"] = "20170206122558"; // 支付时间
			string sign = getSign(map, "u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");
			map["sign"] = sign; // 支付时间

			//商户验签
			checkSign(map, "u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");*/
			

			Console.WriteLine(paramStr);

		}
	}

}