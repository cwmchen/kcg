package com.kaadog.kcg.dashboard.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.kaadog.kcg.dashboard.config.DashboardConfiguration;

/**
 * 相应请求结果为 api 服务地址
 */
@WebFilter(filterName = "pathFilter", urlPatterns = { "/apServierPath" })
public class PathFilter extends OncePerRequestFilter {

    private final String apServierPath;

    public PathFilter(DashboardConfiguration dashboardConfiguration){
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
