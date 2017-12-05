package com.pay.business.merchant.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.cache.annotations.Cacheable;
import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.IdUtils;
import com.core.teamwork.base.util.ValidatorUtil;
import com.core.teamwork.base.util.encrypt.MD5;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.redis.RedisDBUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.entity.Payv2BussCompanyShop;
import com.pay.business.merchant.entity.Payv2BussSupportPayWay;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.mapper.Payv2BussCompanyAppMapper;
import com.pay.business.merchant.mapper.Payv2BussCompanyMapper;
import com.pay.business.merchant.mapper.Payv2BussCompanyShopMapper;
import com.pay.business.merchant.mapper.Payv2BussSupportPayWayMapper;
import com.pay.business.merchant.mapper.Payv2ChannelMapper;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.payv2.entity.Payv2ProvincesCity;
import com.pay.business.payv2.mapper.Payv2ProvincesCityMapper;
import com.pay.business.util.GenerateUtil;
import com.pay.business.util.ParameterEunm;

/**
 * @author cyl
 * @version 
 */
@Service("payv2BussCompanyService")
public class Payv2BussCompanyServiceImpl extends BaseServiceImpl<Payv2BussCompany, Payv2BussCompanyMapper> implements Payv2BussCompanyService {
	// 注入当前dao对象
    @Autowired
    private Payv2BussCompanyMapper payv2BussCompanyMapper;
    @Autowired
    private Payv2BussCompanyShopMapper payv2BussCompanyShopMapper;
    @Autowired
    private Payv2BussCompanyAppMapper payv2BussCompanyAppMapper;
    @Autowired
    private Payv2ProvincesCityMapper payv2ProvincesCityMapper;
    @Autowired
    private Payv2ChannelMapper payv2ChannelMapper;
    @Autowired
    private Payv2BussSupportPayWayMapper payv2BussSupportPayWayMapper;
    
    public static final String PAY_ERROR_PASSWORD_KEY = "pay_error_password_%s_%s";// key

    public Payv2BussCompanyServiceImpl() {
        setMapperClass(Payv2BussCompanyMapper.class, Payv2BussCompany.class);
    }
    
 	public Payv2BussCompany selectSingle(Payv2BussCompany t) {
		return payv2BussCompanyMapper.selectSingle(t);
	}

	public List<Payv2BussCompany> selectByObject(Payv2BussCompany t) {
		return payv2BussCompanyMapper.selectByObject(t);
	}

	/**
	 * 商户列表
	 */
	public PageObject<Payv2BussCompany> companyList(Map<String, Object> map) {
		int totalData = payv2BussCompanyMapper.getCountBySearch(map);
		map.put("sortName", "create_time");
		map.put("sortOrder", "desc");
		PageHelper pageHelper = new PageHelper(totalData, map);
		//统计公司下的应用数量
		List<Payv2BussCompany> list = payv2BussCompanyMapper.pageQueryByObjectBySearch(pageHelper.getMap());
		for (Payv2BussCompany payv2BussCompany : list) {
			//拼接营业范围
			if(payv2BussCompany.getCompanyRangeProvince()==null){
				payv2BussCompany.setBussRange("全国");
			}else{
				Payv2ProvincesCity pro = new Payv2ProvincesCity();
				pro.setId(payv2BussCompany.getCompanyRangeProvince());
				pro = payv2ProvincesCityMapper.selectSingle(pro);
				
				Payv2ProvincesCity city = new Payv2ProvincesCity();
				city.setId(payv2BussCompany.getCompanyRangeCity());
				city = payv2ProvincesCityMapper.selectSingle(city);
				
				payv2BussCompany.setBussRange(pro.getName()+city.getName());
			}
			if(payv2BussCompany.getChannelId() != null){
				
				Payv2Channel channel = new Payv2Channel();
				channel.setId(payv2BussCompany.getChannelId());
				channel = payv2ChannelMapper.selectSingle(channel);
				if(channel != null){
					payv2BussCompany.setChannelName(channel.getChannelName());
				}else{
					payv2BussCompany.setChannelName("");
				}
			}
		}
		PageObject<Payv2BussCompany> pageObject = pageHelper.getPageObject();
		pageObject.setDataList(list);
		return pageObject;
	}
	
	public Payv2BussCompany getpayv2BussCompanyInfo(Payv2BussCompany payv2BussCompany){
		payv2BussCompany=payv2BussCompanyMapper.selectSingle(payv2BussCompany);
		if(payv2BussCompany!=null){
			//拼接营业范围
			if(payv2BussCompany.getCompanyRangeProvince()==null){
				payv2BussCompany.setBussRange("全国");
			}else{
				Payv2ProvincesCity pro = new Payv2ProvincesCity();
				pro.setId(payv2BussCompany.getCompanyRangeProvince());
				pro = payv2ProvincesCityMapper.selectSingle(pro);
				
				Payv2ProvincesCity city = new Payv2ProvincesCity();
				city.setId(payv2BussCompany.getCompanyRangeCity());
				city = payv2ProvincesCityMapper.selectSingle(city);
				
				payv2BussCompany.setBussRange(pro.getName()+city.getName());
			}
			Payv2Channel channel = new Payv2Channel();
			channel.setId(payv2BussCompany.getChannelId());
			channel = payv2ChannelMapper.selectSingle(channel);
			if(channel != null){
				payv2BussCompany.setChannelName(channel.getChannelName());
			}
		}
		return payv2BussCompany;
	}

	public List<Payv2BussCompany> selectForCompanyApp(Payv2BussCompany t) {
		List<Payv2BussCompany> list = payv2BussCompanyMapper.selectForCompanyApp(t);
		return list;
	}

	public List<Payv2BussCompany> selectForCompanyShop(Payv2BussCompany t) {
		List<Payv2BussCompany> list = payv2BussCompanyMapper.selectForCompanyShop(t);
		return list;
	}

	public int getCountByUserName(String userName) {
		return payv2BussCompanyMapper.getCountByUserName(userName);
	}

	public Payv2BussCompany login(String userName, String password) {
		return payv2BussCompanyMapper.login(userName,password);
	}
	
	public PageObject<Payv2BussCompany> selectBussCompanyByNameLike(
			Map<String, Object> map) {
		int totalData = payv2BussCompanyMapper.selectBussCompanyByNameLikeCount(map);
		PageHelper pageHelper = new PageHelper(totalData, map);
		//统计公司下的应用数量
		List<Payv2BussCompany> list = payv2BussCompanyMapper.selectBussCompanyByNameLike(pageHelper.getMap());
		PageObject<Payv2BussCompany> pageObject = pageHelper.getPageObject();
		pageObject.setDataList(list);
		return pageObject;
	}

	
	public Map<String,Object> scan(Map<String, Object> map) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//保存商户信息
		Payv2BussCompany company = M2O(map);
		if(StringUtils.isNoneEmpty(company.getContactsPhone())){
			company.setUserName(company.getContactsPhone());
			//System.out.println(MD5.GetMD5Code("123456"));
			//System.out.println(MD5.GetMD5Code("e10adc3949ba59abbe56e057f20f883e"));
			company.setPassWord(MD5.GetMD5Code("e10adc3949ba59abbe56e057f20f883e"));//123456的第一次转码，把第二次转码弄为默认密码
		}
		payv2BussCompanyMapper.insertByEntity(company);
		//保存店铺信息
		String shopName = map.get("shopName") == null ? "" : map.get("shopName").toString();
		long shopRangeProvince = map.get("shopRangeProvince") == null ? 0 : Long.valueOf(map.get("shopRangeProvince").toString());
		//long shopRangeCity = map.get("shopRangeCity") == null ? 0 : Long.valueOf(map.get("shopRangeCity").toString());
		String shopContacts = map.get("shopContacts") == null ? "" : map.get("shopContacts").toString();
		String shopAddress = map.get("shopAddress") == null ? "" : map.get("shopAddress").toString();
		String shopContactsPhone = map.get("shopContactsPhone") == null ? "" : map.get("shopContactsPhone").toString();
		String shopCard = map.get("shopCard") == null ? "" : map.get("shopCard").toString();
		//如果有商户，则关联商户
		//String companyId = map.get("companyId") == null ? "" : map.get("companyId").toString();
		//1.获取shopkey
		String urlString = map.get("qrRequestUrl").toString();
		String shopKey = urlString.substring(urlString.indexOf("=")+1,urlString.length());
		
		Payv2BussCompanyShop shop = new Payv2BussCompanyShop();
		shop.setShopKey(shopKey);
		shop = payv2BussCompanyShopMapper.selectSingle(shop);
		if(shop != null){
			shop.setCompanyId(company.getId());
			shop.setShopKey(shopKey);
			shop.setShopName(shopName);//店铺名称shopName
			shop.setShopCard(shopCard);//门店号
			shop.setShopRangeProvince(shopRangeProvince);//门店地址shop_range_province
			shop.setShopAddress(shopAddress);//详细地址shop_address
			shop.setShopContacts(shopContacts);//联系人姓名shop_contacts
			shop.setShopContactsPhone(shopContactsPhone);//联系人手机号shop_contacts_phone
			payv2BussCompanyShopMapper.updateByEntity(shop);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		}else{
			resultMap = ReMessage.resultBack(ParameterEunm.SHOP_NOT_EXIST, null);
		}
		
		return resultMap;
		
	}

	public Map<String, Object> connected(Map<String, Object> map) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		//更新商户信息
		Payv2BussCompany company = M2O(map);
		payv2BussCompanyMapper.updateByEntity(company);
		//保存店铺信息
		String shopName = map.get("shopName") == null ? "" : map.get("shopName").toString();
		long shopRangeProvince = map.get("shopRangeProvince") == null ? 0 : Long.valueOf(map.get("shopRangeProvince").toString());
		long shopRangeCity = map.get("shopRangeCity") == null ? 0 : Long.valueOf(map.get("shopRangeCity").toString());
		String shopContacts = map.get("shopContacts") == null ? "" : map.get("shopContacts").toString();
		String shopAddress = map.get("shopAddress") == null ? "" : map.get("shopAddress").toString();
		String shopContactsPhone = map.get("shopContactsPhone") == null ? "" : map.get("shopContactsPhone").toString();
		String shopCard = map.get("shopCard") == null ? "" : map.get("shopCard").toString();
		
		//如果有商户，则关联商户
		String companyId = map.get("companyId") == null ? "" : map.get("companyId").toString();
		//1.获取shopkey
		String urlString = map.get("qrRequestUrl").toString();
		String shopKey = urlString.substring(urlString.indexOf("=")+1,urlString.length());
		
		Payv2BussCompanyShop shop = new Payv2BussCompanyShop();
		shop.setShopKey(shopKey);
		shop = payv2BussCompanyShopMapper.selectSingle(shop);
		if(shop != null){
			
			shop.setShopKey(shopKey);
			shop.setCompanyId(Long.valueOf(companyId));//
			shop.setShopName(shopName);//店铺名称shopName
			shop.setShopCard(shopCard);//门店号
			shop.setShopRangeProvince(shopRangeProvince);//门店地址shop_range_province
			shop.setShopRangeCity(shopRangeCity);
			shop.setShopAddress(shopAddress);//详细地址shop_address
			shop.setShopContacts(shopContacts);//联系人姓名shop_contacts
			shop.setShopContactsPhone(shopContactsPhone);//联系人手机号shop_contacts_phone
			payv2BussCompanyShopMapper.updateByEntity(shop);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		}else{
			resultMap = ReMessage.resultBack(ParameterEunm.SHOP_NOT_EXIST, null);
		}
		return resultMap;
	}
	/**
	 * 累计商户支付密码错误次数
	 */
	public int lockPayPassWord(Long companyId, int type) throws Exception {
		String redisKey = String.format(PAY_ERROR_PASSWORD_KEY, companyId, type);
		// 先检查redis中的错误次数
		String numStr = RedisDBUtil.redisDao.get(redisKey);
		RedisDBUtil.redisDao.increase(redisKey);
		if (ValidatorUtil.isNotEmpty(numStr)) {
			Long num = Long.valueOf(numStr);
			// 判断是否等于于5， 如果大于5限制1小时锁定，并设置值为5
			if (num>=5) {
				RedisDBUtil.redisDao.setString(redisKey, "5",3600);
				return 5;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	/**
	 * 判断是否被锁定
	 */
	public int lockNumber(Long companyId, int type) throws Exception {
		String redisKey = String.format(PAY_ERROR_PASSWORD_KEY, companyId, type);
		// 先检查redis中的错误次数
		String numStr = RedisDBUtil.redisDao.get(redisKey);
		if (ValidatorUtil.isNotEmpty(numStr)) {
			Long num = Long.valueOf(numStr);
			// 判断是否等于于5
			if (num>=5) {
				return 5;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
	/**
	 * 删除redis记录
	 */
	public void delKey(Long companyId, int type) throws Exception {
		String redisKey = String.format(PAY_ERROR_PASSWORD_KEY, companyId, type);
		RedisDBUtil.redisDao.delete(redisKey);
	}

	/**
	 * 根据商户id查询（缓存）
	 * @param id
	 * @return
	 */
	@Cacheable(
			region = "payv2BussCompanyService",
			namespace = "getComById",
			fieldsKey = {"#id"},
			expire = 300
			)
	public Payv2BussCompany getComById(Long id) {
		Payv2BussCompany pbc = new Payv2BussCompany();
		pbc.setId(id);
		pbc = payv2BussCompanyMapper.selectSingle(pbc);
		return pbc;
	}

	/**
	 * 自动生成商户、app、添加通道
	 * @param map
	 * @return
	 */
	public Map<String, Object> generate(Map<String, Object> map) {
		Map<String,Object> resultMap = new HashMap<>();
		//添加商户
		Payv2BussCompany pbc = new Payv2BussCompany();
		pbc.setCreateTime(new Date());
		pbc.setChannelId(3l);
		payv2BussCompanyMapper.insertByEntity(pbc);
		String ceshi = "ceshi"+pbc.getId();
		pbc.setUserName(ceshi);
		pbc.setPassWord(MD5.GetMD5Code(MD5.GetMD5Code(ceshi)));
		pbc.setCompanyName(ceshi);
		pbc.setLicenseNum(ceshi);
		pbc.setLicenseAddr("广东深圳");
		pbc.setLicensePic(ceshi);
		pbc.setOrganizationCode(ceshi);
		pbc.setOrganizationCodeUrl(ceshi);
		pbc.setLegalName(ceshi);
		pbc.setLegalIdCard("431245198611113678");
		pbc.setContactsName(ceshi);
		pbc.setContactsPhone("15220183163");
		pbc.setContactsMail("403341512@qq.com");
		pbc.setAccountBank("中国工商银行");
		pbc.setAccountName(ceshi);
		pbc.setAccountCard(ceshi);
		pbc.setCompanyStatus(2);
		payv2BussCompanyMapper.updateByEntity(pbc);
		//添加app
		Payv2BussCompanyApp pbca = new Payv2BussCompanyApp();
		pbca.setCreateTime(new Date());
		payv2BussCompanyAppMapper.insertByEntity(pbca);
		String ceshiapp = "ceshiapp"+pbca.getId();
		pbca.setCompanyId(pbc.getId());
		pbca.setAppName(ceshiapp);
		pbca.setAppTypeId(1l);
		pbca.setAppIcon(ceshiapp);
		pbca.setAppDesc(ceshiapp+ceshiapp+ceshiapp);
		pbca.setWebUrl("http://"+ceshiapp);
		String pack = "com.jinfu."+ceshiapp;
		pbca.setIosIphoneId(pack);
		pbca.setIosIphoneTestId(pack);
		pbca.setIosIphoneUrl("http://"+pack);
		pbca.setAndroidAppMd5(pack);
		pbca.setAndroidAppPackage(pack);
		pbca.setAndroidAppUrl("http://"+pack);
		String appkey=GenerateUtil.getRandomString(64);
		pbca.setAppKey(appkey);
		pbca.setAppSecret(IdUtils.createRandomString(40));
		pbca.setAppStatus(2);
		payv2BussCompanyAppMapper.updateByEntity(pbca);
		//添加通道
		Payv2BussSupportPayWay pbspw = new Payv2BussSupportPayWay();
		pbspw.setParentId(pbc.getId());
		pbspw.setPayType(2);
		pbspw.setRateId(Long.valueOf(map.get("rateId").toString()));
		pbspw.setPayWayId(Long.valueOf(map.get("payWayId").toString()));
		pbspw.setPayWayRate(10.0);
		pbspw.setSortNum(1);
		pbspw.setCreateTime(new Date());
		payv2BussSupportPayWayMapper.insertByEntity(pbspw);
		
		
		resultMap.put("appKey", appkey);
		resultMap.put("userName", ceshi);
		resultMap.put("passWord", ceshi);
		return resultMap;
	}

	/**
	 * 检查companyKey是否有效
	 * 先查询商户状态是否通过
	 * @param map
	 * @return
	 */
//	@Cacheable(
//			region = "payv2BussCompanyService",
//			namespace = "getPbc",
//			fieldsKey = {"#companyKey"},
//			expire = 300
//			)
	public Map<String, Object> getPbc(String companyKey) {
		Map<String,Object> map = new HashMap<>();
		map.put("companyKey", companyKey);
		map.put("status", 1);
		map.put("companyStatus", 2);
		map.put("isDelete", 2);
		return payv2BussCompanyMapper.queryByCompanyKey(map);
	}
	
	/**
	 * 查询商户收款码
	 * @param map
	 * @return
	 */
	public Payv2BussCompany queryCodeByKey(String companyKey){
		Map<String,Object> map = new HashMap<>();
		map.put("companyKey", companyKey);
		map.put("companyStatus", 2);
		map.put("isDelete", 2);
		return payv2BussCompanyMapper.queryCodeByKey(map);
	}
}
