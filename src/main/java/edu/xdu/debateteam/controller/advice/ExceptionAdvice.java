package edu.xdu.debateteam.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        logger.error("服务器发生异常" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
//        String xRequestedWith = request.getHeader("x-requested-with");
//        if ("XMLHttpRequest".equals(xRequestedWith)) {
//
//        }
        try {
            response.sendRedirect("http://localhost:8080/discusspost/error");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
