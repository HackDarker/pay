package com.pay.business.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;


/**
 * 验证金额是否正确两位小数
 * @author Administrator
 *
 */
public class DecimalUtil {
	public static boolean isNumber(String str) {  
        Pattern pattern = Pattern.compile("[0-9]*");  
        Matcher match = pattern.matcher(str.trim());  
        return match.matches();  
    }  
  
    public static boolean isBigDecimal(String str) {  
        Matcher match =null;  
        if(isNumber(str)==true){  
            Pattern pattern = Pattern.compile("[0-9]*");  
            match = pattern.matcher(str.trim());  
        }else{  
            if(str.trim().indexOf(".")==-1){  
                Pattern pattern = Pattern.compile("^[+-]?[0-9]*");  
                match = pattern.matcher(str.trim());  
            }else{  
                Pattern pattern = Pattern.compile("^[+-]?[0-9]+(\\.\\d{1,2}){1}");  
                match = pattern.matcher(str.trim());                  
            }  
        }  
        return match.matches();  
    }
    
    /**
	 * 优化金额(将以元为单位,补两个0)
	 * @param amount 
	 * @return
	 */
	public synchronized static String optimisedAmount(String amount){
		int indexOf = StringUtils.indexOf(amount, ".");
		if(indexOf==-1){
			//没有小数点,加上小数点
			amount = amount + ".00";
		}else{
			//看小数点后还有几位 不足补零,超出去掉\
			int s = amount.length()-(indexOf+1);
			if(s==1){
				amount = amount + "0";
			}else if(s>2){
				amount = amount.substring(0, indexOf+3);
			}
		}
		return amount;
	}
	
	/**
	 * 元转分
	 * @param amount
	 * @return
	 * @throws Exception 
	 */
	public static String yuanToCents(String amount) throws Exception{
		if(checkAmout(amount)){
			String optimisedAmount = optimisedAmount(amount);
			
			double centos = NumberUtils.createDouble(optimisedAmount)*100;
			
			BigDecimal b = new BigDecimal(centos);
			int f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			
			return String.valueOf(f1);
		}else{
			return null;
		}
	}
	
	/**
	 * 分转元 保留两位小数
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static String centsToYuan(String amount) throws Exception{
		if(checkAmout(amount)){
			double yuan = NumberUtils.createDouble(amount)/100;
			return optimisedAmount(String.valueOf(yuan));
		}else{
			return null;
		}
	}
	
	public static final String MONEY_REGEX = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"; //"\\-?[0-9]+";
	
	/**
	 * 判断是否为金额
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static boolean checkAmout(String amount) throws Exception{
		Pattern pattern = Pattern.compile(MONEY_REGEX);
		Matcher matcher = pattern.matcher(amount);
		return matcher.matches();
	}
	
	/**
	 * 移除double小数点后面的值(包括小数点)
	 * @param amount
	 * @return
	 */
	public static String removeZero(Double amount){
		if(amount!=null){
			String tempAmount = amount.toString();
			int indexOf = StringUtils.indexOf(tempAmount, ".");
			if(indexOf>0){
				return StringUtils.substring(tempAmount, 0,indexOf);
			}else{
				return tempAmount;
			}
		}else{
			return null;
		}
	}
	
	/**
	 * 向上取整
	 * 
	 * @param d
	 * @param n
	 *            保留位数
	 * @return
	 */
	public static double getCeil(double d, int n) {
		BigDecimal b = new BigDecimal(String.valueOf(d));
		b = b.divide(BigDecimal.ONE, n, BigDecimal.ROUND_CEILING);
		return b.doubleValue();
	}
	
	public static boolean isZero(String str){
		if(Double.valueOf(str)==0){
			return false;
		}
		return true;
	}
	
	/**
	 * 两个dubbo相减，保留两位小数
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static double sub(double value1, double value2) {
		BigDecimal b1 = new BigDecimal(Double.valueOf(value1));		
		BigDecimal b2 = new BigDecimal(Double.valueOf(value2));		
		return b1.subtract(b2).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();		
	}
	
	/**
	 * 两个dubbo相加，保留两位小数
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static double add(double value1, double value2){        
		// 进行加法运算
		 BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
		 BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
		 return b1.add(b2).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
	/**
	* 提供精确乘法运算的mul方法
	* @param value1 被乘数
	* @param value2 乘数
	* @return 两个参数的积
	*/
	public static double mul(double d1,double d2){  
        BigDecimal b1=new BigDecimal(Double.toString(d1));  
        BigDecimal b2=new BigDecimal(Double.toString(d2));  
        return b1.multiply(b2).doubleValue();  
          
    }  
	/**
	* 提供精确的除法运算方法div
    * @param value1 被除数
	* @param value2 除数
	* @param scale 精确范围
	* @return 两个参数的商
	* @throws IllegalAccessException
	*/
    public static double div(double d1,double d2,int scale){  
        if(scale<0){  
            throw new IllegalArgumentException("The scale must be a positive integer or zero");  
        }  
        BigDecimal b1=new BigDecimal(Double.toString(d1));  
        BigDecimal b2=new BigDecimal(Double.toString(d2));  
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();  
          
    }
    /**
	 * 两个dubbo相加，保留自定义位数
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static double add(double value1, double value2,int scale){        
		// 进行加法运算
		 BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
		 BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
		 return b1.add(b2).setScale(scale,BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
    
	public static void main(String[] args) throws Exception {
		System.out.println(getCeil(div(mul(8.8, 25), 1000, 10),2));
		System.out.println(add(0.005,0.01,3));
	}
}
