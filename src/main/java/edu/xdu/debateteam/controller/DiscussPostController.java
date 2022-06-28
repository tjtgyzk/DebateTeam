package edu.xdu.debateteam.controller;

import edu.xdu.debateteam.pojo.*;
import edu.xdu.debateteam.service.CommentService;
import edu.xdu.debateteam.service.DiscussPostService;
import edu.xdu.debateteam.service.UserService;
import edu.xdu.debateteam.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/discusspost")
public class DiscussPostController {
    @Autowired
    UserService userService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;

    //首页帖子
    @GetMapping("/{page}")
    public Result getIndexPage(@PathVariable int page) {
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, 10 * (page - 1), 10);
//        int i = 1 / 0;
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                User user = userService.selectById(post.getUserId());
                map.put("post", post);
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        if (discussPosts.isEmpty()) {
            return new Result(null, Code.SELECT_ERR, "查询不到数据");
        }
        return new Result(discussPosts, Code.SELECT_OK, null);
    }

    //添加帖子
    @PostMapping
    public Result addDiscussPost(@RequestBody DiscussPost discussPost) {
        User user = hostHolder.getUser();
        if (user == null) {
            return new Result(null, Code.SAVE_ERR, "用户未登录");
        }
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        return new Result(null, Code.SAVE_OK, "发布成功!");
    }

    //查询帖子详情
    @GetMapping("/detail/{discussPostId}/{commentPage}")
    public Result getDicussPost(@PathVariable int discussPostId, @PathVariable int commentPage) {
        //帖子
        DiscussPost discussPost = discussPostService.findDiscussPostsById(discussPostId);
        //作者
        User user = userService.selectById(discussPost.getUserId());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("post", discussPost);
        map.put("user", user);
        //给帖子的评论
        List<Comment> commentList = commentService.findCommtntsByEntity(1, discussPost.getId(), 10 * (commentPage - 1), 10);
        //给每个评论一个map,存放评论内容,评论的作者,以及评论的评论
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.selectById(comment.getUserId()));
                //该评论的回复列表
                List<Comment> replyList = commentService.findCommtntsByEntity(2, comment.getId(), 0, Integer.MAX_VALUE);
                //给每个评论的回复一个Map,存放回复该评论的作者,以及评论
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.selectById(reply.getUserId()));
                        //回复的目标用户
                        User target = reply.getTargetId() == 0 ? null : userService.selectById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                //该评论的回复数量
                int commentsCount = commentService.findCommentsCount(2, comment.getId());
                commentVo.put("replycount", commentsCount);
                commentVoList.add(commentVo);
            }
        }
        map.put("commentsVoList", commentVoList);
        return new Result(map, Code.SELECT_OK, null);
    }

    @GetMapping("/error")
    public Result getError() {
        return new Result(null, Code.CONTROLLER_ERR, "服务器异常!请跳转至500页面");
    }

}
