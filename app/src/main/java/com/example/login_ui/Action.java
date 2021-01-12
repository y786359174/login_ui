package com.example.login_ui;

/**
 * 各种标识
 * 枚举不会用，就存一下数据格式了
 */
public class Action {
    /*********************************广播action标识**************************************************/

    public static final String SOCKETRCV_Login="Socketrcv_Login";
    /**
     * 广播Action
     * 发送给LoginActivity
     */


    /*********************************数据包包头标识***************************************************/

    public static final String LoginReq = "LoginReq";
    /**
     * 登录请求
     * Eg.  LoginReq|Account.length()|Account|Password.length()|Password
     */

    public static final String LoginResp = "LoginResp";
    /**
     * 登录响应
     * Eg.  LoginResp|loginstate|id|nickname
     * Eg.  LoginResp|1                 //账号不存在
     * Eg.  LoginResp|2                 //密码错误
     * Eg.  LoginResp|0|id|nickname     //登陆成功
     * 待补充
     */

}
