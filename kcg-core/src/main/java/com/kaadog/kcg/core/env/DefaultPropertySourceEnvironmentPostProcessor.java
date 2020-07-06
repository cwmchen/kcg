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

        String contextPath = environment.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath) || "/".equals(contextPath)) {
            contextPath = "";
        }

        String apiServerPath = environment.getProperty("kaadog.kcg.dashboard.api-server-path");
        if (StringUtils.isBlank(apiServerPath)) {
            String port = environment.getProperty("server.port");
            String ip = NetUtil.getLocalhostStr();
            if (StringUtils.isBlank(ip)) {
                ip = "127.0.0.1";
            }
            String http = "http://";
            if (port.equals("443")) {
                http = "https://";
            }
            apiServerPath = http + ip + ":" + port + contextPath;
        }

        System.setProperty("kaadog.kcg.dashboard.api-server-path", apiServerPath);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
