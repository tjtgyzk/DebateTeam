package edu.xdu.debateteam.controller;

import com.alibaba.druid.util.Base64;
import com.alibaba.druid.util.StringUtils;
import com.google.code.kaptcha.Producer;
import edu.xdu.debateteam.pojo.*;
import edu.xdu.debateteam.service.UserService;

import edu.xdu.debateteam.util.CommunityUtil;
import edu.xdu.debateteam.util.HostHolder;
import edu.xdu.debateteam.util.RedisKeyUtil;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.naming.spi.DirStateFactory;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    HostHolder hostHolder;

    @GetMapping("/kaptcha")
    @ResponseBody
    public Result getKaptcha(HttpServletResponse response) throws IOException {
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
//        session.setAttribute("kaptcha", text);
        //存入redis
        //验证码owner
        String kaptchaOwner = CommunityUtil.generateUUID().substring(0, 5);
        Cookie cookie = new Cookie("kapthaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath("http://localhost:8080/");
        response.addCookie(cookie);
        //存入redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        //图片输出给浏览器
        response.setContentType("image/jpeg");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        String imgString = "data:image/gif;base64," + Base64.byteArrayToBase64(stream.toByteArray());
        stream.flush();
        stream.close();
        return new Result(imgString, Code.SELECT_OK, null);
//        try {
//            ServletOutputStream outputStream = response.getOutputStream();
//            ImageIO.write(image, "png", outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @PostMapping
    //由前端调用checkCode方法提前判断验证码是否错误
    public Result login(@RequestBody LoginUser loginuser, HttpServletResponse response) {
        Result res = new Result();
        String username = loginuser.getUsername();
        String password = loginuser.getPassword();
        boolean rememberMe = loginuser.isRememberMe();
        //检查账号密码
        int expireSeconds = rememberMe ? Code.REMEMBER_LOGIN_DESPIRED : Code.DEFAULT_LOGIN_DESPIRED;
        Map<Boolean, Object> map = userService.login(username, password, expireSeconds);
        if (map.containsKey(true)) {
            res.setCode(Code.LOGIN_OK);
            LoginTicket loginTicket = (LoginTicket) map.get(true);
            res.setData(loginTicket);
            Cookie cookie = new Cookie("ticket", loginTicket.getTicket());
            response.addCookie(cookie);
        } else {
            res.setCode(Code.LOGIN_ERR);
            res.setMsg((String) map.get(false));
        }
        return res;
    }

    //校验验证码是否正确
    @GetMapping("/{code}")
    public Result checkCode(@PathVariable String code, @CookieValue("kapthaOwner") String kapthaOwner) {
        Result res = new Result();
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (!StringUtils.isEmpty(kapthaOwner)) {
            String key = RedisKeyUtil.getKaptchaKey(kapthaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(key);
        }
        if (kaptcha.equalsIgnoreCase(code)) {
            res.setCode(Code.SELECT_OK);
        } else {
            res.setCode(Code.SELECT_ERR);
            res.setMsg("验证码错误");
        }
        return res;
    }

    //登出
    @DeleteMapping("/{ticket}")
    public Result checkCode(@PathVariable String ticket) {
        Result res = new Result();
        userService.logout(ticket);
        res.setCode(Code.DELETE_OK);
        return res;
    }
}
