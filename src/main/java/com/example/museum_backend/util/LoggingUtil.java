package com.example.museum_backend.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class LoggingUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);


    public static void logInfo(Class<?> clazz, String message, Object... args) {
        Logger classLogger = LoggerFactory.getLogger(clazz);
        classLogger.info(message, args);
    }

    public static void logError(Class<?> clazz, String message, Object... args) {
        Logger classLogger = LoggerFactory.getLogger(clazz);
        classLogger.error(message, args);
    }

    public static void logUserAction(String userName, String action, String details) {
        MDC.put("userName", userName);
        MDC.put("action", action);
        logger.info("User action: {} - {}", action, details);
        MDC.remove("userName");
        MDC.remove("action");
    }

    public static void logApiCall(String method, String endpoint, String status, long duration) {
        MDC.put("httpMethod", method);
        MDC.put("endpoint", endpoint);
        MDC.put("status", status);
        MDC.put("duration", String.valueOf(duration));
        logger.info("API Call: {} {} - {} ({}ms)", method, endpoint, status, duration);
        MDC.remove("httpMethod");
        MDC.remove("endpoint");
        MDC.remove("status");
        MDC.remove("duration");
    }
}