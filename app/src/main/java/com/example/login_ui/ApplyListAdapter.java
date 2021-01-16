package com.example.login_ui;

import android.content.Context;
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

import static com.example.login_ui.util.Action.ApplyFriendReq;

public class ApplyListAdapter extends SimpleAdapter {
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

    public ApplyListAdapter(Context context,
                             List<? extends Map<String, ?>> data, int resource, String[] from,
                             int[] to  ) {
        super(context, data, resource, from, to);
        this.context = context;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = super.getView(i, convertView, viewGroup);
        Button permitBtn = (Button) view.findViewById(R.id.permitBtn);
        permitBtn.setTag(i);//设置标签        从零开始
        permitBtn.setOnClickListener(new android.view.View.OnClickListener() {  //同意
            @Override
            public void onClick(View v) {
                String msgstr = ProcessString.addstr(ApplyFriendReq,String.valueOf(userBeanList.get(i).getId()),"0");
                appUtil.SocketSendmsg(msgstr);
            }
        });

        Button defuseBtn = (Button) view.findViewById(R.id.defuseBtn);
        defuseBtn.setTag(i);//设置标签        从零开始
        defuseBtn.setOnClickListener(new android.view.View.OnClickListener() {  //拒绝
            @Override
            public void onClick(View v) {
                String msgstr = ProcessString.addstr(ApplyFriendReq,String.valueOf(userBeanList.get(i).getId()),"1");
                appUtil.SocketSendmsg(msgstr);
            }
        });
        return view;
    }
}