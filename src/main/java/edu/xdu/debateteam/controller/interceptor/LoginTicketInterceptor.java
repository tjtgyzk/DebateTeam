package edu.xdu.debateteam.controller.interceptor;

import edu.xdu.debateteam.pojo.LoginTicket;
import edu.xdu.debateteam.pojo.User;
import edu.xdu.debateteam.service.UserService;
import edu.xdu.debateteam.util.CookieUtil;
import edu.xdu.debateteam.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        System.out.println("校验登录ticket");
        if (ticket != null) {
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
//            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
            if (loginTicket != null) {
                //查询用户
                User user = userService.selectByTicket(ticket);
                System.out.println("当前持有用户为:" + user.getUsername());
                //在本次请求中持有用户(放到ThreadLocal中,实现线程隔离)
                hostHolder.addUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        //业务
        System.out.println("post");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        System.out.println("after");
    }
}
