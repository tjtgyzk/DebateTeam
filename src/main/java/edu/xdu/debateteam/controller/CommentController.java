package edu.xdu.debateteam.controller;

import edu.xdu.debateteam.pojo.Code;
import edu.xdu.debateteam.pojo.Comment;
import edu.xdu.debateteam.pojo.Result;
import edu.xdu.debateteam.pojo.User;
import edu.xdu.debateteam.service.CommentService;
import edu.xdu.debateteam.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;

    @PostMapping
    public Result addComment(@RequestBody Comment comment) {
        User user = hostHolder.getUser();
        if (user == null) {
            return new Result(null, Code.SAVE_ERR, "用户未登录");
        }
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return new Result(null, Code.SAVE_OK, "评论成功!");
    }
}
