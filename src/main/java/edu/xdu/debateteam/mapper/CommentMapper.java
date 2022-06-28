package edu.xdu.debateteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.xdu.debateteam.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    //根据实体类型查
    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);
    //查询数据的条目数
    int selectCountByEntity(int entityType,int entityId);
}
