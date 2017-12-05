package com.pay.channel.controller.payv2;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.entity.Payv2ChannelWay;
import com.pay.business.merchant.mapper.Payv2ChannelMapper;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.merchant.service.Payv2ChannelWayService;
import com.pay.channel.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/Payv2ChannelInfo/*")
public class Payv2ChannelInfoController extends BaseManagerController<Payv2Channel, Payv2ChannelMapper> {	
	@Autowired
	private Payv2ChannelService payv2ChannelService;
	@Autowired
	private Payv2ChannelWayService payv2ChannelWayService;
	/**
	 * 渠道商信息加载页面
	 * 
	 * @param map
	 * @return ModelAndView
	 */	
	@RequestMapping("alldata")
	public ModelAndView showDetailDay(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/moneyManage/payv2ChannelInfo");
		// 查询渠道商的基本信息
		Payv2Channel payv2Channel = new Payv2Channel();
		payv2Channel.setId(getAdmin().getId());
		payv2Channel = payv2ChannelService.selectSingle(payv2Channel);
		// 查询渠道商支持的支付方式
		List<Payv2ChannelWay> payTypeList = payv2ChannelWayService.payTypeList(String.valueOf(getAdmin().getId()));
		mv.addObject("payv2Channel", payv2Channel);
		mv.addObject("payTypeList", payTypeList);
		return mv;
	}
	
}
