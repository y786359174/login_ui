package com.example.login_ui.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import com.example.login_ui.MessageTransmit;

import java.io.IOException;

public class ApplicationUtil extends Application {
    private static final String TAG = "ApplicationUtil";
    public UserBean playerBean = new UserBean();
    private MessageTransmit mTransmit;
    private Context mContext;
    private SharedPreferences mShared;
    public void SocketInit() throws IOException, Exception {
        mTransmit = new MessageTransmit();                                   //socket发送数据class
        new Thread(mTransmit).start();                                       //???新建Thread(mTransmit)并开始
        mTransmit.setmcontext(mContext);
    }

    public void setmcontext(Context context){mContext=context;}
    public void SocketSendmsg(String msgstr)
    {
        Message msg = Message.obtain();                 //???  //话说为什么不能直接发送String
        msg.obj = msgstr;                             //设置msg的字符串信息
        mTransmit.mRecvHandler.sendMessage(msg);        //给mTransmit这个handle发送信息
    }
/*
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }
*/
    public void mShaerdInit()
    {
        mShared=getSharedPreferences("LoginShared",Context.MODE_PRIVATE);//创建共享参数，文件名LoginShared，私有模式
    }
    public String mSharedGetString(String key,String defValue){
        String str = mShared.getString(key,defValue);
        return  str;
    }
    public void mSharedSetActPswd(String Accountstr,String Passwordstr){
        SharedPreferences.Editor editor =mShared.edit();  //shared的editer
        editor.putString("Accountstr", Accountstr);
        editor.putString("PasswordstrEncrypt", Passwordstr);
        editor.commit();
    }
}