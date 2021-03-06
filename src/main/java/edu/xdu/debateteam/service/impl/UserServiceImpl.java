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
        //??????????????????????????????,?????????user???????????????(??????????????????)
        if (userMapper.selectById(user.getId()) != null) {
            map.put(false, "?????????????????????");
            return map;
        }
        if (selectByName(user.getUsername()) != null) {
            map.put(false, "????????????????????????");
            return map;
        }
        if (selectByEmail(user.getEmail()) != null) {
            map.put(false, "?????????????????????");
            return map;
        }
        //??????salt
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        //????????????
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //??????
        user.setType(isTeamMember(user.getId()) ? 2 : 0);
        user.setStatus(0);
        //?????????
        user.setActivationCode(CommunityUtil.generateUUID());
        //????????????
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //????????????
        user.setCreateTime(new Date());

        //??????????????????
        mailCilent.sendMail(user.getEmail(), "???????????????,?????????????????????", user.getActivationCode());
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
            res.put(false, "??????????????????");
        } else {
            if (user.getActivationCode().equals(activeCode)) {
                updateStatus(id, 1);
                clearCache(id);
                res.put(true, "????????????");
            } else {
                res.put(false, "???????????????,?????????????????????");
            }
        }
        return res;
    }

    //????????????


    @Override
    public Map<Boolean, Object> login(@RequestBody String username, String password, int expiredSeconds) {
        //??????????????????????????????
        Map<Boolean, Object> map = new HashMap<>();
        //????????????
        User user = selectByName(username);
        if (user == null) {
            map.put(false, "??????????????????,?????????????????????");
            return map;
        }
//        if (loginTicketMapper.selectByUserId(user.getId()) != null) {
//            map.put(false, "??????????????????!");
//            return map;
//        }
        User userHolded = hostHolder.getUser();
        if (userHolded != null && userHolded.getUsername().equals(username)) {
            map.put(false, "??????????????????!");
            return map;
        }
        boolean passwordCorrect = CommunityUtil.md5(password + user.getSalt()).equals(user.getPassword());
        if (!passwordCorrect) {
            map.put(false, "????????????,?????????");
            return map;
        }
        //??????????????????
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

    //????????????

    @Override
    public void logout(String ticket) {
//        loginTicketMapper.deleteByMap((Map<String, Object>) new HashMap<String, Object>().put("ticket", ticket));
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        redisTemplate.delete(ticketKey);
    }

    //???ticket??????

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

    //Redis??????
    //1.????????????????????????
    private User getCache(long userId) {
        String key = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(key);
    }

    //2.?????????????????????????????????
    private User initCache(long userId) {
        User user = userMapper.selectById(userId);
        String key = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(key, user);
        return user;
    }

    //3.???????????????????????????????????????
    private void clearCache(long userId) {
        String key = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(key);
    }
}
