package org.christor.ordersystem.service;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BaseUrlExtractionFilter implements Filter {

    String baseUrl;

    public BaseUrlExtractionFilter() {
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        if (baseUrl == null) {
            final String serverName = sr.getServerName();
            final int serverPort = sr.getServerPort();
            final String scheme = sr.getScheme();
            final String contextPath = sr.getServletContext().getContextPath();

            final boolean isStandardPort = scheme.equals("http") && serverPort == 80 || scheme.equals("https") && serverPort == 443;
            final StringBuilder baseUrlBuilder = new StringBuilder(scheme)
                    .append("://").append(serverName)
                    .append(isStandardPort ? "" : ":")
                    .append(isStandardPort ? "" : String.valueOf(serverPort))
                    .append("/").append(contextPath)
                    .append(contextPath.length() == 0 ? "" : "/");
            baseUrl = baseUrlBuilder.toString();
        }
        fc.doFilter(sr, sr1);
    }

    @Override
    public void destroy() {
    }

}
