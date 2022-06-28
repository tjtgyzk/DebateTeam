package edu.xdu.debateteam.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class Comment {
    private int id;
    private long userId;
    //实体类型 1 帖子 2 评论
    private int entityType;
    //实体id
    private int entityId;
    //对于评论,可以回复该条评论或者该评论的评论
    private long targetId;

    private String content;
    //0 正常 1 禁用
    private int status;
    private Date createTime;
}
