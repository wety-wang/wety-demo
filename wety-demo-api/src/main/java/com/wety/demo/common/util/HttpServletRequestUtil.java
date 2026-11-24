package com.wety.demo.common.util;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author wety
 */
public class HttpServletRequestUtil {

    private HttpServletRequestUtil() {
    }

    /**
     * 通过HttpServletRequest获取ClientIp
     *
     * @param request
     *
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotEmpty(ip)){
            String[] ips = ip.split(",");
            for (String s : ips) {
                if (StringUtils.isNotBlank(s) && !StringUtils.equalsIgnoreCase("unknown", s.trim())) {
                    return s.trim();
                }
            }
        }
        return ip;
    }
}
