package edu.xdu.debateteam.controller;

import edu.xdu.debateteam.pojo.Code;
import edu.xdu.debateteam.pojo.Result;
import edu.xdu.debateteam.pojo.User;
import edu.xdu.debateteam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/id/{id}")
    public Result getById(@PathVariable long id) {
        User user = userService.selectById(id);
        Result res = new Result();
        if (user == null) {
            res.setCode(Code.SELECT_ERR);
            res.setMsg("未查询到指定用户,请稍后再试");
        } else {
            res.setCode(Code.SELECT_OK);
            res.setData(user);
        }
        return res;
    }

    @GetMapping("/username/{username}")
    public Result getByName(@PathVariable String username) {
        User user = userService.selectByName(username);
        Result res = new Result();
        if (user == null) {
            res.setCode(Code.SELECT_ERR);
            res.setMsg("未查询到指定用户,请稍后再试");
        } else {
            res.setCode(Code.SELECT_OK);
            res.setData(user);
        }
        return res;
    }

    @GetMapping("/email/{email}")
    public Result getByEmail(@PathVariable String email) {
        User user = userService.selectByEmail(email);
        Result res = new Result();
        if (user == null) {
            res.setCode(Code.SELECT_ERR);
            res.setMsg("未查询到指定用户,请稍后再试");
        } else {
            res.setCode(Code.SELECT_OK);
            res.setData(user);
        }
        return res;
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable long id){
        Result res = new Result();
        boolean flag = userService.deleteUser(id);
        if (flag) {
            res.setCode(Code.DELETE_OK);
        } else {
            res.setCode(Code.SAVE_ERR);
            res.setMsg("删除失败,请稍后重试");
        }
        return res;
    }
}
