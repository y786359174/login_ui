package com.example.login_ui.util;

public class UserBean {
    private int id;          //用户ID，不显示
    private String nickName;     //昵称，显示用

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }


}
