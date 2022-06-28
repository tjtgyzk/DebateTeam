package edu.xdu.debateteam.pojo;

public class Code {
    //200 成功
    //xxx 编号
    public static final Integer SAVE_OK = 200001;
    public static final Integer SELECT_OK = 200002;
    public static final Integer DELETE_OK = 200003;
    public static final Integer UPDATE_OK = 200004;
    public static final Integer LOGIN_OK = 200005;
    //400 失败
    //xxx 编号
    public static final Integer SAVE_ERR = 400001;
    public static final Integer SELECT_ERR = 400002;
    public static final Integer DELETE_ERR = 400003;
    public static final Integer UPDATE_ERR = 400004;
    public static final Integer LOGIN_ERR = 400005;

    public static final Integer CONTROLLER_ERR = 500001;

    //常量
    public static final Integer DEFAULT_LOGIN_DESPIRED = 3600 * 12;
    public static final Integer REMEMBER_LOGIN_DESPIRED = 3600 * 24 * 7;

    //权限
    public static final String AUTHORITY_USER = "USER";
    public static final String AUTHORITY_TEAMER = "TEAMER";
    public static final String AUTHORITY_LEDAER = "LEADER";
}
