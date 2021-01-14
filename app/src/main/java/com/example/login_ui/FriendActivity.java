package com.example.login_ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_ui.util.ApplicationUtil;
import com.example.login_ui.util.ProcessString;
import com.example.login_ui.util.UserBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.login_ui.util.Action.*;

public class FriendActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "FriendActivity";
    private final Context mContext = this ;
    private Button BackBtn ;
    private ApplicationUtil appUtil;  //新建application用来？？保存socket连接信息
    private BcastReceiver bcastReceiver;
    private ListView friendListview;
    ArrayList<UserBean> userBeanList = null;
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
    private void freshFriendList()
    {
        FriendListAdapter adapter = new FriendListAdapter(mContext,getData(),R.layout.fragment_friend,
                new String[]{"image", "name"},
                new int[]{R.id.FriendIconView, R.id.friendNameTextView});
        adapter.setUserBeanList(userBeanList);
        adapter.setAppUtil(appUtil);
        friendListview.setAdapter(adapter);
        friendListview.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(FriendActivity.this,"我是item点击事件 i = " + i + "l = " + l,Toast.LENGTH_SHORT).show();
            }
        });
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
                        Log.i(TAG, "userBeanList.add---" + rcvstrs[i + 1]);
                    }
                    freshFriendList();

                    Log.i(TAG, contentRcv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /**************************************删除好友响应***********************************************/
            contentRcv = intent.getExtras().getString(DeleteFriendListResp);   //接收
            if(contentRcv!=null) {
                Log.i(TAG, "broadcast接收---" + contentRcv);

                try {
                    String[] rcvstrs = ProcessString.splitstr(contentRcv);

                    if(0==Integer.valueOf(rcvstrs[1]))
                    {
                        Toast.makeText(FriendActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                        appUtil.SocketSendmsg(GetFriendListReq);
                    }
                    else
                        Toast.makeText(FriendActivity.this,"删除异常",Toast.LENGTH_SHORT).show();
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