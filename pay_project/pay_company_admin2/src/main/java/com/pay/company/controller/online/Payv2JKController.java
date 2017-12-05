package com.pay.company.controller.online;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.core.teamwork.base.util.IdUtils;
import com.core.teamwork.base.util.ObjectUtil;
import com.core.teamwork.base.util.ReadProChange;
import com.core.teamwork.base.util.redis.RedisDBUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.jk.entity.JkCompanyInfo;
import com.pay.business.jk.mapper.JkCompanyInfoMapper;
import com.pay.business.jk.service.JkCompanyInfoService;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.RandomUtil;
import com.pay.business.util.mail.SslSmtpMailUtil;
import com.pay.company.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/Payv2JK/*")
public class Payv2JKController extends BaseManagerController<JkCompanyInfo, JkCompanyInfoMapper> {
	private static final Logger logger = Logger.getLogger(Payv2JKController.class);
	
	@Autowired
	private JkCompanyInfoService jkCompanyInfoService;
	
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	
	/**
	 * 获取商户的账户余额与接口信息
	 * 
	 * @param map
	 * @return Map<String, Object>
	 */
	@ResponseBody
	@RequestMapping("acountBalance")
	public Map<String, Object> acountBalance(@RequestParam Map<String, Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			Payv2BussCompany company = getAdmin();
			JkCompanyInfo jkCompanyInfo = jkCompanyInfoService.cruxBalace(company.getId().toString());
			Payv2BussCompany company2 = new Payv2BussCompany();
			company2.setId(company.getId());
			company2 = payv2BussCompanyService.selectSingle(company2);
			resultMap.put("jkCompanyInfo", jkCompanyInfo);
			resultMap.put("company", company2);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			logger.error("获取接口商户账户余额关键数据错误", e);
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, "获取数据失败！");
		}
		return resultMap;
	}
	/**
	 * 发送邮件
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("sendEmail")
	public Map<String,Object> sendEmail(){
		Map<String, Object> resultMap = new HashMap<>();
		Payv2BussCompany company = getAdmin();
		Payv2BussCompany company2 = new Payv2BussCompany();
		company2.setId(company.getId());
		company2 = payv2BussCompanyService.selectSingle(company2);
		try {
			String email = company2.getContactsMail();
			if(!ObjectUtil.isEmpty(email)){
				SslSmtpMailUtil ms = new SslSmtpMailUtil();
		        ms.setSubject("重置秘钥通知");
		        
		        StringBuffer sb = new StringBuffer();
		        String code = RandomUtil.generateString(6);
		        sb.append("重置秘钥验证码为"+code+"，此验证码10分钟有效，秘钥涉及重要支付信息请妥善保管。");
		        RedisDBUtil.redisDao.setString(email+company2.getUserName().toString(), code,600);
		        ms.setText(sb.toString());
		        ms.setFrom(ReadProChange.getValue("suport_mail"));
		        String [] mailFroms = email.split("-");
		        ms.setRecipients(mailFroms, "TO");
		        ms.setSentDate();
		        ms.sendMail();
		        resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
			}else{
				resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null,"邮箱不存在，请联系商务！");
			}
		} catch (Exception e) {
			logger.error(" 获取重置秘钥邮箱验证码错误", e);
			resultMap = ReMessage.resultBack(ParameterEunm.SHOP_ERROR, null);
		}		
		return resultMap;
	}
	/**
	 * 验证验证码、生成并更新商户秘钥
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("checkEmailCode")
	public Map<String,Object> checkEmailCode(@RequestParam Map<String, Object> map, HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<>();
		Payv2BussCompany company = getAdmin();
		Payv2BussCompany company2 = new Payv2BussCompany();
		company2.setId(company.getId());
		company2 = payv2BussCompanyService.selectSingle(company2);
		if (ObjectUtil.checkObject(new String[] {"code"}, map)) {			
			try {
				String email = company2.getContactsMail();
				if(!ObjectUtil.isEmpty(email)){
					String code = RedisDBUtil.redisDao.getString(email+company2.getUserName().toString());
					if(!ObjectUtil.isEmpty(code)){
						if(code.toUpperCase().equals(map.get("code").toString().toUpperCase())){
							// 生成并更新新的密钥
							String newcompanySecret = IdUtils.createRandomString(32);
							company2.setCompanySecret(newcompanySecret);
							payv2BussCompanyService.update(company2);
							resultMap.put("newcompanySecret", newcompanySecret);
	    					RedisDBUtil.redisDao.delete(email+company2.getUserName().toString());
							resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
						}else{
							resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null,"验证码错误!");
						}
					}else{
						resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null,"验证码不存在或超时");
					}			       
				}else{
					resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null,"邮箱不存在，请联系商务！");
				}
			} catch (Exception e) {
				logger.error(" 更新接口商户秘钥错误！", e);
				resultMap = ReMessage.resultBack(ParameterEunm.SHOP_ERROR, null);
			}
		}else{
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null,"缺少参数");
		}
		return resultMap;
	}
}
