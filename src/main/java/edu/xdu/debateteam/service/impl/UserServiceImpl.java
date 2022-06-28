package edu.xdu.debateteam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.xdu.debateteam.mapper.LoginTicketMapper;
import edu.xdu.debateteam.mapper.UserMapper;
import edu.xdu.debateteam.pojo.Code;
import edu.xdu.debateteam.pojo.LoginTicket;
import edu.xdu.debateteam.pojo.User;
import edu.xdu.debateteam.service.UserService;
import edu.xdu.debateteam.util.CommunityUtil;
import edu.xdu.debateteam.util.HostHolder;
import edu.xdu.debateteam.util.MailCilent;
import edu.xdu.debateteam.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    MailCilent mailCilent;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    HostHolder hostHolder;
//    @Autowired
//    LoginTicketMapper loginTicketMapper;

    @Override
    public User selectById(long id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    @Override
    public User selectByName(String name) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.lambda().eq(User::getUsername, name);
        return userMapper.selectOne(qw);
    }

    @Override
    public User selectByEmail(String email) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.lambda().eq(User::getEmail, email);
        return userMapper.selectOne(qw);
    }

    @Override
    public boolean addUser(User user) {
        int i = userMapper.insert(user);
        return i == 1;
    }

    @Override
    public boolean updateStatus(long id, int status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        int i = userMapper.updateById(user);
        clearCache(id);
        return i == 1;
    }

    @Override
    public boolean updateHeader(long id, String headerUrl) {
        User user = new User();
        user.setId(id);
        user.setHeaderUrl(headerUrl);
        int i = userMapper.updateById(user);
        clearCache(id);
        return i == 1;
    }

    @Override
    public boolean updatePassword(long id, String password) {
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        int i = userMapper.updateById(user);
        clearCache(id);
        return i == 1;
    }

    @Override
    public Map<Boolean, String> registerUser(User user) {
        Map<Boolean, String> map = new HashMap<>();
        //前端校验输入是否合法,传入的user为合法数据(不存在空数据)
        if (userMapper.selectById(user.getId()) != null) {
            map.put(false, "该学号已被注册");
            return map;
        }
        if (selectByName(user.getUsername()) != null) {
            map.put(false, "该用户名已被注册");
            return map;
        }
        if (selectByEmail(user.getEmail()) != null) {
            map.put(false, "该邮箱已被注册");
            return map;
        }
        //生成salt
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        //生成密码
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //状态
        user.setType(isTeamMember(user.getId()) ? 2 : 0);
        user.setStatus(0);
        //激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //随机头像
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //创建时间
        user.setCreateTime(new Date());

        //发送激活邮件
        mailCilent.sendMail(user.getEmail(), "您的激活码,请在登录后激活", user.getActivationCode());
        addUser(user);
        map.put(true, "");
        return map;
    }

    @Override
    public boolean deleteUser(long id) {
        return userMapper.deleteById(id) == 1;
    }

    @Override
    public boolean isTeamMember(long id) {
        return userMapper.selectByIdInTeamer(id) != null;
    }

    @Override
    public Map<Boolean, String> activeUser(long id, String activeCode) {
        User user = userMapper.selectById(id);
        Map<Boolean, String> res = new HashMap<>();
        if (user.getStatus() == 1) {
            res.put(false, "请勿重复激活");
        } else {
            if (user.getActivationCode().equals(activeCode)) {
                updateStatus(id, 1);
                clearCache(id);
                res.put(true, "激活成功");
            } else {
                res.put(false, "激活码错误,请重新查阅邮件");
            }
        }
        return res;
    }

    //登录功能


    @Override
    public Map<Boolean, Object> login(@RequestBody String username, String password, int expiredSeconds) {
        //前端校验输入是否合法
        Map<Boolean, Object> map = new HashMap<>();
        //验证用户
        User user = selectByName(username);
        if (user == null) {
            map.put(false, "不存在该用户,请注册后再登录");
            return map;
        }
//        if (loginTicketMapper.selectByUserId(user.getId()) != null) {
//            map.put(false, "请勿重复登录!");
//            return map;
//        }
        User userHolded = hostHolder.getUser();
        if (userHolded != null && userHolded.getUsername().equals(username)) {
            map.put(false, "请勿重复登录!");
            return map;
        }
        boolean passwordCorrect = CommunityUtil.md5(password + user.getSalt()).equals(user.getPassword());
        if (!passwordCorrect) {
            map.put(false, "密码错误,请重试");
            return map;
        }
        //生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID().substring(0, 5));
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insert(loginTicket);
        String key = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(key, loginTicket, 30, TimeUnit.MINUTES);
        map.put(true, loginTicket);
        return map;
    }

    //退出登录

    @Override
    public void logout(String ticket) {
//        loginTicketMapper.deleteByMap((Map<String, Object>) new HashMap<String, Object>().put("ticket", ticket));
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        redisTemplate.delete(ticketKey);
    }

    //按ticket查询

    @Override
    public User selectByTicket(String ticket) {
//        long userID = loginTicketMapper.selectUserIdByTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        long userID = loginTicket.getUserId();
        return userMapper.selectById(userID);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
//        return loginTicketMapper.selectByTicket(ticket);
    }

    //Redis更新
    //1.优先从缓存中取值
    private User getCache(long userId) {
        String key = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(key);
    }

    //2.取不到时初始化缓存数据
    private User initCache(long userId) {
        User user = userMapper.selectById(userId);
        String key = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(key, user);
        return user;
    }

    //3.数据变更时直接清除缓存数据
    private void clearCache(long userId) {
        String key = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(key);
    }
}
