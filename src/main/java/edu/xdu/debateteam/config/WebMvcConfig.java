package edu.xdu.debateteam.config;

import edu.xdu.debateteam.controller.interceptor.LoginRequiredInterceptor;
import edu.xdu.debateteam.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginTicketInterceptor LoginTicketInterceptor;
    @Autowired
    LoginRequiredInterceptor LoginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(LoginTicketInterceptor)
                .addPathPatterns("/*");
        registry.addInterceptor(LoginRequiredInterceptor)
                .addPathPatterns("/*");
    }

}
