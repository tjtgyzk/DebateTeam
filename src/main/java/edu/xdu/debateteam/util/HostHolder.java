package edu.xdu.debateteam.util;

import edu.xdu.debateteam.pojo.User;
import org.springframework.stereotype.Component;

//持有用户信息,代替session对象
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void addUser(User user) {
        users.set(user);
    }
    public User getUser() {
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
