package com.example.login_ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MessageTransmit implements Runnable {
    private static final String TAG = "MessageTransmit";
    private static final String SOCKETRCV_ACT="Socketrcv_act";
    private static final String Loginstr="Login";
//    private static final String SOCKET_IP="101.200.125.165";// Socket服务器的IP，根据实际情况修改
    private static final String SOCKET_IP="192.168.0.18";// Socket服务器的IP，根据实际情况修改
    private static final int SOCKET_PORT = 826;// Socket服务器的端口，根据实际情况修改
    private static final int TIME_OUT = 10000;
    private Socket mSocket;
    private BufferedReader mReader = null;
    private static OutputStream mWriter = null;

    @Override
    public void run() {
        mSocket = new Socket();
        try {
            mSocket.connect(new InetSocketAddress(SOCKET_IP, SOCKET_PORT), TIME_OUT);
            Log.i(TAG,"connect server succeed!\nip="+SOCKET_IP+"\nport="+SOCKET_PORT);
            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"UTF-8"));
            mWriter = mSocket.getOutputStream();
            new RecvThread().start();//启动一条子线程读取服务器的返回数据
            Looper.prepare();
            Looper.loop();

        } catch (Exception e) {
            Log.i(TAG,"connect server failed!");
            e.printStackTrace();
        }

    }
    //定义接收UI线程的Handler对象,App向后台服务器发送消息
    public Handler mRecvHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String send_msg = msg.obj.toString() + "\n"; //不知道为什么在线程中处理就会报错
            new SendThread(send_msg).start();//不能再handler里连接服务器，必须在子线程中
        }
    };
                //定义消息发送子线程,App从后台服务器发送消息
    private class SendThread extends Thread {
        private String msg;        //传递信息
        public SendThread (String msg){    //构造器
            this.msg=msg;
        }
        @Override
        public void run(){
            //换行符相当于回车键,表示“我写好了发出去吧//接收端需要换行符检测已结束发送
            try {
                mWriter.write(msg.getBytes("utf8"));
                Log.i(TAG,"SendThread---"+msg);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG,"SendThread send_msg failed!");
            }
        }
    }
    private Context mcontext;
    public void setmcontext(Context context) { mcontext=context; }  //传递context用于sendbroadcast
                //定义消息接收子线程,App从后台服务器接收消息
    private class RecvThread extends Thread {
        @Override
            public void run(){
                try {
                    String contentRcv = null;
                    while (null != (contentRcv = mReader.readLine())) {//读取来自服务器的数据
//                        Message msg = Message.obtain();
//                        msg.obj = content;
//                        MainActivity.handler.sendMessage(msg);  //改用广播发送

/**********************数据处理*****************************************************/
                        Intent sendIntend = new Intent();
                        sendIntend.setAction(SOCKETRCV_ACT);
                        sendIntend.putExtra(Loginstr, contentRcv);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        mcontext.sendBroadcast(sendIntend);



                        Log.i(TAG, "sendbroadcast---"+contentRcv);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i(TAG,"receiving message failed!");
                }
        }
    }

}