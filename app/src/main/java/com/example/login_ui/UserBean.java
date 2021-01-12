package com.example.login_ui;

public class UserBean {
    private Long id;          //用户ID，不显示
    private String nickName;     //昵称，显示用

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }


}
