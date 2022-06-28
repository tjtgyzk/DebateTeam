package edu.xdu.debateteam.service.impl;

import edu.xdu.debateteam.mapper.CommentMapper;
import edu.xdu.debateteam.pojo.Comment;
import edu.xdu.debateteam.service.CommentService;
import edu.xdu.debateteam.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    DiscussPostService discussPostService;

    @Override
    public List<Comment> findCommtntsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int findCommentsCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {

        //过滤标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        int rows = commentMapper.insert(comment);
        //更新评论数量
        if (comment.getEntityType() == 1) {
            //类型是帖子
            int count = commentMapper.selectCountByEntity(1, comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }
}
