package com.example.login_ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_ui.util.ApplicationUtil;
import com.example.login_ui.util.DateUtil;
import com.example.login_ui.util.ProcessString;
import static com.example.login_ui.util.Action.*;



public class GameActivity extends AppCompatActivity implements
        View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "GameActivity";
    private TextView SpeakOutText;
    private Button SendMsgBtn;
    private EditText SpeakOutEdit;
    private Button FriendBtn;
    private ApplicationUtil appUtil;  //新建application用来？？保存socket连接信息
    private BcastReceiver bcastReceiver;
    private IntentFilter filter;
//    private boolean Isrefresh = true;  //不会监听textview滚动
/*    private RelativeLayout FragmentContainerLayout;
    private InviteGameFragment inviteGameFragment;//弹出邀请碎片
    private FragmentTransaction fragmentTransaction;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.i(TAG,"onCreate");
        SpeakOutText = (TextView) findViewById(R.id.SpeakOutText);
        SpeakOutText.setOnLongClickListener(this);                                   //登录button设置长按
        SpeakOutText.setGravity(Gravity.LEFT|Gravity.BOTTOM);                         //???
        SpeakOutText.setLines(8);                                                     //???
        SpeakOutText.setMaxLines(8);
        SpeakOutText.setMovementMethod(new ScrollingMovementMethod());
        SendMsgBtn = (Button)findViewById(R.id.SendMsgBtn);
        SendMsgBtn.setOnClickListener(this);                                   //发信button设置click事件listener
        SpeakOutEdit = (EditText)findViewById(R.id.SpeakOutEdit);
        FriendBtn = (Button)findViewById(R.id.FriendBtn);
        FriendBtn.setOnClickListener(this);                                   //好友button设置click事件listener
        /****************************调用socket****************************/
        appUtil =  (ApplicationUtil) GameActivity.this.getApplication();    //设置为唯一application
        /*****************************接收广播******************************/
        bcastReceiver =  new BcastReceiver();
        filter = new IntentFilter();
        filter.addAction(SOCKETRCV_game);     //只有持有相同的action的接受者才能接收广播
        registerReceiver(bcastReceiver, filter);
        Log.i(TAG,"BroadCastReceiver succeed");
//        /*****************************提前加载Friend界面***************************/
//        Intent game_intent = new Intent(GameActivity.this, FriendActivity.class);       //提前加载Friend界面//不用提前加载
//        startActivity(game_intent);
//        /*****************************创建邀请碎片***************************/
/*        inviteGameFragment = new InviteGameFragment();
        FragmentContainerLayout = (RelativeLayout)findViewById(R.id.FragmentContainer);
        FragmentContainerLayout.bringToFront();

        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.FragmentContainer,inviteGameFragment);
        fragmentTransaction.hide(inviteGameFragment);
        fragmentTransaction.commit();

//        fragmentTransaction.show(inviteGameFragment);
//        fragmentTransaction.hide(inviteGameFragment);*/
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.SendMsgBtn) {
            String sendmsgstr = SpeakOutEdit.getText().toString();
            if(!sendmsgstr.equals("")) {
                String msgstr = ProcessString.addstr(SpeakOutReq,appUtil.playerBean.getNickName(),sendmsgstr);
                appUtil.SocketSendmsg(msgstr);
                SpeakOutEdit.setText("");
//                Isrefresh = true;
                refreshSpeakOutText();
            }
        }

        if(v.getId() ==R.id.FriendBtn)
        {
        Intent game_intent = new Intent(GameActivity.this, FriendActivity.class);       //提前加载Friend界面//不用提前加载
        startActivity(game_intent);
            Log.i(TAG, "切换到FriendActivity");
        }

    }
    public void refreshSpeakOutText() {
        int offset = SpeakOutText.getLineCount() * SpeakOutText.getLineHeight();
            if (offset > SpeakOutText.getHeight()) {
             SpeakOutText.scrollTo(0, offset - 8 * SpeakOutText.getLineHeight());

        }
    }
    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.SpeakOutText) {
            SpeakOutText.setText("");
        }
        return true;
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
                String newStr = String.format("%s\n(%s)%s:%s",
                SpeakOutText.getText().toString(), DateUtil.getNowTime(),rcvstrs[1],rcvstrs[2]);
                SpeakOutText.setText(newStr);
//                if(Isrefresh)
                    refreshSpeakOutText();
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

