package edu.xdu.debateteam.service.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.xdu.debateteam.mapper.DiscussPostMapper;
import edu.xdu.debateteam.pojo.DiscussPost;
import edu.xdu.debateteam.service.DiscussPostService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${caffeine.posts.maxsize}")
    private int maxSize;
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSecond;
    // Caffeine核心接口: Cache
    // LoadingCache:同步缓存,多人访问,只允许一人访问数据库
    // AsyncLoadingCache:异步,支持并发取
    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    //初始化
    @PostConstruct
    public void init() {
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSecond, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        //可加redis作为二级缓存

                        //
//                        logger.warn("缓存调用了数据库");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit);
                    }
                });
    }

    @Autowired
    DiscussPostMapper discussPostMapper;

    public DiscussPostServiceImpl() {
    }

    @Override
    public List<DiscussPost> findDiscussPosts(long userId, int offset, int limit) {
        if (userId == 0) {
//            logger.warn("从缓存中拿到了数据");
            return postListCache.get(offset + ":" + limit);
        }
//        logger.warn("从数据库获取了这一页的帖子数据!!");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    @Override
    public int findDiscussPostRows(long userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public DiscussPost findDiscussPostsById(int id) {
        return discussPostMapper.selectById(id);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        //对html关键词转义
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        //敏感词过滤工具：前缀树实现


        return discussPostMapper.insert(discussPost);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        DiscussPost discussPost = discussPostMapper.selectById(id);
        discussPost.setCommentCount(commentCount);
        return discussPostMapper.updateById(discussPost);
    }
}
