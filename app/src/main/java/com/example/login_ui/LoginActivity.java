package com.example.login_ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final String SOCKETRCV_ACT="Socketrcv_act";
    private static final String Loginstr="Login";
    private CheckBox RmbActCheckBox;
    private CheckBox RmbPswdCheckBox;
    private static TextView TipText;
    private EditText AccountEdit;
    private EditText PasswordEdit;
    private Button LoginBtn;
    private Button RegisterBtn;
    private Button ForgetPasswordBtn;

    private ApplicationUtil appUtil;  //新建application用来？？保存socket连接信息
    private BcastReceiver bcastReceiver;
    private IntentFilter filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RmbActCheckBox = (CheckBox)findViewById(R.id.RmbActCheckBox);        //记住账户checkbox设置id
        RmbPswdCheckBox = (CheckBox)findViewById(R.id.RmbPswdCheckBox);      //记住密码checkbox设置id
        TipText = (TextView)findViewById(R.id.TipText);                      //提示信息text设置id
        AccountEdit = (EditText)findViewById(R.id.AccountEdit);              //账户edit设置id
        PasswordEdit = (EditText)findViewById(R.id.PasswordEdit);            //密码edit设置id
        LoginBtn = (Button)findViewById(R.id.LoginBtn);                      //登录button设置id
        LoginBtn.setOnClickListener(this);                                   //登录button设置click事件listener
        RegisterBtn = (Button)findViewById(R.id.RegisterBtn);                //注册button设置id
        ForgetPasswordBtn = (Button)findViewById(R.id.ForgetPasswordBtn);    //忘记密码button设置id

        appUtil =  (ApplicationUtil) LoginActivity.this.getApplication();    //设置为唯一application

        bcastReceiver =  new  BcastReceiver();
        filter = new IntentFilter();
        filter.addAction(SOCKETRCV_ACT);     //只有持有相同的action的接受者才能接收广播
        registerReceiver(bcastReceiver, filter);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.LoginBtn)
        {
            appUtil.SocketSendmsg("826826");
            /*弹窗提示*/
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("尊敬的用户");
            builder.setMessage("密码错误");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alert = builder.create();
            alert.show();*/
        }

    }

/**定义BcastReceiver类用于接收广播并处理*/
    private class BcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context contsext, Intent intent) {
            String content = intent.getExtras().getString(Loginstr);
            /**处理广播信息*/

            
            Log.i("BcastReceiver.class", "broadcast接收到:"+content);
        }
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(bcastReceiver);
        super.onDestroy();
    }



}
