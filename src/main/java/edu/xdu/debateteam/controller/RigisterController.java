package edu.xdu.debateteam.controller;

import edu.xdu.debateteam.pojo.Code;
import edu.xdu.debateteam.pojo.Result;
import edu.xdu.debateteam.pojo.User;
import edu.xdu.debateteam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/register")
public class RigisterController {
    @Autowired
    UserService userService;

    @PostMapping
    public Result save(@RequestBody User user) {
        Result res = new Result();
        long id = user.getId();
        Map<Boolean, String> map = userService.registerUser(user);
        if (map.containsKey(true)) {
            res.setData(userService.selectById(id));
            res.setCode(Code.SAVE_OK);
        } else {
            res.setCode(Code.SAVE_ERR);
            res.setMsg(map.get(false));
        }
        return res;
    }
}
