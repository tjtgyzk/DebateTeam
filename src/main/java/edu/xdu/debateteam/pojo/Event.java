package edu.xdu.debateteam.pojo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Event {
    //事件类型(主题)
    private String topic;
    //触发用户
    private long userId;
    //实体信息
    private int entityType;
    private int entityId;
    //实体作者
    private long entityUserId;
    //其它数据
    private Map<String,Object> data = new HashMap<>();

    public void setData(String key,Object value) {
        data.put(key,value);
    }
}
