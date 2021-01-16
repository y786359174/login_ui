package com.example.login_ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_ui.util.ApplicationUtil;
import com.example.login_ui.util.ProcessString;
import com.example.login_ui.util.UserBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.login_ui.util.Action.AddFriendReq;
import static com.example.login_ui.util.Action.AddFriendResp;
import static com.example.login_ui.util.Action.ApplyFriendResp;
import static com.example.login_ui.util.Action.DeleteFriendResp;
import static com.example.login_ui.util.Action.GetApplyListReq;
import static com.example.login_ui.util.Action.GetApplyListResp;
import static com.example.login_ui.util.Action.GetFriendListReq;
import static com.example.login_ui.util.Action.GetFriendListResp;
import static com.example.login_ui.util.Action.SOCKETRCV_friend;

public class FriendActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "FriendActivity";
    private final Context mContext = this ;
    private Button BackBtn ;
    private Button AddFriendBtn;
    private Button FriendBtn;
    private Button ApplyBtn;
    private ApplicationUtil appUtil;  //新建application用来？？保存socket连接信息
    private BcastReceiver bcastReceiver;
    private ListView friendListview;
    private int IsFirendList = 1;
    ArrayList<UserBean> userBeanList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Log.i(TAG,"onCreate");
        BackBtn =  (Button)findViewById(R.id.BackBtn);
        BackBtn.setOnClickListener(this);                                   //添加好友button设置click事件listener
        AddFriendBtn =  (Button)findViewById(R.id.AddFriendBtn);
        AddFriendBtn.setOnClickListener(this);                                   //返回button设置click事件listener
        FriendBtn =  (Button)findViewById(R.id.FriendBtn);
        FriendBtn.setOnClickListener(this);                                   //返回button设置click事件listener
        ApplyBtn =  (Button)findViewById(R.id.ApplyBtn);
        ApplyBtn.setOnClickListener(this);                                   //返回button设置click事件listener
        /****************************调用socket****************************/
        appUtil =  (ApplicationUtil) this.getApplication();    //设置为唯一application
        /*****************************接收广播******************************/
        bcastReceiver =  new BcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SOCKETRCV_friend);     //只有持有相同的action的接受者才能接收广播
        registerReceiver(bcastReceiver, filter);
        Log.i(TAG,"BroadCastReceiver succeed");
        /******************************加载好友list控件*********************/
        friendListview = (ListView) findViewById(R.id.friendListview);

        /******************************刷新好友*****************************/
        appUtil.SocketSendmsg(GetFriendListReq);

    }

    /**
     * 加载好友list控件
     */
    private void freshList()
    {
        switch (IsFirendList)
        {
            case 1 :
                FriendListAdapter friendListAdapter = new FriendListAdapter(mContext, getData(), R.layout.fragment_friend,
                        new String[]{"image", "name"},
                        new int[]{R.id.FriendIconView, R.id.friendNameTextView});
                friendListAdapter.setUserBeanList(userBeanList);
                friendListAdapter.setAppUtil(appUtil);
                friendListview.setAdapter(friendListAdapter);
                friendListview.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(FriendActivity.this, "我是item1点击事件 i = " + i + "l = " + l, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2 :
                ApplyListAdapter applyListAdapter = new ApplyListAdapter(mContext, getData(), R.layout.fragment_apply,
                        new String[]{"image", "name"},
                        new int[]{R.id.FriendIconView, R.id.friendNameTextView});
                applyListAdapter.setUserBeanList(userBeanList);
                applyListAdapter.setAppUtil(appUtil);
                friendListview.setAdapter(applyListAdapter);
                friendListview.setOnItemClickListener(new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(FriendActivity.this, "我是item2点击事件 i = " + i + "l = " + l, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

    }

    private ArrayList<Map<String,Object>> getData() {
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for(int i = 0 ; i < userBeanList.size() ; i ++)
        {
            map = new HashMap<String, Object>();
            map.put("image", R.drawable.iconv0_1);
            map.put("name", userBeanList.get(i).getNickName());
            data.add(map);
        }
        return data;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() ==R.id.BackBtn){
            Intent game_intent = new Intent(FriendActivity.this, GameActivity.class);
            startActivity(game_intent);
            Log.i(TAG,"切换到GameActivity");
            finish();
        }
        if(v.getId() ==R.id.AddFriendBtn){
            EditText AddFriendEditText = new EditText(this);
            AddFriendEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(this).setTitle("请输入好友ID(可省略第一个1)").setView(AddFriendEditText
                    ).setPositiveButton("确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int userid = Integer.valueOf(AddFriendEditText.getText().toString());
                    if(userid==appUtil.playerBean.getId()||userid+10000000==appUtil.playerBean.getId())
                        Toast.makeText(FriendActivity.this,"申请好友不能是自己哦",Toast.LENGTH_SHORT).show();
                    else
                    {
                        String msgstr = ProcessString.addstr(AddFriendReq, AddFriendEditText.getText().toString());
                        appUtil.SocketSendmsg(msgstr);
                    }

                }
                    })
                    .setNegativeButton("取消", null).show();
        }
        if(v.getId() ==R.id.FriendBtn) {
            IsFirendList =1;
            FriendBtn.setBackgroundColor(Color.parseColor("#711232"));
            FriendBtn.setTextColor(Color.parseColor("#FFFFFF"));
            ApplyBtn.setBackgroundColor(Color.parseColor("#FFC2AF"));
            ApplyBtn.setTextColor(Color.parseColor("#000000"));
            appUtil.SocketSendmsg(GetFriendListReq);
        }
        if(v.getId() ==R.id.ApplyBtn) {
            IsFirendList =2;
            FriendBtn.setBackgroundColor(Color.parseColor("#FFC2AF"));
            FriendBtn.setTextColor(Color.parseColor("#000000"));
            ApplyBtn.setBackgroundColor(Color.parseColor("#711232"));
            ApplyBtn.setTextColor(Color.parseColor("#FFFFFF"));
            appUtil.SocketSendmsg(GetApplyListReq);
        }
    }

    /**定义BcastReceiver类用于接收广播并处理*/
    private class BcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /**************************************获取好友列表***********************************************/
            String contentRcv = intent.getExtras().getString(GetFriendListResp);   //接收
            if(contentRcv!=null) {
                Log.i(TAG, "broadcast接收---" + contentRcv);

                try {
                    String[] rcvstrs = ProcessString.splitstr(contentRcv);
                    userBeanList = new ArrayList<UserBean>();

                    for (int i = 1; i < rcvstrs.length; i = i + 2) {
                        UserBean userBean = new UserBean();
                        userBean.setId(Integer.valueOf(rcvstrs[i]));//rs.getInt(1)，获取表第一列
                        userBean.setNickName(rcvstrs[i + 1]);
                        userBeanList.add(userBean);
//                        Log.i(TAG, "userBeanList.add---" + rcvstrs[i + 1]);
                    }
                    freshList();

                    Log.i(TAG, contentRcv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /**************************************删除好友响应***********************************************/
            contentRcv = intent.getExtras().getString(DeleteFriendResp);   //接收
            if(contentRcv!=null) {
                Log.i(TAG, "broadcast接收---" + contentRcv);

                try {
                    String[] rcvstrs = ProcessString.splitstr(contentRcv);

                    if(0==Integer.valueOf(rcvstrs[1]))
                        Toast.makeText(FriendActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(FriendActivity.this,"删除异常",Toast.LENGTH_SHORT).show();
                    appUtil.SocketSendmsg(GetFriendListReq);
                    Log.i(TAG, contentRcv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /***************************************申请好友响应************************************************/
            contentRcv = intent.getExtras().getString(AddFriendResp);   //接收
            if(contentRcv!=null) {
                Log.i(TAG, "broadcast接收---" + contentRcv);

                try {
                    String[] rcvstrs = ProcessString.splitstr(contentRcv);
                    switch (Integer.valueOf(rcvstrs[1]))//0申请成功，1没有这个人，2已经是好友了，3直接添加上了，4未知的错误
                    {
                        case 0:
                            Toast.makeText(FriendActivity.this,"申请好友"+rcvstrs[2]+"成功",Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(FriendActivity.this,"没找到此用户",Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(FriendActivity.this,rcvstrs[2]+"已经是你的好友啦",Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(FriendActivity.this,rcvstrs[2]+"也想添加你为好友，你们现在是好友啦",Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(FriendActivity.this,"未知的错误",Toast.LENGTH_SHORT).show();
                            break;

                    }

                    Log.i(TAG, contentRcv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /**************************************获取申请列表***********************************************/
            contentRcv = intent.getExtras().getString(GetApplyListResp);   //接收
            if(contentRcv!=null) {
                Log.i(TAG, "broadcast接收---" + contentRcv);

                try {
                    String[] rcvstrs = ProcessString.splitstr(contentRcv);
                    userBeanList = new ArrayList<UserBean>();

                    for (int i = 1; i < rcvstrs.length; i = i + 2) {
                        UserBean userBean = new UserBean();
                        userBean.setId(Integer.valueOf(rcvstrs[i]));//rs.getInt(1)，获取表第一列
                        userBean.setNickName(rcvstrs[i + 1]);
                        userBeanList.add(userBean);
                    }
                    freshList();

                    Log.i(TAG, contentRcv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /**************************************同意/拒绝申请***********************************************/
            contentRcv = intent.getExtras().getString(ApplyFriendResp);   //接收
            if(contentRcv!=null) {
                Log.i(TAG, "broadcast接收---" + contentRcv);

                try {
                    String[] rcvstrs = ProcessString.splitstr(contentRcv);
                    switch (Integer.valueOf(rcvstrs[1]))
                    {
                        case 0:
                            Toast.makeText(FriendActivity.this,"操作成功",Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(FriendActivity.this,"操作异常",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    appUtil.SocketSendmsg(GetApplyListReq);
                    Log.i(TAG, contentRcv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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