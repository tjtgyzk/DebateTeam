package edu.xdu.debateteam;


import edu.xdu.debateteam.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void TestString() {
        String key = "stu:";
        User user = new User();
        user.setId(20011210466L);
        user.setEmail("864793683@qq.com");
        redisTemplate.opsForValue().set(key,user);
        User x = (User)redisTemplate.opsForValue().get(key);
        System.out.println(x.getId());
        System.out.println(x.getEmail());
//        System.out.println(redisTemplate.opsForValue().increment(key));
    }
}
