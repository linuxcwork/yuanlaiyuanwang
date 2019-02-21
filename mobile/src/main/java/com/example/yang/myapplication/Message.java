package com.example.yang.myapplication;

public class Message {
    /*
    * 对端的ID号*/
    private String id;

    /*当前数据属于什么事件
    * 事件：所有事件以EVENT_开头
    *   EVENT_GET_VERIFICATION = 1 获取验证码
    *   EVENT_ACCOUNT_LOGIN = 2    账号登录
    *   EVENT_PHONE_LOGIN = 3      手机登录
    *   EVENT_FORGET_PASSWD = 4    忘记密码
    *   EVENT_REGISTER = 5         注册账号
    *   EVENT_CHANGE_SETTING = 6   更改设置
    *   */
    public int EVENT_GET_VERIFICATION = 1;
    public int EVENT_ACCOUNT_LOGIN =2;
    public int EVENT_PHONE_LOGIN = 3;
    public int EVENT_FORGET_PASSWD = 4;
    public int EVENT_REGISTER = 5;
    public int EVENT_CHANGE_SETTING = 6;
    public int Event_PASSWD_CHANGE = 7;

    private int event;
    /*
    * 发送需要进行处理的数据*/
    private String data;
    /*
    * 数据总长度*/
    private int len;
    /*
    * data数据的类型*/
    private  String datatype;

    public String getDatatype() {
        return datatype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getData() {

        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getEvent() {

        return event;
    }

    public int setEvent(int event) {
        this.event = event;
        return 0;
    }
}
