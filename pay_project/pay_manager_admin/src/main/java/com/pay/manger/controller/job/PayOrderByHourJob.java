package com.pay.manger.controller.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pay.business.record.service.PayDataRecordService;
/**
* @Title: payOrderByHourJob.java 
* @Package com.pay.manger.controller.job 
* @Description: 每小时去统计订单数据
* @author ZHOULIBO   
* @date 2017年8月1日 下午4:36:09 
* @version V1.0
*/
@Component
@Controller
@RequestMapping("/PayOrderByHourJob")
public class PayOrderByHourJob {
	@Autowired
	private PayDataRecordService payDataRecordService;
	
	
	/**
	 * startUp 
	 * 每小时统计：今天统计昨天的订单；按每小时存上：触发时间每天：23:30
	 * void    返回类型
	 * @throws ParseException 
	 */
	@RequestMapping("/startUp")
	public void startUp() {
		System.out.println("=========》按每小时（昨日）统计开始《==========");
		System.out.println("按每小时（昨日）统计：统计时间为："+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("payOrderByHourJob-startUp").build();
		ExecutorService cachedThreadPool =new ThreadPoolExecutor(10, 200, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024),namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
		final Date date = new Date();
		for (int i = 0; i <=23; i++) {
			final int index = i;
			cachedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try{
						String h="0";
						if(index<=9){
							h=h+String.valueOf(index)+"";
						}else{
							h=index+"";
						}
						// 开始时间
						String timeBegin = timeBegin(date,h);
						// 结束时间
						String timeEnd = timeEnd(date,h);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("timeBegin", timeBegin);
						map.put("timeEnd", timeEnd);
						payDataRecordService.setStatisticsOrderByHour(map);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}
	}
	/**
	 * getHour 
	 * 获取开始时间
	 * @param date
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String  timeBegin(Date date,String h){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//date 换成已经已知的Date对象
        cal.add(Calendar.DATE, -1);// 前一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd "+h+":00:00");
//        System.out.println("按每小时（昨日）统计：区域开始时间："+format.format(cal.getTime()));
        String hour=format.format(cal.getTime());
		return hour;
	}
	public static String  timeEnd(Date date,String h){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//date 换成已经已知的Date对象
        cal.add(Calendar.DATE, -1);// 前一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd "+h+":59:59");
//        System.out.println("按每小时（昨日）统计：区域结束时间："+format.format(cal.getTime()));
        String hour=format.format(cal.getTime());
		return hour;
	}
	public static void main(String[] args) {
		Date date = new Date();
//		System.out.println(timeBegin);
		for (int i = 0; i <=23; i++) {
			String h="0";
			if(i<=9){
				h=h+String.valueOf(i)+"";
			}else{
				h=i+"";
			}
			// 开始时间
			String timeBegin = timeBegin(date,h);
			// 结束时间
			String timeEnd = timeEnd(date,h);
		}
	}
	
	
}
