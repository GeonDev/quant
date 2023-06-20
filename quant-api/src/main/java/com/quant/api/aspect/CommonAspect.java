package com.quant.api.aspect;


import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class CommonAspect {

    @Before("execution(* com.quant.api.controller.*.*(..)) && @annotation(com.quant.api.aspect.option.AllowAccessIp)")
    public void addResponseRedirectAdvice() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String ip = getRemoteAddr(request);


    }



    protected String getRemoteAddr(HttpServletRequest request){
        return (null != request.getHeader("X-FORWARDED-FOR")) ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
    }

}
