package com.example.login_ui;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketService extends Service {
    private Socket clientSocket = null;
    private ServerSocket mServerSocket = null;
    private SocketAcceptThread socketAcceptThread = null;
    private SocketReceiveThread socketReceiveThread = null;
    private SocketReceiver socketReceiver;
    public static final String SOCKER_ACTION = "com.jia.Socket.Control";
    public static final String SOCKER_RCV = "com.jia.Socket.ReceiveStr";
    private boolean stop = true;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "socket service created");
        socketReceiver = new SocketReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SOCKER_ACTION);
        registerReceiver(socketReceiver, filter);


        socketAcceptThread = new SocketAcceptThread();
        // 开启 Socket 监听线程
        socketAcceptThread.start();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("service", "socket service start");
    }


    @Override
    public void onDestroy() {
        Log.d("service", "socket service destroy!");
    }


    public class SocketReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(SOCKER_ACTION)) {
                String sub_action = intent.getExtras().getString("ACTION");
                if(sub_action.equals("reconnect")) {
                    Log.d("service", "socket service: reconnect.");


                    socketAcceptThread = new SocketAcceptThread();
                    // 开启 Socket 监听线程
                    socketAcceptThread.start();
                }
            }
        }
    }


    private class SocketAcceptThread extends Thread
    {
        @Override
        public void run()
        {
            Log.d("service", "socket service - SocketAcceptThread::run");
            try {
                // 实例化ServerSocket对象并设置端口号为 12589
                mServerSocket = new ServerSocket(12589);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            try {
                // 等待客户端的连接（阻塞）
                clientSocket = mServerSocket.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            socketReceiveThread = new SocketReceiveThread(clientSocket);
            stop = false;
            // 开启接收线程
            socketReceiveThread.start();


            Intent sendIntent = new Intent(SOCKER_RCV);
            sendIntent.putExtra("action", "ClientIP");
            sendIntent.putExtra("content", clientSocket.getInetAddress().getHostAddress());
            // 发送广播，将被Activity组件中的BroadcastReceiver接收到
            sendBroadcast(sendIntent);
        }
    }


    private class SocketReceiveThread extends Thread
    {
        private InputStream mInputStream = null;
        private byte[] buf;
        private String str = null;
        Socket sUsed;


        SocketReceiveThread(Socket s)
        {
            Log.d("service", "socket service - SocketReceiveThread");
            try {
                // 获得输入流
                this.mInputStream = s.getInputStream();
                sUsed = s;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        @Override
        public void run()
        {
            Log.d("service", "socket service - SocketReceiveThread::run");
            while((!stop) && (!mServerSocket.isClosed()))
            {
                this.buf = new byte[2048];


                // 读取输入的数据(阻塞读)
                try {
                    this.mInputStream.read(buf);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


                // 字符编码转换
                try {
                    this.str = new String(this.buf, "GB2312").trim();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                Intent sendIntent = new Intent(SOCKER_RCV);
                sendIntent.putExtra("action", "RcvStr");
                sendIntent.putExtra("content", this.str);
                // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                sendBroadcast(sendIntent);
            }
        }
    }
}