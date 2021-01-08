package com.example.login_ui;

import android.app.Application;
import android.content.Context;
import android.os.Message;

import java.io.IOException;

public class ApplicationUtil extends Application {
    private static final String TAG = "ApplicationUtil";
    private MessageTransmit mTransmit;
    public void SocketInit() throws IOException, Exception {
        mTransmit = new MessageTransmit();                                   //socket发送数据class
        new Thread(mTransmit).start();                                       //???新建Thread(mTransmit)并开始
    }
    public void setmcontext(Context context){mTransmit.setmcontext(context);}
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

}