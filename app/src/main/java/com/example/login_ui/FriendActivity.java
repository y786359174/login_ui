package com.example.login_ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_ui.util.ApplicationUtil;
import com.example.login_ui.util.ProcessString;

import static com.example.login_ui.util.Action.GetFriendListReq;
import static com.example.login_ui.util.Action.SOCKETRCV_game;
import static com.example.login_ui.util.Action.SpeakOutResp;

public class FriendActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "FriendActivity";
    private Button BackBtn ;
    private ApplicationUtil appUtil;  //新建application用来？？保存socket连接信息
    private BcastReceiver bcastReceiver;
    private IntentFilter filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);             //提前创建，但并不显示
        Log.i(TAG,"onCreate");
        BackBtn =  (Button)findViewById(R.id.BackBtn);
        BackBtn.setOnClickListener(this);                                   //返回button设置click事件listener
        /****************************调用socket****************************/
        appUtil =  (ApplicationUtil) this.getApplication();    //设置为唯一application
        /*****************************接收广播******************************/
        bcastReceiver =  new BcastReceiver();
        filter = new IntentFilter();
        filter.addAction(SOCKETRCV_game);     //只有持有相同的action的接受者才能接收广播
        registerReceiver(bcastReceiver, filter);
        Log.i(TAG,"BroadCastReceiver succeed");
        /******************************刷新好友*****************************/
        appUtil.SocketSendmsg(GetFriendListReq);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==R.id.BackBtn){
            Intent game_intent = new Intent(FriendActivity.this, GameActivity.class);
            startActivity(game_intent);
            Log.i(TAG,"切换到GameActivity");
            finish();
        }

    }

    /**定义BcastReceiver类用于接收广播并处理*/
    private class BcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String contentRcv = intent.getExtras().getString(SpeakOutResp);   //接收
            Log.i(TAG, "broadcast接收到:"+contentRcv);
            /**************************************处理广播信息***********************************************/
            try {
                String[] rcvstrs = ProcessString.splitstr(contentRcv);
                Log.i(TAG,contentRcv);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    @Override
    public void onDestroy() {
        unregisterReceiver(bcastReceiver);
        Log.i(TAG,"onDestroy");
        super.onDestroy();
    }
}