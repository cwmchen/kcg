package com.kaadog.kcg.dashboard.config;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kaadog.kcg.dashboard.config.DashboardConfiguration.CorsRegistration;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private DashboardConfiguration dashboardConfiguration;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        if (CollectionUtils.isNotEmpty(dashboardConfiguration.getCorsRegistrations())) {
            for (CorsRegistration ca : dashboardConfiguration.getCorsRegistrations()) {
                org.springframework.web.servlet.config.annotation.CorsRegistration corsRegistration = registry
                        .addMapping(ca.getPathPattern());

                corsRegistration.allowedOrigins(ca.getAllowedOrigins());
                corsRegistration.allowedMethods(ca.getAllowedMethods());
                corsRegistration.allowedHeaders(ca.getAllowedHeaders());

                if (ca.getExposedHeaders() != null) {
                    corsRegistration.exposedHeaders(ca.getExposedHeaders());
                }

                if (ca.getAllowCredentials() != null) {
                    corsRegistration.allowCredentials(ca.getAllowCredentials());
                }

                corsRegistration.maxAge(ca.getMaxAge());
            }
        }
    }
}
