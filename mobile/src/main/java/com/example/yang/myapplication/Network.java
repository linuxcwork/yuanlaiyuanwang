package com.example.yang.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;


/**
 * Created by yang on 2018/3/17.
 */

class Network {

    static Socket network;
    static HttpURLConnection urlconn;

    public Network(){
      try {
          network = new Socket("192.168.0.103",8000);
      }catch (UnknownHostException e){
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }

    }

    public Network(String urlname){
        try {
            urlconn = (HttpURLConnection) new URL(urlname).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void url_setflag(){
        urlconn.setDoOutput(true);
        try {
            urlconn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        urlconn.setUseCaches(false);
        urlconn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
    }

    public void url_getoutputstream() throws IOException {
        DataOutputStream dos = null;

            dos = new DataOutputStream(urlconn.getOutputStream());
            dos.write(Integer.parseInt("name="+ URLEncoder.encode("CHEJFJJJFI","gb2312")));

            dos.flush();
            dos.close();
    }

    public void url_setoutputstream() throws IOException {
        BufferedReader reaader = new BufferedReader(new InputStreamReader((urlconn.getInputStream())));
            reaader.readLine();
            reaader.close();
    }

    public boolean isNetworkConnect(Context context){
        if(context != null){
            ConnectivityManager mConnectiveManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectiveManager.getActiveNetworkInfo();
            if(mNetworkInfo != null){
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public int send_message(String clienttalk){
        if(network.isConnected()) {
            try {
                OutputStreamWriter recvfrom = new OutputStreamWriter(network.getOutputStream());
                BufferedWriter buffer = new BufferedWriter(recvfrom);
                PrintWriter ptser = new PrintWriter(buffer);
                ptser.println(clienttalk);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            /*showExitDialog01();*/
        }
        return 0;
    }

    public String recv_message(){
        String msg = null;
        try {
            DataInputStream sendto = new DataInputStream(network.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }


    /*private void showExitDialog01(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("网络未连接...")
                .setPositiveButton("确定", null)
                .show();
    }*/

    public int destrod_network(){

        return 0;
    }

}
