package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfiguration {
    private static String SPLUNK_LOG_NAME;

    @Value("${SPLUNK_LOG_NAME}")
    public void setSplunkLogName(String SPLUNK_LOG_NAME) {
        ApplicationConfiguration.SPLUNK_LOG_NAME = SPLUNK_LOG_NAME;
    }

    public static String getSplunkLogName() {
        return SPLUNK_LOG_NAME;
    }
}
