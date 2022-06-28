package edu.xdu.debateteam;

import edu.xdu.debateteam.mapper.CommentMapper;
import edu.xdu.debateteam.mapper.DiscussPostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DebateTeamApplicationTests {

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    CommentMapper commentMapper;

    @Test
    void DiscussMapper() {
        System.out.println(discussPostMapper.selectDiscussPosts(0, 0, 10));
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }

    @Test
    void CommentMapper() {
        System.out.println(commentMapper.selectCommentsByEntity(1,281,0,10));
        commentMapper.selectById(10);
    }

}
