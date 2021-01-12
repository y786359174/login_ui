package com.example.login_ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.login_ui.Action.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
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

//    private SharedPreferences mShared=this.getSharedPreferences("LoginShared",Context.MODE_PRIVATE);//创建共享参数，文件名LoginShared，私有模式//放在Main里初始化
    private boolean IsRmbAccount = false;
    private boolean IsRmbPassword = false;
    private boolean IsChangePassword = false;
    String Accountstr = "";
    private String PasswordstrEncrypt = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RmbActCheckBox = (CheckBox)findViewById(R.id.RmbActCheckBox);        //记住账户checkbox设置id
        RmbActCheckBox.setOnClickListener(this);                                   //登录CheckBox设置click事件listener
        RmbPswdCheckBox = (CheckBox)findViewById(R.id.RmbPswdCheckBox);      //记住密码checkbox设置id
        RmbPswdCheckBox.setOnClickListener(this);                                   //登录CheckBox设置click事件listener
        TipText = (TextView)findViewById(R.id.TipText);                      //提示信息text设置id
        AccountEdit = (EditText)findViewById(R.id.AccountEdit);              //账户edit设置id

        PasswordEdit = (EditText)findViewById(R.id.PasswordEdit);            //密码edit设置id

        LoginBtn = (Button)findViewById(R.id.LoginBtn);                      //登录button设置id
        LoginBtn.setOnClickListener(this);                                   //登录button设置click事件listener
        RegisterBtn = (Button)findViewById(R.id.RegisterBtn);                //注册button设置id
        RegisterBtn.setOnClickListener(this);                                   //登录button设置click事件listener
        ForgetPasswordBtn = (Button)findViewById(R.id.ForgetPasswordBtn);    //忘记密码button设置id
        ForgetPasswordBtn.setOnClickListener(this);                                   //登录button设置click事件listener
        /****************************调用socket****************************/
        appUtil =  (ApplicationUtil) LoginActivity.this.getApplication();    //设置为唯一application
        /*****************************接收广播******************************/
        bcastReceiver =  new  BcastReceiver();
        filter = new IntentFilter();
        filter.addAction(SOCKETRCV_login);     //只有持有相同的action的接受者才能接收广播
        registerReceiver(bcastReceiver, filter);
        Log.i(TAG,"BroadCastReceiver succeed");
        /******************************重载账号密码*************************/
        Accountstr = appUtil.mSharedGetString("Accountstr","");   //共享参数获取账号
        IsRmbAccount=!Accountstr.equals("");                                    //判断是否有，有则赋值IsRmbAccount=1
        RmbActCheckBox.setChecked(IsRmbAccount);                                //同步记住账号CheckBox
        AccountEdit.setText(Accountstr);                                        //同步账号Edit
        PasswordstrEncrypt = appUtil.mSharedGetString("PasswordstrEncrypt","");//共享参数获取密码md5码
        IsRmbPassword=!PasswordstrEncrypt.equals("");                           //判断是否有，有则赋值IsRmbPassword=1
        if(IsRmbPassword) {                                                     //若记住密码

            RmbPswdCheckBox.setChecked(IsRmbPassword);                          //同步记住密码CheckBox
            PasswordEdit.setText("$$$$$$$");                                     //密码随机设置
        }
        /**在修改之后设置监听*/
        EditTextChangeListener EditTextAccountChangeListener = new EditTextChangeListener(R.id.AccountEdit);   //定义edittext变化监听
        AccountEdit.addTextChangedListener(EditTextAccountChangeListener);                                     //将EditTextPasswordChangeListener绑上
        EditTextChangeListener EditTextPasswordChangeListener = new EditTextChangeListener(R.id.PasswordEdit);  //定义edittext变化监听
        PasswordEdit.addTextChangedListener(EditTextPasswordChangeListener);                                     //将EditTextPasswordChangeListener绑上
        Log.i(TAG,"Account&Password init succeed!");

    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.LoginBtn)
        {
            String Accountstr =String.valueOf(AccountEdit.getText());
            String Passwordstr = String.valueOf(PasswordEdit.getText());
            String msgstr="";
            if(Accountstr.length()>5&&Accountstr.length()<15&&Passwordstr.length()>5&&Passwordstr.length()<15) {
                if(IsChangePassword)
                    try {
                        PasswordstrEncrypt = ProcessString.encryptSHA256(Passwordstr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                msgstr=ProcessString.addstr(LoginReq,Accountstr,PasswordstrEncrypt); //发送
                appUtil.SocketSendmsg(msgstr);
//                Log.i(TAG,"SocketSendmsg---"+msgstr);

                /***************************保存账号******************************/
                if (!IsRmbAccount)
                    Accountstr ="";
                /***************************保存密码******************************/
                if (!IsRmbPassword)
                    PasswordstrEncrypt ="";

                appUtil.mSharedSetActPswd(Accountstr,PasswordstrEncrypt);
                TipText.setText("等待响应");
//                Log.i(TAG,"mSharedSetActPswd---"+Accountstr+"---"+PasswordstrEncrypt);
            }
            else {
                /*弹窗提示*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("尊敬的用户");
            builder.setMessage("请输入6-14位的账号密码");
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            AlertDialog alert = builder.create();
            alert.show();
            }
        }
        if(v.getId()==R.id.RmbActCheckBox) //记住账号checkbox
        {
            IsRmbAccount=RmbActCheckBox.isChecked();
            Log.i(TAG,"RmbActCheckBox---"+IsRmbAccount);
            if(!IsRmbAccount)
            {
                RmbPswdCheckBox.setChecked(IsRmbAccount);
                IsRmbPassword=RmbPswdCheckBox.isChecked();
            }
        }
        if(v.getId()==R.id.RmbPswdCheckBox) //记住密码checkbox
        {
            IsRmbPassword=RmbPswdCheckBox.isChecked();
            Log.i(TAG,"RmbPswdCheckBox---"+IsRmbPassword);
            if(IsRmbPassword)
            {
                RmbActCheckBox.setChecked(IsRmbPassword);
                IsRmbAccount=RmbActCheckBox.isChecked();
            }
        }
        if(v.getId()==R.id.RegisterBtn) //注册
        {
            Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(register_intent);
            Log.i(TAG, "切换到RegisterActivity");
       }
        if(v.getId()==R.id.ForgetPasswordBtn) //忘记密码
        {

        }

    }


    /**
     * 监听edittext变化
     */
    public class EditTextChangeListener implements TextWatcher {
        int id ;
        public EditTextChangeListener(int id)
        {
            this.id=id;
        }
        /**
         * 编辑框的内容发生改变之前的回调方法
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
/*            Log.i(TAG, "beforeTextChanged---" + charSequence.toString());*/
        }

        /**
         * 编辑框的内容正在发生改变时的回调方法 >>用户正在输入
         * 我们可以在这里实时地 通过搜索匹配用户的输入
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.i(TAG, "onTextChanged---" + charSequence.toString());
            if(R.id.AccountEdit==id)//账号修改时，密码消失
            {
                if(!String.valueOf(PasswordEdit.getText()).equals(""))
                PasswordEdit.setText("");
            }
            else if(R.id.PasswordEdit==id)
            {
                if(!IsChangePassword)
                {
                    IsChangePassword=true;
                    PasswordEdit.setText("");
                }
            }
        }

        /**
         * 编辑框的内容改变以后,用户没有继续输入时 的回调方法
         */
        @Override
        public void afterTextChanged(Editable editable) {
/*            Log.i(TAG, "afterTextChanged");*/
        }
    }

    /**定义BcastReceiver类用于接收广播并处理*/
    private class BcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String contentRcv = intent.getExtras().getString(LoginResp);   //接收
            Log.i(TAG, "broadcast接收到:"+contentRcv);
            /**************************************处理广播信息***********************************************/
            try {
                String[] rcvstrs = ProcessString.splitstr(contentRcv);
                if(0==Integer.valueOf(rcvstrs[1]))                      //登录成功
                {
                    appUtil.playerBean.setId(Long.valueOf(rcvstrs[2]));
                    appUtil.playerBean.setNickName(rcvstrs[3]);
                    Intent game_intent = new Intent(LoginActivity.this, GameActivity.class);
                    startActivity(game_intent);
                    Log.i(TAG, "切换到GameActivity");
                    finish();
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
        super.onDestroy();
    }

}
