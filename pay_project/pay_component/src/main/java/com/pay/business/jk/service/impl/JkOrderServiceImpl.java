package com.pay.business.jk.service.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.ReadProChange;
import com.core.teamwork.base.util.http.HttpUtil;
import com.pay.business.jk.entity.JkCompanyInfo;
import com.pay.business.jk.entity.JkOrder;
import com.pay.business.jk.mapper.JkCompanyInfoMapper;
import com.pay.business.jk.mapper.JkOrderMapper;
import com.pay.business.jk.service.JkOrderService;

/**
 * @author cyl
 * @version 
 */
@Service("jkOrderService")
public class JkOrderServiceImpl extends BaseServiceImpl<JkOrder, JkOrderMapper> implements JkOrderService {
	// 注入当前dao对象
    @Autowired
    private JkOrderMapper jkOrderMapper;
    @Autowired
    private JkCompanyInfoMapper jkCompanyInfoMapper;

    public JkOrderServiceImpl() {
        setMapperClass(JkOrderMapper.class, JkOrder.class);
    }
    
    /**
	 * 下单接口
	 * @param map
	 * @param jci
	 * @param ip
	 * @return
	 */
	public String urlCon(Map<String,Object> map,JkCompanyInfo jci,String ip){
		String url = map.get("url").toString();
		String result = HttpUtil.HttpGet(ReadProChange.getValue("luoma_url") 
				+ URLEncoder.encode(url), null);
		JSONObject jsonObject = JSONObject.parseObject(result);
		String conUrl = jsonObject.getString("ticket_url");
		
//		String conUrl = "http://";
		
		JkOrder jkOrder = new JkOrder();
		jkOrder.setCompanyId(jci.getCompanyId());
		jkOrder.setUrl(URLDecoder.decode(url));
		jkOrder.setConUrl(conUrl);
		jkOrder.setCreateTime(new Date());
		jkOrder.setInfoId(jci.getId());
		jkOrder.setServiceBalance(jci.getServiceMoney());
		jkOrder.setIp(ip);
		jkOrderMapper.insertByEntity(jkOrder);
		
		jci.setLastCallIp(ip);
		jci.setLastCallTime(jkOrder.getCreateTime());
		jkCompanyInfoMapper.updateCallInfo(jci);
		
		return conUrl;
	}
}
