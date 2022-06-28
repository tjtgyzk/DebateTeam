package edu.xdu.debateteam.service;

import edu.xdu.debateteam.pojo.LoginTicket;
import edu.xdu.debateteam.pojo.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {
    User selectById(long id);

    User selectByName(String name);

    User selectByEmail(String email);

    boolean addUser(User user);

    boolean updateStatus(long id, int status);

    boolean updateHeader(long id, String headerUrl);

    boolean updatePassword(long id, String password);

    boolean deleteUser(long id);

    boolean isTeamMember(long id);

    Map<Boolean, String> registerUser(User user);

    Map<Boolean, String> activeUser(long id, String activeCode);

    Map<Boolean, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    User selectByTicket(String ticket);

    LoginTicket findLoginTicket(String ticket);
}
