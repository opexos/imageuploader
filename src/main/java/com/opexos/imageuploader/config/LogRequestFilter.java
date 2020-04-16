package com.opexos.imageuploader.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LogRequestFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        assert request instanceof HttpServletRequest;

        long start = System.currentTimeMillis();
        MDC.put("requestId", UUID.randomUUID().toString());
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - start;

        val httpRequest = (HttpServletRequest) request;

        String query = httpRequest.getQueryString();
        query = query == null ? "" : "?" + query;

        log.info("Method executed: {} {}{} Duration: {} ms",
                httpRequest.getMethod(), httpRequest.getRequestURI(), query, duration);
    }

}
