package edu.xdu.debateteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.xdu.debateteam.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {
    //分页查询, 可以查询某个用户的帖子,当username不为null时触发
    List<DiscussPost> selectDiscussPosts(long userId, int offset, int limit);

    //共有多少条数据
    int selectDiscussPostRows(@Param("userId") long userId);
}
