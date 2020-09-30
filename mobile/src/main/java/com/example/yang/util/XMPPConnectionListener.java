package com.example.yang.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class XMPPConnectionListener implements ConnectionListener {

    private final String TAG = "XMPPConnectionListener";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void connected(XMPPConnection connection) {
        Log.d(TAG,"connected");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void connectionClosed() {
        Log.d(TAG,"connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }
}
