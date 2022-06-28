package edu.xdu.debateteam.service;

import edu.xdu.debateteam.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface CommentService {
    List<Comment> findCommtntsByEntity(int entityType, int entityId, int offset, int limit);

    int findCommentsCount(int entityType, int entityId);


    int addComment(Comment comment);
}
