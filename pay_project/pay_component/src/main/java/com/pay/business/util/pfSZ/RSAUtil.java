package com.pay.business.util.pfSZ;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {
	protected static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * (仅)使用代理商密钥加密
	 * @param content 加密串
	 * @param key 代理商密钥
	 */
	protected static String signByAgentID(String content, String key) throws Exception{
		final byte[] innerKey = Base64.decode(key);
		final byte[] result = new byte[innerKey.length + 26];

		System.arraycopy(Base64.decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKY="), 0, result, 0, 26);
		System.arraycopy(BigInteger.valueOf(result.length - 4).toByteArray(), 0, result, 2, 2);
		System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, result, 24, 2);
		System.arraycopy(innerKey, 0, result, 26, innerKey.length);

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(result);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		
		return signByPrivate(content, privateKey, "UTF-8");
	}

	protected static String signByPrivate(String content, PrivateKey privateKey, String input_charset) throws Exception {
		if (privateKey == null) {
			throw new Exception("加密私钥为空, 请设置");
		}
		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		signature.initSign(privateKey);
		signature.update(content.getBytes(input_charset));
		return Base64.encode(signature.sign());
	}

	public static void main(String[] args) throws Exception {
		String xx = "agentId=88888899&clientIp=127.0.0.1&commodityName=169601707101105600&merNo=800440054111002&notifyUrl=http://localhost:80&orderDate=20170908&orderNo=20170908100844641&productId=0119&requestNo=20170908100844640&returnUrl=http://localhost:80&subMchId=2088621880001464&timeExpire=1d&transAmt=1&transId=10&version=V1.1";
		System.out.println(signByAgentID(xx, "MIIEowIBAAKCAQEAxYO+7H+kGDpw8YD7Xwj3dCoklYETPLN1UG1Yt20X7DyrA8w+xC9ws5d23HJJ+/YEH89sME5vVLghQFxoVUZz6NDnVI0IclfEsH0BAFp8Xl1b3ExURYj4lT+a6mNho6Ui+ouiRb1XzW57P6srhKyW7t1wms7c+lgPsXAr5b2KnRVoHWYyaCATLzcEhhr3Mm2WUCD6LJYyswv5NaTOzvshkWcYnZZUcXslhMa1A9A3vlJtPmH1ai3TRK0SwzC/mNe3d+dkYXd77Eyh5JwGFQFWXbxXjxaBEesY0NjjSUDmJmXvI7t5pD48FSOOJQMVTrEACsaZO5hc3dAIaBBFzKIcfQIDAQABAoIBABfESJ8QpOA9eAW3bYf7/jq+L3TF+Vieh4lL/xbjS7OjgTiNxSe6Rad2nFjeb8Sfz9M8FFqjtYXOOkISXIOWXLAxIwTri46mvQY3pH00Zi68sScLEEDlwHPFGZEGsGMOpezcDISzyfLwTmhU4oGueuL3Rmt6ZODC4/CH/OBCNIG+MZFODVt2E8G9/W2z1dcFGo6tjxCCvlYNJ0OhRCsoJyKiKSQmJAk5O7MMVU1+jPvP3x6nCu+Yy9cnr9CH86M2jRafZ3HOKh5Jc7ymDqQwRMsgRqh9TrX6QDuVkJSn12e/hfjuYZQAWSigHjTLLMGjS0yIzwX8N5fSyiT7z8TJF8ECgYEA4RMTu5DQd6No6CX399hd8UfokQHapPpgRezDzIk1kAp25GeSOWkw8HSdlOjObV2/sYmz1LnglzuRTR31oajVqV6ZVcR/2fpFK87GPiri3+U2RkXwM3aJGh88VqdWVzboqgjLQ4eI9A3rbbptepqeQf4Tb0o3KvKaDKqwtDLU1nUCgYEA4KdCNiBewrOtalxUfD12oTVnjgrLsUZKApkLWL05QXLmNnksB1DVxDbKQiHDk6IvTXIgaMy/ZCkyZkNlcVQd1VDuB9JZN7aSVeEOvkww/99D+w51V9LMNKbnytLbK0nmZJRFouoRodphIeFJqA02vtWTK4pNme14iGADJ+IZvOkCgYAJAFdQsAj2T+25IxOYsOmI5cRSUE2rPWwuP7rQ6kffG9wHZHD/pMpVQ4St2OWwkAhDlGtBvbFSuwojmGgjb/ojjOn6+SHX2N99Ugaxo8txAty50MA7fqkbB1bFbGnSkRqa+kEO0VPT1t6sg8EvHxHnN78VO1WbfRpWGVl5y3KhpQKBgF3CcMfWSrZH9yBk2H3hyRkPCOEncEvUYh8jcLDgiHzgT2R8vftvqUfy9gcTwGRlVAimkRAsI9TRvM8hYb0itjDJTg7Fo6a08+4Tt+uEMQ8ZR24IYsD8oW14G1VzGzW96gIgP8/2kNVUJyXUuMECgs6ypHGPj0Om8J86Mxb6LnPBAoGBAKwYXfmsYmwh4mRutOFMHsrs+4SK50ug32Cgakd+nUZx8/+WDm5O/C4ZjRtOIu2wC2a+5dxy56vCJnwwSXt3V9mnRupV9B72S0PJheE54WjdSJhrwASGpnG4k6dmk0pskTSqCEGiNtsHDElNeytPWVoB+HA/sQZMIERUJCi9+LoX"));
	}

	/**
     * RSA验签名检查
     * 
     * @param content
     *            待签名数据
     * @param sign
     *            签名值
     * @param publicKey
     *            支付宝公钥
     * @param input_charset
     *            编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign,
                                 String publicKey, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey);
            PublicKey pubKey = keyFactory
                    .generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));
            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
