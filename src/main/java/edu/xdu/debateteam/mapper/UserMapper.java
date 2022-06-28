package edu.xdu.debateteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.xdu.debateteam.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User selectByIdInTeamer(long id);
}
