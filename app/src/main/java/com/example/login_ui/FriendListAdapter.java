package com.example.login_ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.example.login_ui.util.ApplicationUtil;
import com.example.login_ui.util.ProcessString;
import com.example.login_ui.util.UserBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.login_ui.util.Action.DeleteFriendReq;

public class FriendListAdapter extends SimpleAdapter {
    //上下文
    Context context;
    private ApplicationUtil appUtil;
    ArrayList<UserBean> userBeanList = null;
    public void setUserBeanList(ArrayList<UserBean> userBeanList) {
        this.userBeanList = userBeanList;
    }

    public void setAppUtil(ApplicationUtil appUtil) {
        this.appUtil = appUtil;
    }

    public FriendListAdapter(Context context,
                             List<? extends Map<String, ?>> data, int resource, String[] from,
                             int[] to  ) {
        super(context, data, resource, from, to);
        this.context = context;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = super.getView(i, convertView, viewGroup);
        Button deleteBtn = (Button) view.findViewById(R.id.deleteBtn);
        deleteBtn.setTag(i);//设置标签        从零开始
        deleteBtn.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*弹窗提示*/
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("waring");
                builder.setMessage("你确定要删除好友"+userBeanList.get(i).getNickName());             //array是从0开始
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msgstr = ProcessString.addstr(DeleteFriendReq,String.valueOf(userBeanList.get(i).getId()));
                        appUtil.SocketSendmsg(msgstr);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return view;
    }
}