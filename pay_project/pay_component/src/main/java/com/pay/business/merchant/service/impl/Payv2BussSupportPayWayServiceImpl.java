package com.pay.business.merchant.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.cache.annotations.Cacheable;
import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.ReadProChange;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.redis.RedisDBUtil;
import com.pay.business.merchant.entity.Payv2BussSupportPayWay;
import com.pay.business.merchant.entity.Payv2CompanyPayType;
import com.pay.business.merchant.mapper.Payv2BussSupportPayWayMapper;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.merchant.service.Payv2CompanyPayTypeService;
import com.pay.business.record.mapper.Payv2CompanyQuotaMapper;

/**
 * @author cyl
 * @version
 */
@Service("payv2BussSupportPayWayService")
public class Payv2BussSupportPayWayServiceImpl extends BaseServiceImpl<Payv2BussSupportPayWay, Payv2BussSupportPayWayMapper> implements
		Payv2BussSupportPayWayService {

	private static Random r = new Random();

	// 注入当前dao对象
	@Autowired
	private Payv2BussSupportPayWayMapper payv2BussSupportPayWayMapper;
	@Autowired
	private Payv2CompanyQuotaMapper payv2CompanyQuotaMapper;
	
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	
	@Autowired
	private Payv2CompanyPayTypeService payv2CompanyPayTypeService;

	public Payv2BussSupportPayWayServiceImpl() {
		setMapperClass(Payv2BussSupportPayWayMapper.class, Payv2BussSupportPayWay.class);
	}

	public Payv2BussSupportPayWay selectSingle(Payv2BussSupportPayWay t) {
		return payv2BussSupportPayWayMapper.selectSingle(t);
	}

	public List<Payv2BussSupportPayWay> selectByObject(Payv2BussSupportPayWay t) {
		return payv2BussSupportPayWayMapper.selectByObject(t);
	}

	public List<Payv2BussSupportPayWay> selectByObjectForCompany(Payv2BussSupportPayWay t) {
		return payv2BussSupportPayWayMapper.selectByObjectForCompany(t);
	}

	public List<Map<String, Object>> queryPayWayIdAndNameByCompanyId(Long companyId) {
		return payv2BussSupportPayWayMapper.queryPayWayIdAndNameByCompanyId(companyId);
	}

	public List<Payv2BussSupportPayWay> selectPayWayIdByGroup(Payv2BussSupportPayWay t) {
		return payv2BussSupportPayWayMapper.selectPayWayIdByGroup(t);
	}

	/**
	 * 查询对象（缓存）
	 * 
	 * @param t
	 * @return
	 */
	@Cacheable(region = "payv2BussSupportPayWayService", namespace = "selectSingle1", fieldsKey = { "#t.parentId", "#t.payWayId", "#t.rateId", "#t.isDelete" }, expire = 300)
	public Payv2BussSupportPayWay selectSingle1(Payv2BussSupportPayWay t) {
		return payv2BussSupportPayWayMapper.selectSingle(t);
	}

	/**
	 * 根据支付类型和商户查询商户支持支付通道
	 * 
	 * @param companyId
	 * @param payViewType
	 * @param payType
	 * @return
	 */
	//@Cacheable(region = "payv2BussSupportPayWayService", namespace = "queryByCompany", fieldsKey = { "#companyId", "#payViewType", "#dictName","#payMoney" }, expire = 300)
	public List<Payv2BussSupportPayWay> queryByCompany(Long companyId, Integer payViewType, String dictName,Double payMoney) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("companyId", companyId);
		paramMap.put("payViewType", payViewType);
		paramMap.put("dictName", dictName);
		paramMap.put("payWayStatus", 1);
		paramMap.put("isDelete", 2);
		paramMap.put("status", 1);
		if(payMoney!=null){
			paramMap.put("payMoney", payMoney);
		}
		List<Payv2BussSupportPayWay> list = payv2BussSupportPayWayMapper.queryByCompany(paramMap);
		if (list != null && list.size() == 0) {
			return null;
		}
		return list;

	}

	/**
	 * 根据支付方式和商户查询商户支持支付通道
	 * 
	 * @param companyId
	 * @param payViewType
	 * @param payType
	 * @return
	 */
	//@Cacheable(region = "payv2BussSupportPayWayService", namespace = "queryByCompany1", fieldsKey = { "#companyId", "#payWayId", "#payType","#payMoney" }, expire = 300)
	public List<Payv2BussSupportPayWay> queryByCompany(Long companyId, Long payWayId, Integer payType,Double payMoney) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("companyId", companyId);
		paramMap.put("payWayId", payWayId);
		paramMap.put("payType", payType);
		paramMap.put("payWayStatus", 1);
		paramMap.put("isDelete", 2);
		paramMap.put("status", 1);
		paramMap.put("payMoney", payMoney);
		List<Payv2BussSupportPayWay> list = payv2BussSupportPayWayMapper.queryByCompany(paramMap);
		if (list != null && list.size() == 0) {
			return null;
		}
		return list;

	}

	/**
	 * 获取支付方式、通道
	 * 
	 * @param payWayList
	 * @param payMoney
	 * @param list
	 * @return
	 */
	public Payv2BussSupportPayWay getWayRate(List<Payv2BussSupportPayWay> payWayList, Double payMoney
			, Long companyId, Integer payViewType,Integer isRandom,String dictName) {
		Payv2BussSupportPayWay pbspw = null;
		System.out.println("通道个数：" + payWayList.size());
		// 路由按排序
		if (isRandom == 2) {
			System.out.println("#######通道模式：排序");
			for (Payv2BussSupportPayWay way : payWayList) {
				String rate_disabled_id="rate_disabled_"+way.getRateId();
				//是否通道关闭
				String yes=RedisDBUtil.redisDao.getString(rate_disabled_id);
				if(yes!=null&&!("").equals(yes)){
					// 置灰
					continue;
				}
				// 1.过滤单笔订单金额大于单笔金额或者当天金额大于商户支付通道限额当天金额 (设置商户的单笔限额)
				Double oneMaxMoney = way.getOneMaxMoney() == null ? 0 : way.getOneMaxMoney();
				// 官方通道设置的总限额
				Double rateOneMaxMoney = way.getRateOneMaxMoney() == null ? 0 : way.getRateOneMaxMoney();

				System.out.println("通道id:" + way.getRateId() + "######支付金额：" + payMoney + "####商户单笔限额：" + oneMaxMoney + "!######官方单笔限额：" + rateOneMaxMoney);

				// 超过单笔限额(总通道)
				if (rateOneMaxMoney > 0 && payMoney > rateOneMaxMoney) {
					// 置灰
					continue;
				}
				// 超过单笔限额
				if (oneMaxMoney > 0 && payMoney > oneMaxMoney) {
					// 置灰
					continue;
				}
				boolean con = false;

				Date as = new Date();
				String time = DateUtil.DateToString(as, "yyyyMMdd");
				// 先去缓存找是否存在缓存
				String redisKey = way.getRateId()+"RID"+ time;
				Map<String, String> map = RedisDBUtil.redisDao.getStringMapAll(redisKey);
				Double totalMoney = 0.00;
				if (map != null && map.containsKey("totalMoney")) {
					// 获取当天支付总金额
					totalMoney = Double.valueOf(map.get("totalMoney"));

				} else {
					// 没有存入缓存
					map.put("totalMoney", "0");
					RedisDBUtil.redisDao.hmset(redisKey, map);
					// 设置失效时间
					RedisDBUtil.redisDao.expire(redisKey, 87000);
				}
				// 通道当天总额度（官方）
				Double rateMaxMoney = way.getRateMaxMoney() == null ? 0 : way.getRateMaxMoney();

				System.out.println("当前此通道总金额" + totalMoney + "##########通道id:" + way.getRateId() + "!######官方每日限额：" + rateMaxMoney);
				// 超过通道当天总额度（官方）
				if (rateMaxMoney > 0 && totalMoney + payMoney > rateMaxMoney) {
					con = true;
				}
				if (!con) {
					String companytime = DateUtil.DateToString(as, "yyyyMMdd");
					// 先去缓存找是否存在缓存
					String companyredisKey = companyId+"CID"+ way.getRateId()+"RID"+ companytime;
					Map<String, String> companyMap = RedisDBUtil.redisDao.getStringMapAll(companyredisKey);
					Double companyTotalMoney = 0.00;
					if (companyMap != null && companyMap.containsKey("companyTotalMoney")) {
						// 获取当天支付总金额
						companyTotalMoney = Double.valueOf(companyMap.get("companyTotalMoney"));

					} else {
						// 没有存入缓存
						companyMap.put("companyTotalMoney", "0");
						RedisDBUtil.redisDao.hmset(companyredisKey, companyMap);
						// 设置失效时间
						RedisDBUtil.redisDao.expire(companyredisKey, 87000);
					}
					// 当天限额
					Double maxMoney = way.getMaxMoney() == null ? 0 : way.getMaxMoney();
					System.out.println("当前商户此通道总金额" + companyTotalMoney + "##########通道id:" + way.getRateId() + "!######商户每日限额：" + maxMoney);
					// 超过当天限额
					if (maxMoney > 0 && companyTotalMoney + payMoney > maxMoney) {
						con = true;
					}
				}
				if (!con) {
					pbspw = way;
					break;
				}
			}
		} else if (isRandom == 1) {// 随机路由
			System.out.println("#######通道模式：随机");
			forway: for (int i = payWayList.size() - 1; i >= 0; i--) {
				Payv2BussSupportPayWay way = payWayList.get(i);
				String rate_disabled_id="rate_disabled_"+way.getRateId();
				//是否通道关闭
				String yes=RedisDBUtil.redisDao.getString(rate_disabled_id);
				if(yes!=null&&!"".equals(yes)){
					// 置灰
					continue;
				}
				// 1.过滤单笔订单金额大于单笔金额或者当天金额大于商户支付通道限额当天金额 (设置商户的单笔限额)
				Double oneMaxMoney = way.getOneMaxMoney() == null ? 0 : way.getOneMaxMoney();
				// 官方通道设置的总限额
				Double rateOneMaxMoney = way.getRateOneMaxMoney() == null ? 0 : way.getRateOneMaxMoney();

				System.out.println("通道id:" + way.getRateId() + "######支付金额：" + payMoney + "####商户单笔限额：" + oneMaxMoney + "!######官方单笔限额：" + rateOneMaxMoney);

				// 超过单笔限额(总通道)
				if (rateOneMaxMoney > 0 && payMoney > rateOneMaxMoney) {
					// 置灰
					payWayList.remove(i);
					continue;
				}
				// 超过单笔限额
				if (oneMaxMoney > 0 && payMoney > oneMaxMoney) {
					// 置灰
					payWayList.remove(i);
					continue;
				}
				boolean con = false;

				Date as = new Date();
				String time = DateUtil.DateToString(as, "yyyyMMdd");
				// 先去缓存找是否存在缓存
				String redisKey = way.getRateId()+"RID"+ time;
				Map<String, String> map = RedisDBUtil.redisDao.getStringMapAll(redisKey);
				Double totalMoney = 0.00;
				if (map != null && map.containsKey("totalMoney")) {
					// 获取当天支付总金额
					totalMoney = Double.valueOf(map.get("totalMoney"));
				} else {
					// 没有存入缓存
					map.put("totalMoney", "0");
					RedisDBUtil.redisDao.hmset(redisKey, map);
					// 设置失效时间
					RedisDBUtil.redisDao.expire(redisKey, 87000);
				}
				// 通道当天总额度（官方）
				Double rateMaxMoney = way.getRateMaxMoney() == null ? 0 : way.getRateMaxMoney();

				System.out.println("当前此通道总金额" + totalMoney + "##########通道id:" + way.getRateId() + "!######官方每日限额：" + rateMaxMoney);

				// 超过通道当天总额度（官方）
				if (rateMaxMoney > 0 && totalMoney + payMoney > rateMaxMoney) {
					payWayList.remove(i);
					continue forway;
				}
				if (!con) {
					String companytime = DateUtil.DateToString(as, "yyyyMMdd");
					// 先去缓存找是否存在缓存
					String companyredisKey = companyId+"CID"+ way.getRateId()+"RID"+ companytime;
					Map<String, String> companyMap = RedisDBUtil.redisDao.getStringMapAll(companyredisKey);
					Double companyTotalMoney = 0.00;
					if (companyMap != null && companyMap.containsKey("companyTotalMoney")) {
						// 获取当天支付总金额
						companyTotalMoney = Double.valueOf(companyMap.get("companyTotalMoney"));

					} else {
						// 没有存入缓存
						companyMap.put("companyTotalMoney", "0");
						RedisDBUtil.redisDao.hmset(companyredisKey, companyMap);
						// 设置失效时间
						RedisDBUtil.redisDao.expire(companyredisKey, 87000);
					}
					// 当天限额
					Double maxMoney = way.getMaxMoney() == null ? 0 : way.getMaxMoney();

					System.out.println("当前商户此通道总金额" + companyTotalMoney + "##########通道id:" + way.getRateId() + "!######商户每日限额：" + maxMoney);

					// 超过当天限额
					if (maxMoney > 0 && companyTotalMoney + payMoney > maxMoney) {
						payWayList.remove(i);
						continue forway;
					}
				}
			}
			if (payWayList.size() > 0) {
				int rInt = r.nextInt(payWayList.size());
				pbspw = payWayList.get(rInt);
			}

		//循环
		}else{
			System.out.println("#######通道模式：循环");
			
			double min = 0 ;
			double max = payWayList.get(0).getOneMaxMoney();
			//取最小值的最小，最大值的最大
			for (Payv2BussSupportPayWay way : payWayList) {
				if(way.getOneMinMoney()>min){
					min = way.getOneMinMoney();
				}
				if(way.getOneMaxMoney()<max){
					max = way.getOneMaxMoney();
				}else{
					if(max==0){
						max = way.getOneMaxMoney();
					}
				}
			}
			//金额纬度
			String moneyCache = min +"-"+max;
			//支付平台+商户id+金额+支付方式纬度缓存
			String key = payViewType+"#"+ReadProChange.getValue("last_rate_cache")+"#"+companyId+"#"
					+moneyCache+"#"+(dictName==null?0:dictName);
			System.out.println("上一次缓存的支付通道key："+key);
			String rate = RedisDBUtil.redisDao.getString(key);
			System.out.println("###########上一次缓存的通道id:"+rate);
			//排序后的通道集合
			List<Payv2BussSupportPayWay> payWayLists = new ArrayList<>();
			if(rate!=null&&!"".equals(rate)){
				Long rateId = Long.valueOf(rate);
				//上一次支付的通道
				Payv2BussSupportPayWay last_rate_cache = null;
				//记录上一次支付的通道后面的要排序的序号
				Integer index = null;
				for (Payv2BussSupportPayWay way : payWayList) {
					if(way.getRateId().longValue()==rateId.longValue()){
						last_rate_cache = way;
						index = 0;
						continue;
					}
					if(last_rate_cache==null){
						payWayLists.add(way);
					}else{
						if(index!=null){
							payWayLists.add(index, way);
							index++;
						}
					}
				}
				if(last_rate_cache!=null){
					//把上一次支付的通道放最后，如果前面的通道都超过限额，就可以取上一次支付的通道
					payWayLists.add(last_rate_cache);
				}
			}else{
				payWayLists.addAll(payWayList);
			}
			
			for (Payv2BussSupportPayWay way : payWayLists) {
				String rate_disabled_id="rate_disabled_"+way.getRateId();
				//是否通道关闭
				String yes=RedisDBUtil.redisDao.getString(rate_disabled_id);
				if(yes!=null&&!("").equals(yes)){
					// 置灰
					continue;
				}
				// 1.过滤单笔订单金额大于单笔金额或者当天金额大于商户支付通道限额当天金额 (设置商户的单笔限额)
				Double oneMaxMoney = way.getOneMaxMoney() == null ? 0 : way.getOneMaxMoney();
				// 官方通道设置的总限额
				Double rateOneMaxMoney = way.getRateOneMaxMoney() == null ? 0 : way.getRateOneMaxMoney();

				System.out.println("通道id:" + way.getRateId() + "######支付金额：" + payMoney + "####商户单笔限额：" + oneMaxMoney + "!######官方单笔限额：" + rateOneMaxMoney);

				// 超过单笔限额(总通道)
				if (rateOneMaxMoney > 0 && payMoney > rateOneMaxMoney) {
					// 置灰
					continue;
				}
				// 超过单笔限额
				if (oneMaxMoney > 0 && payMoney > oneMaxMoney) {
					// 置灰
					continue;
				}
				boolean con = false;

				Date as = new Date();
				String time = DateUtil.DateToString(as, "yyyyMMdd");
				// 先去缓存找是否存在缓存
				String redisKey = way.getRateId()+"RID"+ time;
				Map<String, String> map = RedisDBUtil.redisDao.getStringMapAll(redisKey);
				Double totalMoney = 0.00;
				if (map != null && map.containsKey("totalMoney")) {
					// 获取当天支付总金额
					totalMoney = Double.valueOf(map.get("totalMoney"));

				} else {
					// 没有存入缓存
					map.put("totalMoney", "0");
					RedisDBUtil.redisDao.hmset(redisKey, map);
					// 设置失效时间
					RedisDBUtil.redisDao.expire(redisKey, 87000);
				}
				// 通道当天总额度（官方）
				Double rateMaxMoney = way.getRateMaxMoney() == null ? 0 : way.getRateMaxMoney();

				System.out.println("当前此通道总金额" + totalMoney + "##########通道id:" + way.getRateId() + "!######官方每日限额：" + rateMaxMoney);
				// 超过通道当天总额度（官方）
				if (rateMaxMoney > 0 && totalMoney + payMoney > rateMaxMoney) {
					con = true;
				}
				if (!con) {
					String companytime = DateUtil.DateToString(as, "yyyyMMdd");
					// 先去缓存找是否存在缓存
					String companyredisKey = companyId+"CID"+ way.getRateId()+"RID"+ companytime;
					Map<String, String> companyMap = RedisDBUtil.redisDao.getStringMapAll(companyredisKey);
					Double companyTotalMoney = 0.00;
					if (companyMap != null && companyMap.containsKey("companyTotalMoney")) {
						// 获取当天支付总金额
						companyTotalMoney = Double.valueOf(companyMap.get("companyTotalMoney"));

					} else {
						// 没有存入缓存
						companyMap.put("companyTotalMoney", "0");
						RedisDBUtil.redisDao.hmset(companyredisKey, companyMap);
						// 设置失效时间
						RedisDBUtil.redisDao.expire(companyredisKey, 87000);
					}
					// 当天限额
					Double maxMoney = way.getMaxMoney() == null ? 0 : way.getMaxMoney();
					System.out.println("当前商户此通道总金额" + companyTotalMoney + "##########通道id:" + way.getRateId() + "!######商户每日限额：" + maxMoney);
					// 超过当天限额
					if (maxMoney > 0 && companyTotalMoney + payMoney > maxMoney) {
						con = true;
					}
				}
				if (!con) {
					pbspw = way;
					System.out.println("###################支付平台："+payViewType+";支付通道："+way.getRateName());
					RedisDBUtil.redisDao.setString(key,""+way.getRateId());
					break;
				}
			}
		}
		return pbspw;
	}

	
	public List<Payv2BussSupportPayWay> queryByCompany(Map<String, Object> map) {
		return payv2BussSupportPayWayMapper.queryByCompany2(map);
	}

	public void configCompanyWayRate(Map<String, Object> map) throws Exception {
		Map<String,Object> param = new HashMap<>();
		param.put("parentId", map.get("id"));
		// 先删除所有支付通道，再添加
		this.delete(param);
		String payWayList = map.get("payWayList").toString();
		// 解析
		String a[] = payWayList.split(",");
		if(!payWayList.equals("") && a.length > 0) {
			for (String string : a) {
				String b[] = string.split("-");
				// 支付平台ID
				Long wayId = Long.valueOf(b[0]);
				// 支付类型ID
				int typeId = Integer.valueOf(b[1]);
				// 通道ID
				Long rateId = Long.valueOf(b[2]);
				// 通道费
				Double payWayRate = Double.valueOf(b[3]);
				// 排序
				int sortNum = Integer.valueOf(b[4]);
				// 限额
				double maxMoney = Double.valueOf(b[5]);
				// 单笔限额（最小值）
				double oneMinMoney = Double.valueOf(b[6]);
				//每笔限额（最大值）
				double oneMaxMoney = Double.valueOf(b[7]);
				
	
				Payv2BussSupportPayWay payv2BussSupportPayWay = new Payv2BussSupportPayWay();
				payv2BussSupportPayWay.setIsDelete(2);
				payv2BussSupportPayWay.setParentId(Long.parseLong(map.get("id").toString()));
				payv2BussSupportPayWay.setPayWayId(wayId);
				payv2BussSupportPayWay.setPayType(typeId);
				payv2BussSupportPayWay.setRateId(rateId);
				payv2BussSupportPayWay.setSortNum(sortNum);
				payv2BussSupportPayWay.setPayWayRate(payWayRate);
				payv2BussSupportPayWay.setPayWayStatus(1);
				payv2BussSupportPayWay.setCreateTime(new Date());
				payv2BussSupportPayWay.setMaxMoney(maxMoney);
				payv2BussSupportPayWay.setOneMinMoney(oneMinMoney);
				payv2BussSupportPayWay.setOneMaxMoney(oneMaxMoney);
				this.add(payv2BussSupportPayWay);
			}
		}
		payv2BussCompanyService.update(map);
	}
	
	public String batchGiveRateToCompany(Map<String, Object> map) throws Exception {
		int exits = 0;
		int notSupport = 0;
		Long companyId = Long.parseLong(map.get("companyId").toString());
		// 解析
		List<String> rateIds = Arrays.asList(map.get("gids").toString().split(","));
		List<String> typeIds = Arrays.asList(map.get("tids").toString().split(","));
		List<String> wayIds = Arrays.asList(map.get("wids").toString().split(","));
		Map<String, Double> rateMap = new HashMap<String, Double>();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentId", companyId);
		Integer maxSortNum = this.selectMaxSortNumByCompany(paramMap);
		if(maxSortNum == null)
			maxSortNum = 0;
		int sortNum = maxSortNum.intValue();
		for (int i = 0; i < rateIds.size(); i++) {
			// 支付平台ID
			Long wayId = Long.valueOf(wayIds.get(i));
			// 支付类型ID
			int typeId = Integer.valueOf(typeIds.get(i));
			// 通道ID
			Long rateId = Long.valueOf(rateIds.get(i));
			
			paramMap = new HashMap<String, Object>();
			paramMap.put("parentId", companyId);
			paramMap.put("rateId", rateId);
			Payv2BussSupportPayWay w = this.detail(paramMap);
			if(w != null) {
				exits++;
				continue;
			}
			// 通道费率
			Double payWayRate = 0.0;
			if(rateMap.containsKey(typeIds.get(i))) {
				payWayRate = rateMap.get(typeIds.get(i));
			} else {
				paramMap = new HashMap<String, Object>();
				paramMap.put("companyId", companyId);
				paramMap.put("payTypeId", typeId);
				Payv2CompanyPayType t = payv2CompanyPayTypeService.detail(paramMap);
				if(t == null) {
					notSupport++;
					continue;
				} else {
					payWayRate = t.getPayWayRate();
					rateMap.put("typeIds.get(i)", payWayRate);
				}
			}
			// 排序
			sortNum++;
			// 限额
			double maxMoney = 200000;
			// 单笔限额（最小值）
			double oneMinMoney = 0;
			//每笔限额（最大值）
			double oneMaxMoney = 0;

			Payv2BussSupportPayWay payv2BussSupportPayWay = new Payv2BussSupportPayWay();
			payv2BussSupportPayWay.setIsDelete(2);
			payv2BussSupportPayWay.setParentId(companyId);
			payv2BussSupportPayWay.setPayWayId(wayId);
			payv2BussSupportPayWay.setPayType(typeId);
			payv2BussSupportPayWay.setRateId(rateId);
			payv2BussSupportPayWay.setSortNum(sortNum);
			payv2BussSupportPayWay.setPayWayRate(payWayRate);
			payv2BussSupportPayWay.setPayWayStatus(1);
			payv2BussSupportPayWay.setCreateTime(new Date());
			payv2BussSupportPayWay.setMaxMoney(maxMoney);
			payv2BussSupportPayWay.setOneMinMoney(oneMinMoney);
			payv2BussSupportPayWay.setOneMaxMoney(oneMaxMoney);
			this.add(payv2BussSupportPayWay);
		}
		return exits+","+notSupport;
	}

	public Integer selectMaxSortNumByCompany(Map<String, Object> paramMap) {
		return payv2BussSupportPayWayMapper.selectMaxSortNumByCompany(paramMap);
	}

	
	public void updatePayWayRate(Map<String, Object> map) {
		payv2BussSupportPayWayMapper.updatePayWayRate(map);
	}
	
	/**
	 * 关闭通道-批量删除商户的通道
	 * @param ids
	 */
	public void batchDelete(String ids){
		payv2BussSupportPayWayMapper.batchDelete(ids);
	}
}
