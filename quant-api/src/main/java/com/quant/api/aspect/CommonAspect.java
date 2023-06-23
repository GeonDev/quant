package com.quant.api.aspect;


import com.quant.core.exception.ForbiddenException;
import com.quant.core.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class CommonAspect {

    @Value("${quant.allow-ip}")
    private List<String> allowIp;

    @Before("execution(* com.quant.api.controller.*.*(..)) && @annotation(com.quant.api.aspect.option.AllowAccessIp)")
    public void addResponseRedirectAdvice() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String remoteIp = CommonUtils.getRequestRemoteIp(request);
        boolean access = false;

        for(String ip : allowIp ){
            if(ip.equals(remoteIp)){
                access = true;
                break;
            }
        }

        if(!access){
            throw new ForbiddenException("허용되지 않은 IP 접근");
        }

    }


}
