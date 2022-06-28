package edu.xdu.debateteam.service;

import edu.xdu.debateteam.pojo.DiscussPost;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DiscussPostService {
    List<DiscussPost> findDiscussPosts(long userId, int offset, int limit);

    int findDiscussPostRows(long userId);

    DiscussPost findDiscussPostsById(int id);

    int addDiscussPost(DiscussPost dicussPost);

    int updateCommentCount(int id,int commentCount);
}
