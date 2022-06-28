package edu.xdu.debateteam;

import edu.xdu.debateteam.mapper.UserMapper;
import edu.xdu.debateteam.pojo.User;
import edu.xdu.debateteam.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;

    //测试Mapper
    @Test
    void selectById() {
        User user = userMapper.selectById(1);
        System.out.println(user.getUsername());
    }

    @Test
    void selectByIdInTeamer() {
        System.out.println(userMapper.selectByIdInTeamer(20011210466L));
    }

    //测试Service
    @Test
    void isTeamer() {
        System.out.println(userService.isTeamMember(20011210466L));
    }

    @Test
    void activate() {
        System.out.println(userService.activeUser(20011210467L, "0205c6d35e13481e9472e1873bd1806c"));
    }

    @Test
    void testLogout() {
        userService.logout("17ef0");
    }

    @Test
    void testSelectByTicket(){
        User user = userService.selectByTicket("136a4");
        System.out.println(user);
    }
}
