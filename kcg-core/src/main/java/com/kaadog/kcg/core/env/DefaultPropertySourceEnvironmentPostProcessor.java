package com.kaadog.kcg.core.env;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import cn.hutool.core.net.NetUtil;

public class DefaultPropertySourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        String port = environment.getProperty("server.port");
        String ip = NetUtil.getLocalhostStr();
        if (StringUtils.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        String contextPath = environment.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "";
        }

        System.setProperty("kcg.local.ip", ip);
        System.setProperty("kcg.local.port", port);
        System.setProperty("kcg.local.context-path", contextPath);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
