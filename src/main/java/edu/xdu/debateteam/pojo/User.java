package edu.xdu.debateteam.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //学号
    private long id;
    private String username;
    private String password;
    private String salt;
    private String email;
    //0表示游客,1表示队长(管理员),2表示队员
    private int type;
    //是否激活
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
    //早训打卡天数
    private int morningTrains;
}
