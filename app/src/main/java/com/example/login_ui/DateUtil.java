package com.example.login_ui;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ouyangshen on 2016/9/14.
 */
public class DateUtil {
    @SuppressLint("SimpleDateFormat")
    public static String getNowDateTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("yyyyMMddhhmmss");
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss");
        return s_format.format(new Date());
    }

}
