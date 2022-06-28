package edu.xdu.debateteam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.xdu.debateteam.pojo.LoginTicket;
import edu.xdu.debateteam.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@Deprecated
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {
    long selectUserIdByTicket(String ticket);
    LoginTicket selectByTicket(String ticket);
    LoginTicket selectByUserId(long userId);
}
