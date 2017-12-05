package com.pay.business.jk.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.jk.entity.JkCompanyInfo;
import com.pay.business.jk.mapper.JkCompanyInfoMapper;
import com.pay.business.jk.service.JkCompanyInfoService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;

/**
 * @author cyl
 * @version 
 */
@Service("jkCompanyInfoService")
public class JkCompanyInfoServiceImpl extends BaseServiceImpl<JkCompanyInfo, JkCompanyInfoMapper> implements JkCompanyInfoService {
	// 注入当前dao对象
    @Autowired
    private JkCompanyInfoMapper jkCompanyInfoMapper;

    public JkCompanyInfoServiceImpl() {
        setMapperClass(JkCompanyInfoMapper.class, JkCompanyInfo.class);
    }
    
    /**
	 * 检查商户和余额是否可用
	 * @param map
	 * @return
	 */
	public JkCompanyInfo companyCheck(String mchNum){
		Map<String,Object> map=new HashMap<>();
		map.put("companyKey", mchNum);
		map.put("companyStatus", 2);
		map.put("isDelete", 2);
		map.put("status", 1);
		return jkCompanyInfoMapper.companyCheck(map);
	}

	public PageObject<JkCompanyInfo> getPageObject(Map<String, Object> map) {
		// 获取总数
		int totalData = jkCompanyInfoMapper.getCount2(map);
		// 获取当前页数据
		PageHelper helper = new PageHelper(totalData, map);
		List<JkCompanyInfo> JkInfoList = jkCompanyInfoMapper.JkInfoPage(helper.getMap());
		PageObject<JkCompanyInfo> pageObject = helper.getPageObject();
		pageObject.setDataList(JkInfoList);
		return pageObject;
	}

	public JkCompanyInfo cruxBalace(String companyId) {
		JkCompanyInfo jkCompanyInfo = jkCompanyInfoMapper.cruxBalace(companyId);
		if(jkCompanyInfo == null){
			jkCompanyInfo = new JkCompanyInfo();
			jkCompanyInfo.setBalance(0.00);
			jkCompanyInfo.setAddBalace(0.00);
			jkCompanyInfo.setServiceBalance("0.0000");
		}
		return jkCompanyInfo;
	}
}
