package com.example.login_ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_ui.util.ApplicationUtil;
import com.example.login_ui.util.ProcessString;

import static com.example.login_ui.util.Action.*;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RegisterActivity";

    private static TextView TipText;
    private EditText  NicknameEdit;
    private EditText AccountEdit;
    private EditText PasswordEdit;
    private EditText ConfirmPasswordEdit;
    private Button RegisterBtn;
    private Button BackBtn;
    private ApplicationUtil appUtil;  //新建application用来？？保存socket连接信息
    private BcastReceiver bcastReceiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.i(TAG,"onCreate");
        TipText = (TextView)findViewById(R.id.TipText);                      //提示信息text设置id
        NicknameEdit = (EditText)findViewById(R.id.NicknameEdit);              //用户名edit设置id
        NicknameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        AccountEdit = (EditText)findViewById(R.id.AccountEdit);              //账户edit设置id
        PasswordEdit = (EditText)findViewById(R.id.PasswordEdit);            //密码edit设置id
        ConfirmPasswordEdit = (EditText)findViewById(R.id.ConfirmPasswordEdit);//确认密码edit设置id
        RegisterBtn = (Button)findViewById(R.id.RegisterBtn);                //注册button设置id
        RegisterBtn.setOnClickListener(this);                                //登录button设置click事件listener
        BackBtn = (Button)findViewById(R.id.BackBtn);                        //忘记密码button设置id
        BackBtn.setOnClickListener(this);                                    //登录button设置click事件listener
        /****************************调用socket****************************/
        appUtil =  (ApplicationUtil) RegisterActivity.this.getApplication();    //设置为唯一application
        /*****************************接收广播******************************/
        bcastReceiver =  new BcastReceiver();
        filter = new IntentFilter();
        filter.addAction(SOCKETRCV_register);     //只有持有相同的action的接受者才能接收广播
        registerReceiver(bcastReceiver, filter);
        Log.i(TAG,"BroadCastReceiver succeed");
        Log.i(TAG,"succeed");

    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.BackBtn) {
            onClose();
        }
        if (v.getId()==R.id.RegisterBtn) {
            String Nicknamestr = String.valueOf(NicknameEdit.getText());
            String Accountstr = String.valueOf(AccountEdit.getText());
            String Passwordstr = String.valueOf(PasswordEdit.getText());
            String ConfirmPasswordstr = String.valueOf(ConfirmPasswordEdit.getText());
            String PasswordstrEncrypt = null;
            String msgstr= null;
            if(Accountstr.length()>5&&Accountstr.length()<15&&Passwordstr.length()>5&&Passwordstr.length()<15) {
                if(Passwordstr.equals(ConfirmPasswordstr))
                {
                    try {
                        PasswordstrEncrypt = ProcessString.encryptSHA256(Passwordstr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    msgstr=ProcessString.addstr(RegisterReq,Nicknamestr,Accountstr,PasswordstrEncrypt); //发送
                    appUtil.SocketSendmsg(msgstr);
                    TipText.setText("等待响应");
                }
                else
                {
                    Toast.makeText(this, "两次密码不相等", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "请输入6-14位的账号密码", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**定义BcastReceiver类用于接收广播并处理*/
    private class BcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String contentRcv = intent.getExtras().getString(RegisterResp);   //接收
            Log.i(TAG, "broadcast接收到:"+contentRcv);
            /**************************************处理广播信息***********************************************/
            try {
                String[] rcvstrs = ProcessString.splitstr(contentRcv);
                if(0==Integer.valueOf(rcvstrs[1]))
                {
                    /*弹窗提示*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("尊敬的用户");
                    builder.setMessage("恭喜你，注册成功");
                    builder.setPositiveButton("返回登录界面", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onClose();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();



                }
                else if(1==Integer.valueOf(rcvstrs[1]))
                {
                    Toast.makeText(context, "对不起，用户名已被占用，请重新输入", Toast.LENGTH_SHORT).show();
                }
                TipText.setText(contentRcv);
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
    public void onClose()
    {
/*        Intent login_intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(login_intent);*/
        Log.i(TAG, "切换到LoginActivity");
        finish();
    }
}