package com.example.login_ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private View match_view;
    private TextView textView;
    private int threadnum = 0;
    private boolean SocketIsConnected = false;

    private ApplicationUtil appUtil;  //新建application用来？？保存socket连接信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        match_view =(View)findViewById(R.id.match_view);
        match_view.setOnClickListener(this);
        textView =(TextView)findViewById(R.id.textView);
        textView.setText("点击屏幕开始");
        Log.i(TAG,"succeed");

/** 利用application连接socket，在MainActivity初始化*/
        appUtil =  (ApplicationUtil) MainActivity.this.getApplication();  //设置为唯一application
        appUtil.setmcontext(this);                                    //传递Context用于broadcast
        try {
            /*********socketinit***********************/
            appUtil.SocketInit();                                         //在MainActivity进行初始化
            Log.i(TAG, "初始化SOCKET成功");
            SocketIsConnected=true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        appUtil.mShaerdInit();//创建SharedPreferences共享参数
/**用于判断网络是否可用，并利用Toast弹窗提示*/
        /*ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
               // 获取代表联网状态的NetWorkInfo对象
                NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                 // 获取当前的网络连接是否可用
                 if (null == networkInfo)
                  {
                         Toast.makeText(this, "当前的网络连接不可用", Toast.LENGTH_SHORT).show();
                          //当网络不可用时，跳转到网络设置页面
                       startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 1);

                } else
                 {
                          boolean available = networkInfo.isAvailable();
                            if (available)
                         {
                                  Toast.makeText(this, "当前的网络连接可用", Toast.LENGTH_SHORT).show();
                                  Log.i(TAG, "当前的网络连接可用");
                         }else
                        {
                                Toast.makeText(this, "当前的网络连接不可用", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "当前的网络连接不可用");
                         }
                 }*/

/*        mTransmit = new MessageTransmit();                                   //socket发送数据class
        new Thread(mTransmit).start();                                       //???新建Thread(mTransmit)并开始*/
/*        handler.post(updateThread);*/

    }

    @Override
    public void onClick(View v) {

        if (SocketIsConnected) {
            Intent login_intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login_intent);
            Log.i(TAG, "切换到LoginActivity");
//            finish();      //在MainActivity处理socket，不能关闭
        }
    }

//    /**
//     * 实现线程有两种方法
//     * 1.继承Thread类
//     * 2.实现Runnale接口*/
//    private Runnable updateThread = new Runnable() {
//        @Override
//        public void run() {
//            textView.setText("UpdateThread"+threadnum);
//            threadnum=threadnum+1;
//            handler.postDelayed(updateThread,3000);
//            /**
//             * 当线程运行玩run方法之后，该线程就会进入死亡状态，
//             * 所以在run方法里面添加运行本身线程的消息可以实现循环多次运行*/
//        }
//    };


/** 用作主机检查判断ip是否可连接*/
/*     @Override
    public void onClick( View v) {
        new CheckThread("101.200.125.165").start();//101.200.125.165
    }
    private Handler mHandler= new Handler(){
    @Override
    public void handleMessage(Message msg) {
        textView.setText("主机检查结果如下:\n" + msg.obj);
    }
    };
    private class CheckThread extends Thread{
        private String mHostName;
        public CheckThread(String host_name) {
            mHostName = host_name;
        }
            @Override
            public void run(){
                Message message = Message.obtain();
                try {
                    InetAddress host = InetAddress.getByName(mHostName);
                    boolean isReachable = host.isReachable(5000);
                    String desc = (isReachable) ? "可以连接" : "无法连接";
//                    if (isReachable == true) {
                        desc = String.format("%s\n主机名为%s\n主机地址为%s",
                                desc, host.getHostName(), host.getHostAddress());
//                    }
                    message.what = 0;
                    message.obj = desc;
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = -1;
                    message.obj = e.getMessage();
                }
                mHandler.sendMessage(message);
            }

        }*/
}