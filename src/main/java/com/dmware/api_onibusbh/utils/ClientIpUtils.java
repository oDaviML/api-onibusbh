package com.dmware.api_onibusbh.utils;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIpUtils {

    private ClientIpUtils() {
      
    }

    public static String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("CF-Connecting-IP");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp != null && !clientIp.isEmpty()) {
                clientIp = clientIp.split(",")[0].trim();
            }
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
