package com.pay.business.util.suiningDF;

/**
 * Copyright (c) 2008 Bank Of China Software Center
 * All rights reserved.
 */

/**
 * @(#)PKCS7Tool.java 1.0 2008-9-23
 * Copyright (c) 2008 Bank Of China Software Center
 * All rights reserved.
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;

/**
 * PKCS12Tool.java pkcs12格式签名工具
 * 
 * @version 1.0
 * @author niezhong@yusys.com.cn
 */
public class PKCS12Tool {
	/** 签名 */
	private static final int SIGNER = 1;
	/** 验证 */
	private static final int VERIFIER = 2;
	/** 用途 */
	private int mode = 0;
	/** 摘要算法 */
	private String digestAlgorithm = "SHA256";
	/** 签名算法 */
	private String signingAlgorithm = "SHA256withRSA";
	/** 签名证书链 */
	private X509Certificate[] certificates = null;
	/** 签名私钥 */
	private PrivateKey privateKey = null;
	/** 根证书 */
	private Certificate rootCertificate = null;

	private final static String KEY_STORE_TYPE = "PKCS12";
	/** JVM 提供商 */
	private static char jvm = 0;
	private static Class algorithmId = null;
	private static Class derValue = null;
	private static Class objectIdentifier = null;
	private static Class x500Name = null;
	private static boolean debug = false;

	/**
	 * 私有构造方法
	 */
	private PKCS12Tool(int mode) {
		this.mode = mode;
	}

	public static PKCS12Tool getSigner(String keyStorePath, String keyPassword)
			throws GeneralSecurityException, IOException {
		init();
		Security.addProvider(new BouncyCastleProvider());
		KeyStore keyStore = KeyStore.getInstance(PKCS12Tool.KEY_STORE_TYPE);
		// 绝对路径
		InputStream fis = null;
		try {
			fis = new FileInputStream(new File(keyStorePath));
			keyStore.load(fis, keyPassword.toCharArray());
		} finally {
			if (fis != null)
				fis.close();
		}

		Enumeration<String> aliases = keyStore.aliases();

		String alias = null;
		while (aliases.hasMoreElements()) {
			alias = aliases.nextElement();
			Certificate[] certs = keyStore.getCertificateChain(alias);
			if (certs == null || certs.length == 0)
				continue;
			X509Certificate cert = (X509Certificate) certs[0];
			if (matchUsage(cert.getKeyUsage(), 1)) {
				try {
					cert.checkValidity();
				} catch (CertificateException e) {
					continue;
				}
				break;
			}
		}
		PrivateKey key = (PrivateKey) keyStore.getKey(alias,
				keyPassword.toCharArray());
		PKCS12Tool tool = new PKCS12Tool(SIGNER);

		X509Certificate[] certificates = null;
		if (keyStore.isKeyEntry(alias)) {
			// 检查证书链
			Certificate[] certs = keyStore.getCertificateChain(alias);
			for (int i = 0; i < certs.length; i++) {
				if (!(certs[i] instanceof X509Certificate))
					throw new GeneralSecurityException("Certificate[" + i
							+ "] in chain '" + alias
							+ "' is not a X509Certificate.");
			}
			// 转换证书链
			certificates = new X509Certificate[certs.length];
			for (int i = 0; i < certs.length; i++)
				certificates[i] = (X509Certificate) certs[i];
		} else if (keyStore.isCertificateEntry(alias)) {
			// 只有单张证书
			Certificate cert = keyStore.getCertificate(alias);
			if (cert instanceof X509Certificate) {
				certificates = new X509Certificate[] { (X509Certificate) cert };
			}
		} else {
			throw new GeneralSecurityException(alias
					+ " is unknown to this keystore");
		}

		tool.certificates = certificates;
		tool.privateKey = key;
		return tool;
	}

	/**
	 * 签名
	 * 
	 * @param data
	 *            数据
	 * @return signature 签名结果
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public String sign(byte[] data) throws Exception {
		if (mode != SIGNER)
			throw new IllegalStateException(
					"call a PKCS7Tool instance not for signature.");
		Signature signature = Signature.getInstance(certificates[0]
				.getSigAlgName());
		signature.initSign(privateKey);
		signature.update(data);
		byte[] signdata = signature.sign();
		return bytesToStrHex(signdata);
	}

	/**
	 * 取得验签名工具 加载信任根证书
	 * 
	 * @param rootCertificatePath
	 *            根证书路径
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static PKCS12Tool getVerifier(String rootCertificatePath)
			throws GeneralSecurityException, IOException {
		init();
		// 加载根证书
		FileInputStream fis = null;
		Certificate rootCertificate = null;
		try {
			fis = new FileInputStream(rootCertificatePath);
			CertificateFactory certificatefactory = CertificateFactory
					.getInstance("X.509");
			try {
				rootCertificate = certificatefactory.generateCertificate(fis);
			} catch (Exception exception) {
				InputStream is = new ByteArrayInputStream(
						new BASE64Decoder().decodeBuffer(fis));
				rootCertificate = certificatefactory.generateCertificate(is);
			}
		} finally {
			if (fis != null)
				fis.close();
		}

		PKCS12Tool tool = new PKCS12Tool(VERIFIER);
		tool.rootCertificate = rootCertificate;
		return tool;
	}

	public boolean verify(String signData, byte[] data)
			throws IOException, NoSuchAlgorithmException, SignatureException,
			InvalidKeyException, CertificateException, NoSuchProviderException {
		if (mode != VERIFIER)
			throw new IllegalStateException(
					"call a PKCS7Tool instance not for verify.");

//		byte[] signDataBytes = new BASE64Decoder().decodeBuffer(signData);
		 byte[] signDataBytes = hexStrToBytes(signData);
		X509Certificate x509Certificate = (X509Certificate) rootCertificate;
		Signature signature = Signature.getInstance(x509Certificate
				.getSigAlgName());
		signature.initVerify(x509Certificate);
		signature.update(data);
		return signature.verify(signDataBytes);
	}

	/**
	 * 方法: bytesToStrHex 描述: 数组转换成16进制字符串
	 * 
	 * @param bytes
	 *            源数组
	 * @return String
	 */
	public static final String bytesToStrHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length);
		String sTemp;
		for (int i = 0; i < bytes.length; i++) {
			sTemp = Integer.toHexString(0xFF & bytes[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	/**
     * 方法: hexStrToBytes
     * 描述: 将16进制字符串还原为字节数组
     * @param str 16进制字符串
     * @return byte[]
     */
    private static final byte[] hexStrToBytes(String str) {
        byte[] bytes;
        bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

	/**
	 * 匹配私钥用法
	 * 
	 * @param keyUsage
	 * @param usage
	 * @return
	 */
	private static boolean matchUsage(boolean[] keyUsage, int usage) {
		if (usage == 0 || keyUsage == null)
			return true;
		for (int i = 0; i < Math.min(keyUsage.length, 32); i++) {
			if ((usage & (1 << i)) != 0 && !keyUsage[i])
				return false;
		}
		return true;
	}

	private static void init() {
		if (jvm != 0)
			return;
		String vendor = System.getProperty("java.vm.vendor");
		if (vendor == null)
			vendor = "";
		String vendorUC = vendor.toUpperCase();
		try {
			if (vendorUC.indexOf("ORACLE") >= 0) {
				jvm = 'S';
				algorithmId = Class.forName("sun.security.x509.AlgorithmId");
				derValue = Class.forName("sun.security.util.DerValue");
				objectIdentifier = Class
						.forName("sun.security.util.ObjectIdentifier");
				x500Name = Class.forName("sun.security.x509.X500Name");
			} else if (vendorUC.indexOf("IBM") >= 0) {
				jvm = 'I';
				algorithmId = Class
						.forName("com.ibm.security.x509.AlgorithmId");
				derValue = Class.forName("com.ibm.security.util.DerValue");
				objectIdentifier = Class
						.forName("com.ibm.security.util.ObjectIdentifier");
				x500Name = Class.forName("com.ibm.security.x509.X500Name");
			} else {
				System.out.println("Not support JRE: " + vendor);
				System.exit(-1);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * @return 返回 digestAlgorithm。
	 */
	public final String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	/**
	 * @param digestAlgorithm
	 *            要设置的 digestAlgorithm。
	 */
	public final void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	/**
	 * @return 返回 signingAlgorithm。
	 */
	public final String getSigningAlgorithm() {
		return signingAlgorithm;
	}

	/**
	 * @param signingAlgorithm
	 *            要设置的 signingAlgorithm。
	 */
	public final void setSigningAlgorithm(String signingAlgorithm) {
		this.signingAlgorithm = signingAlgorithm;
	}

	public static void setDebug(boolean debug) {
		PKCS12Tool.debug = debug;
	}
	
	
	public static void main(String[] args) throws Exception{
		PKCS12Tool tool = getSigner("E:\\sign\\BKT0982010062819.pfx", "111111");
//		String sourceDate = "<merchcode>000500000001</merchcode><msgtype>IPOS</msgtype><txcode>2100</txcode><hvbrno>313662000015</hvbrno><sw_tx_code>IPOS</sw_tx_code><txtpcd>C200</txtpcd><txamt>100.78</txamt><dbtrnm>米珈对应内部账户</dbtrnm><dbtrid>9100300100038</dbtrid><dbtrissr>遂宁市商业银行</dbtrissr><dbtrtype>AT00</dbtrtype><dbtrbrnchid>313662000015</dbtrbrnchid><cash_opn_br_no>402611099974</cash_opn_br_no><cdtrnm>小胖003</cdtrnm><cdtrid>6228696811001399518</cdtrid><ctgypurpcd>02001</ctgypurpcd><remark>22</remark><usage>33</usage><tel>999999</tel><passwd></passwd>";
		String sourceDate = "<merchcode>4</merchcode><msgtype>BKPY</msgtype><txcode>1100</txcode><hvbrno>313662000015</hvbrno><sw_tx_code>BKPY</sw_tx_code><txtpcd>C200</txtpcd><txamt>100000.00</txamt><dbtrnm>理财产品暂收</dbtrnm><dbtrid>9224100100079</dbtrid><dbtrissr>遂宁市商业银行</dbtrissr><dbtrtype>AT00</dbtrtype><dbtrbrnchid>313662000015</dbtrbrnchid><cash_opn_br_no>313662000015</cash_opn_br_no><cdtrnm>李乐</cdtrnm><cdtrid>6210900300000515274</cdtrid><ctgypurpcd>02001</ctgypurpcd><remark></remark><usage></usage><tel>999999</tel><passwd></passwd>";
		
		String singnData = tool.sign(sourceDate.getBytes());
		
		System.out.println(singnData);
		
		
		PKCS12Tool tool2 = getVerifier("E:\\sign\\BKT0982010062819.cer");
		boolean t = tool2.verify(singnData, sourceDate.getBytes());
		System.out.println(t);
	}
}
