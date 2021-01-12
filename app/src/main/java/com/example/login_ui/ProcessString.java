package com.example.login_ui;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * 字符处理 静态类
 */
public class ProcessString {

    //对字符串进行MD5加密
    public static String encryptMD5(String str) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(str.getBytes());
        BigInteger bigInteger = new BigInteger(1, bytes);//第一个参数是符号位，-1 表示负，0 表示零，1 表示正。
        String string = bigInteger.toString(16);//转为16进制
        return string;
    }

    /**
     * 利用java原生的类实现SHA256加密
     *
     * @param str 加密后的报文
     * @return
     */
    public static String encryptSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * 将数据，分隔符|和数据长度拼接成需要发送的字符串
     * @param oldstr
     * 包头指令
     * @param addstrs
     * 将要发送的数据（们）
     * @return
     */
    public static String addstr(String oldstr, String... addstrs) {
        String newstr = oldstr;
        for (int i = 0; i < addstrs.length; i++) {
            newstr = newstr + "|" + addstrs[i].length() + "|" + addstrs[i];
        }
        newstr=newstr+"\n";
        return newstr;
    }

    /**
     * 解出接收到的数据
     * @param str
     * @return 返回String数组
     * @throws Exception
     */
    public static String[] splitstr(String str) throws Exception
    {
//        String[] splitContent = content.split("\\|");   //分割成Action |length|data ...
        ArrayList<String> newlist = new ArrayList();
        int ch = str.indexOf("|");
        newlist.add(str.substring(0,ch));
        str = str.substring(ch+1);
        while(true)
        {
            ch = str.indexOf("|");
            int strlength = Integer.valueOf(str.substring(0,ch)).intValue();
            str=str.substring(ch+1);
            newlist.add( str.substring(0,strlength) );
            if (str.length()>strlength)
            {
                str=str.substring(strlength+1);
            }else{break;}
        }
        String [] newstr = new String[newlist.size()];
        for (int i =0 ; i<newstr.length;i++)
            newstr[i] = newlist.get(i);
        return newstr;
    }
}