package com.kaadog.kcg.dashboard.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.kaadog.kcg.dashboard.config.DashboardConfiguration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 相应请求结果为 api 服务地址
 */
@WebFilter(filterName = "pathFilter", urlPatterns = { "/apServierPath" })
public class PathFilter extends OncePerRequestFilter {

    private final String apServierPath;

    public PathFilter(DashboardConfiguration dashboardConfiguration) {
        StringBuilder path = new StringBuilder("var _globalPath ='");
        path.append(dashboardConfiguration.getApiServerPath());
        path.append("';");
        apServierPath = path.toString();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.setContentType("text/javascript");
        ServletOutputStream out = response.getOutputStream();
        out.write(apServierPath.getBytes());
        out.flush();
        out.close();
    }
}
