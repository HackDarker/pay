package com.pay.manger.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pay.business.util.LogTypeEunm;

@Target({ ElementType.METHOD, ElementType.TYPE })  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
public @interface MethodLog {  
    LogTypeEunm logTypeEunm() default LogTypeEunm.T1;
   // String desc() default "";  
}  
