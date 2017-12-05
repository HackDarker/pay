package com.pay.business.merchant.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyCode;
import com.pay.business.merchant.entity.Payv2BussSupportPayWay;
import com.pay.business.merchant.entity.Payv2CompanyPayType;
import com.pay.business.merchant.mapper.Payv2BussCompanyCodeMapper;
import com.pay.business.merchant.service.Payv2BussCompanyCodeService;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.merchant.service.Payv2CompanyPayTypeService;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.mapper.Payv2PayOrderMapper;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.OrderUtil;
import com.pay.business.util.PayFinalUtil;
import com.pay.business.util.QRCodeUtilByZXing;
import com.pay.business.util.mail.MailRun;
import com.pay.business.util.qrcode.ImageUtil;
import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.ReadProChange;
import com.core.teamwork.base.util.ftp.FtpUploadClient;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

/**
 * @author cyl
 * @version 
 */
@Service("payv2BussCompanyCodeService")
public class Payv2BussCompanyCodeServiceImpl extends BaseServiceImpl<Payv2BussCompanyCode, Payv2BussCompanyCodeMapper> implements Payv2BussCompanyCodeService {
	// 注入当前dao对象
    @Autowired
    private Payv2BussCompanyCodeMapper payv2BussCompanyCodeMapper;
    @Autowired
    private Payv2BussSupportPayWayService payv2BussSupportPayWayService;
    @Autowired
    private Payv2PayOrderMapper payv2PayOrderMapper;
    @Autowired
	private Payv2CompanyPayTypeService payv2CompanyPayTypeService;
    
    public Payv2BussCompanyCodeServiceImpl() {
        setMapperClass(Payv2BussCompanyCodeMapper.class, Payv2BussCompanyCode.class);
    }
    
    /**
	 * 检查商户是否支持此支付方式的通道
	 * @param companyId
	 * @param payPlatform
	 * @return
	 */
	public boolean checkComRate(Long companyId,Integer payPlatform){
		String dictName = "SCAN";
		if(payPlatform==2){
			dictName = "GZH";
		}
		
		List<Payv2BussSupportPayWay> payWayList = payv2BussSupportPayWayService
				.queryByCompany(companyId, payPlatform, dictName,null);
		
		if(payWayList!=null&&payWayList.size()>0){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 创建订单(提供点游)
	 * 
	 * @param map
	 * @return
	 */
	public Map<String, String> createOrder(Map<String, Object> map, Payv2BussCompany pbc
			, Integer payViewType,String dictName) {
		Map<String, String> resultMap = new HashMap<>();
		Double payMoney = Double.valueOf(map.get("payMoney").toString());
		//根据支付类型和商户查询商户支持支付通道
		List<Payv2BussSupportPayWay> payWayList = payv2BussSupportPayWayService
				.queryByCompany(pbc.getId(), payViewType, dictName,payMoney);
		
		if(payWayList==null||payWayList.size()==0){
			resultMap.put("status", PayFinalUtil.PAY_TYPE_FAIL);
			MailRun.send("获取不到通道","未配置支付通道或支付类型不支持",null,null,null
					,""+payMoney,pbc.getCompanyName(),null,null);
			return resultMap;
		}
		
		//获取支付方式、通道
		Payv2BussSupportPayWay pbspw = payv2BussSupportPayWayService.getWayRate(payWayList, payMoney, pbc.getId()
				, payViewType,pbc.getIsRandom(),dictName);
		
		if(pbspw==null){
			resultMap.put("status", PayFinalUtil.PAY_STATUS_FAIL);
			MailRun.send("超过限额","已超过限额,请检查支付通道单笔额度和每日额度",null,null,null
					,""+payMoney,pbc.getCompanyName(),null,null);
			return resultMap;
		}
		//支付通道id
		resultMap.put("rateId", ""+pbspw.getRateId());
		resultMap.put("dictName", pbspw.getDictName());
		resultMap.put("rateKey1", pbspw.getRateKey1());
		resultMap.put("rateKey2", pbspw.getRateKey2());
		resultMap.put("rateKey3", pbspw.getRateKey3()==null?"":pbspw.getRateKey3());
		resultMap.put("rateKey4", pbspw.getRateKey4()==null?"":pbspw.getRateKey4());
		resultMap.put("rateKey5", pbspw.getRateKey5()==null?"":pbspw.getRateKey5());
		resultMap.put("rateKey6", pbspw.getRateKey6()==null?"":pbspw.getRateKey6());
		resultMap.put("gzhAppId", pbspw.getGzhAppId()==null?"":pbspw.getGzhAppId());
		resultMap.put("gzhKey", pbspw.getGzhKey()==null?"":pbspw.getGzhKey());
		resultMap.put("rateCompanyName", pbspw.getRateName());
		resultMap.put("payWayName", pbspw.getPayWayName());
		resultMap.put("companyName", pbc.getCompanyName());
		
		// 创建订单
		Payv2PayOrder payOrder = new Payv2PayOrder();

		// 创建订单
		payOrder = createOrder(map, payOrder, pbc, payMoney, pbspw.getPayWayId()
				, pbspw.getRateId(),pbspw.getPayWayRate(),pbspw.getCostRate());

		resultMap.put("status", PayFinalUtil.PAY_STATUS_SUSSESS);
		resultMap.put("orderNum", payOrder.getOrderNum());
		resultMap.put("payMoney", ""+payOrder.getPayMoney());
		resultMap.put("orderName", payOrder.getOrderName()==null?"":payOrder.getOrderName());

		return resultMap;
	}
	
	/**
	 * 创建订单(提供点游)
	 * 
	 * @param map
	 * @param payOrder
	 * @param pbca
	 * @param payUser
	 * @param payMoney
	 * @return
	 */
	public Payv2PayOrder createOrder(Map<String, Object> map, Payv2PayOrder payOrder, Payv2BussCompany pbc, Double payMoney,
			Long payWayId, Long rateId,Double companyRate,Double costRate) {
		payOrder.setUserAgent(map.get("userAgent")==null?"未获取到User-Agent":map.get("userAgent").toString());
		payOrder.setIp(map.get("address").toString());
		payOrder.setMethod(map.get("method").toString());
		payOrder.setPayWayId(payWayId);
		payOrder.setChannelId(pbc.getChannelId()); // 渠道商id
		payOrder.setCompanyId(pbc.getId()); // 商户id
		payOrder.setCreateTime(new Date());
		payOrder.setPayMoney(payMoney);
		payOrder.setOrderType(2);
		
		payOrder.setRateId(rateId);
		// 通道费用
		payOrder.setPayWayMoney(getPayWayMoney(payMoney, companyRate));
		//费率
		payOrder.setBussWayRate(companyRate);
		//成本费率
		payOrder.setCostRate(costRate);
		//成本手续费
		payOrder.setCostRateMoney(getPayWayMoney(payMoney, costRate));
		payOrder.setPayStatus(PayFinalUtil.PAY_ORDER_NOT); // 未支付
		// userId和商户订单组成
		payOrder.setOrderNum(OrderUtil.getOrderNum()); // 订单生产（规则？）
		if(!StringUtils.isNoneBlank(pbc.getNotifyUrl())){
			payOrder.setNotifyUrl(pbc.getNotifyUrl()); // 回调地址（服务器）
		}
		if(map.get("remark")!=null){
			payOrder.setRemark(map.get("remark").toString());
		}
		payv2PayOrderMapper.insertByEntity(payOrder);
		return payOrder;
	}
	
	/**
	 * 通道费用最小值0.01分
	 * 
	 * @param payMoney
	 * @param payWayRate
	 * @return
	 */
	public Double getPayWayMoney(Double payMoney, Double payWayRate) {
		
		double re = DecimalUtil.getCeil(DecimalUtil.div(DecimalUtil.mul(payMoney.doubleValue()
				, payWayRate.doubleValue()), 1000, 10),2);
		System.out.println("支付金额："+payMoney+";费率："+payWayRate+";算出的通道费金额:"+re);
		return re;
		
	}	
	public PageObject<Payv2BussCompanyCode> pageCode(Map<String,Object> map) {
		int totalData = payv2BussCompanyCodeMapper.getCount(map);
		if(totalData == 0){
			return null;
		}
		PageHelper pageHelper = new PageHelper(totalData, map);
		List<Payv2BussCompanyCode> codeList  = payv2BussCompanyCodeMapper.pageCode(pageHelper.getMap());
		PageObject pageObject = pageHelper.getPageObject();
		pageObject.setDataList(codeList);
		return pageObject;
	}

	public Map<String,Object> downloadCode(Payv2BussCompany company,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1、生成二维码
		String companyKey = company.getCompanyKey();
		String codeUrl = ReadProChange.getValue("code_url");
		String content = codeUrl + "?companyKey="+companyKey;
		// 存放二维码、最终图片的路径
		String rootPath = this.getClass().getResource("/").getFile().toString().replace("classes/", "").replace("WEB-INF/", "");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = multiFormatWriter.encode(content,BarcodeFormat.QR_CODE, 300, 300, hints);
        File codeImg = new File(rootPath, "生成的二维码.jpg");
        QRCodeUtilByZXing zx = new QRCodeUtilByZXing();
        zx.writeToFile(bitMatrix, "jpg", codeImg);
//		            System.out.println("这是本地二维码地址："+codeImg.getPath());
        
        // 2、获取商户支持的支付平台与商户名称，拼接好背景图片
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("companyId", company.getId());
        List<Payv2CompanyPayType> payTypeList = payv2CompanyPayTypeService.query(paramMap);
        if(payTypeList.size() == 0){
        	resultMap.put("message", "该商户没有支持的支付平台");
        	return resultMap;
        }
        boolean isALP = false;
        boolean isWX = false;
        boolean isQQ = false;
        for(Payv2CompanyPayType PCPT : payTypeList){
        	if(PCPT.getPayWayId() == 1){
        		isALP = true;
        	}
        	if(PCPT.getPayWayId() == 2){
        		isWX = true;
        	}
        	if(PCPT.getPayWayId() == 3){
        		isQQ = true;
        	}
        }
        int XAxis = 128;
        int YAxis = 38;
        String bgImgUrl = null;
        String backPath = rootPath+"public/img/二维码背景图片.png";
        if(isALP){
        	bgImgUrl = rootPath+"最终二维码.jpg";
        	ImageUtil.writeBackImg2(backPath, rootPath+"public/img/支付宝.jpg", bgImgUrl,XAxis,YAxis);
        	XAxis += 56;
        }
        if(isWX){
        	ImageUtil.writeBackImg2(bgImgUrl == null?backPath:bgImgUrl, rootPath+"public/img/微信.jpg", rootPath+"最终二维码.jpg",XAxis,YAxis);
        	XAxis += 56;
        	bgImgUrl = rootPath+"最终二维码.jpg";
        }
        if(isQQ){
        	ImageUtil.writeBackImg2(bgImgUrl == null?backPath:bgImgUrl, rootPath+"public/img/QQ.jpg", rootPath+"最终二维码.jpg",XAxis,YAxis);
        	XAxis += 56;
        	bgImgUrl = rootPath+"最终二维码.jpg";
        }
        ImageUtil.markImageByText(company.getCompanyName(),bgImgUrl,bgImgUrl,0,446,null);
        
		// 3、将二维码图片与背景图片拼接好，并返回最终图片的地址
        ImageUtil.writeBackImg(bgImgUrl, codeImg.getPath(),bgImgUrl);
//		  System.out.println("这是最终图片的地址："+bgImgUrl);
        
		// 4、将此地址的图片响应给用户        
        File file = new File(bgImgUrl);
        InputStream in = null;
        String imageName = company.getCompanyName()+"收款码.jpg";
        byte[] buffer = new byte[1024];
        int length;
        in = new FileInputStream(file);
        // 读取字符编码  
        String csvEncoding = "utf-8";
        // 设置响应  
        response.setCharacterEncoding(csvEncoding);
        response.setContentType("text/csv; charset=" + csvEncoding);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename="  
                + URLEncoder.encode(imageName, csvEncoding));
  
        // 写出响应  
        OutputStream os = response.getOutputStream();
        // 开始读取  
        while ((length = in.read(buffer)) != -1) {
          os.write(buffer, 0, length);
        }
        in.close();
        os.flush();
        os.close();
        
        // 5、删除生成的最终图片与二维码
        codeImg.delete();
        file.delete();
		return paramMap;
	}

	public Map<String,Object> getCode(Payv2BussCompany company)  throws Exception{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		// 1、生成二维码
		String companyKey = company.getCompanyKey();
		String codeUrl = ReadProChange.getValue("code_url");
		System.out.println(codeUrl);
		String content = codeUrl + "?companyKey="+companyKey;
		// 存放二维码、最终图片的路径
		String rootPath = this.getClass().getResource("/").getFile().toString().replace("classes/", "").replace("WEB-INF/", "");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = multiFormatWriter.encode(content,BarcodeFormat.QR_CODE, 300, 300, hints);
        File codeImg = new File(rootPath, "生成的二维码.jpg");
        QRCodeUtilByZXing zx = new QRCodeUtilByZXing();
        zx.writeToFile(bitMatrix, "jpg", codeImg);
//		            System.out.println("这是本地二维码地址："+codeImg.getPath());
        
        // 2、获取商户支持的支付平台与商户名称，拼接好背景图片
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("companyId", company.getId());
        List<Payv2CompanyPayType> payTypeList = payv2CompanyPayTypeService.query(paramMap);
        if(payTypeList.size() == 0){
        	resultMap.put("message", "该商户没有支持的支付平台");
        	return resultMap;
        }
        boolean isALP = false;
        boolean isWX = false;
        boolean isQQ = false;
        for(Payv2CompanyPayType PCPT : payTypeList){
        	if(PCPT.getPayWayId() == 1){
        		isALP = true;
        	}
        	if(PCPT.getPayWayId() == 2){
        		isWX = true;
        	}
        	if(PCPT.getPayWayId() == 3){
        		isQQ = true;
        	}
        }
        int XAxis = 128;
        int YAxis = 38;
        String bgImgUrl = null;
        String backPath = rootPath+"public/img/二维码背景图片.png";
        if(isALP){
        	bgImgUrl = rootPath+"最终二维码.jpg";
        	ImageUtil.writeBackImg2(backPath, rootPath+"public/img/支付宝.jpg", bgImgUrl,XAxis,YAxis);
        	XAxis += 56;
        }
        if(isWX){
        	ImageUtil.writeBackImg2(bgImgUrl == null?backPath:bgImgUrl, rootPath+"public/img/微信.jpg", rootPath+"最终二维码.jpg",XAxis,YAxis);
        	XAxis += 56;
        	bgImgUrl = rootPath+"最终二维码.jpg";
        }
        if(isQQ){
        	ImageUtil.writeBackImg2(bgImgUrl == null?backPath:bgImgUrl, rootPath+"public/img/QQ.jpg", rootPath+"最终二维码.jpg",XAxis,YAxis);
        	XAxis += 56;
        	bgImgUrl = rootPath+"最终二维码.jpg";
        }
        ImageUtil.markImageByText(company.getCompanyName(),bgImgUrl,bgImgUrl,0,446,null);
        
		// 3、将二维码图片与背景图片拼接好，并返回最终图片的地址
        ImageUtil.writeBackImg(bgImgUrl, codeImg.getPath(),bgImgUrl);
//		            System.out.println("这是最终图片的地址："+bgImgUrl);
        String codeFileUrl = FtpUploadClient.getInstance().uploadFile(company.getCompanyName() + "二维码.jpg", new File(bgImgUrl));
//		            System.out.println("这是最终图片的网络地址："+codeFileUrl);
        // 4、删除生成的最终图片与二维码
        codeImg.delete();
        new File(bgImgUrl).delete();
        resultMap.put("codeFileUrl", codeFileUrl);
		return resultMap;
	
	}
	
}
