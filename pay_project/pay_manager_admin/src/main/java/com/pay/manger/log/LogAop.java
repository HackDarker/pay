package com.pay.manger.log;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pay.business.admin.entity.SysUcenterAdmin;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;

@Component  
@Aspect
public class LogAop {
	public LogAop() {  
		
    }  
	
	@Autowired
	private SysLogService sysLogService;
	 /** 
     * 在Spring 
     * 2.0中，Pointcut的定义包括两个部分：Pointcut表示式(expression)和Pointcut签名(signature 
     * )。让我们先看看execution表示式的格式： 
     * 括号中各个pattern分别表示修饰符匹配（modifier-pattern?）、返回值匹配（ret 
     * -type-pattern）、类路径匹配（declaring 
     * -type-pattern?）、方法名匹配（name-pattern）、参数匹配（(param 
     * -pattern)）、异常类型匹配（throws-pattern?），其中后面跟着“?”的是可选项。 
     *  
     * @param point 
     * @throws Throwable 
     */  
  
    @Pointcut("@annotation(com.pay.manger.log.MethodLog)")  
    public void methodCachePointcut() {  
    	
    }
  
 // // @Before("execution(* com.wssys.controller.*(..))")  
    // public void logAll(JoinPoint point) throws Throwable {  
    // System.out.println("打印========================");  
    // }  
    //  
    // // @After("execution(* com.wssys.controller.*(..))")  
    // public void after() {  
    // System.out.println("after");  
    // }  
  
    // 方法执行的前后调用  
    // @Around("execution(* com.wssys.controller.*(..))||execution(* com.bpm.*.web.account.*.*(..))")  
    // @Around("execution(* com.wssys.controller.*(..))")  
    // @Around("execution(* org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter.handle(..))")  
    @Around("methodCachePointcut()")  
    public Object around(ProceedingJoinPoint point) throws Throwable {  
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder  
                .getRequestAttributes()).getRequest();  
       /* SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");  
        Calendar ca = Calendar.getInstance();  
        String operDate = df.format(ca.getTime());  */
        
        HttpSession session = request.getSession();
        // 从session 里面获取用户名的信息
        SysUcenterAdmin user = (SysUcenterAdmin) session.getAttribute("admin");
        
        String ip = IpAddressUtil.getIpAddress(request);
        LogTypeEunm logTypeEunm = getMthodLogTypeEunm(point);  
        
        //方法名如searchPayv2PayOrderList
        String methodName = point.getSignature().getName();  
        //包名路劲如com.pay.manger.controller.payv2.Payv2PayOrderController
        String packagesUrl = point.getThis().getClass().getName();  
        if (packagesUrl.indexOf("$$EnhancerBySpringCGLIB$$") > -1) { // 如果是SpringCGLIB动态生成的类  
            try {  
            	packagesUrl = packagesUrl.substring(0, packagesUrl.indexOf("$$"));  
            } catch (Exception ex) {  
                ex.printStackTrace();  
            }  
        }  
  
        Object[] method_param = null;  
  
        Object object;  
        try {  
            method_param = point.getArgs(); //获取方法参数   
            // String param=(String) point.proceed(point.getArgs());  
            object = point.proceed();  
        } catch (Exception e) {  
            // 异常处理记录日志..log.error(e);  
            throw e;  
        }  
  
        Map<String,Object> map = new HashMap<>();
        map.put("ip", ip);
        map.put("methodName", methodName);
        map.put("packagesUrl", packagesUrl);
        if(method_param!=null&&method_param.length>0){
        	StringBuffer sb = new StringBuffer();
        	for (Object object2 : method_param) {
        		sb.append(object2);
			}
        	map.put("param", sb.toString());
        }
        map.put("executeType", logTypeEunm.getTypeNum());
        map.put("executeName", logTypeEunm.getTypeName());
        map.put("executor", user.getId());
        map.put("createTime", new Date());
        sysLogService.add(map);
        
        return object;  
    }  
 // 方法运行出现异常时调用    
    // @AfterThrowing(pointcut = "execution(* com.wssys.controller.*(..))",  
    // throwing = "ex")  
    public void afterThrowing(Exception ex) {  
        System.out.println("afterThrowing");  
        System.out.println(ex);  
    }  
    
    // 获取方法的中文备注____用于记录用户的操作类型
    public static LogTypeEunm getMthodLogTypeEunm(ProceedingJoinPoint joinPoint)  
            throws Exception {  
        String targetName = joinPoint.getTarget().getClass().getName();  
        String methodName = joinPoint.getSignature().getName();  
        Object[] arguments = joinPoint.getArgs();  
  
        Class targetClass = Class.forName(targetName);  
        Method[] method = targetClass.getMethods();  
        LogTypeEunm methode = LogTypeEunm.T1;  
        for (Method m : method) {  
            if (m.getName().equals(methodName)) {  
                Class[] tmpCs = m.getParameterTypes();  
                if (tmpCs.length == arguments.length) {  
                    MethodLog methodCache = m.getAnnotation(MethodLog.class);  
                    if (methodCache != null) {  
                        methode = methodCache.logTypeEunm();
                    }  
                    break;  
                }  
            }  
        }  
        return methode;  
    }
}
