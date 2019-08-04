package com.example.yang.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.myapplication
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/5/17 22:43
 * @change
 * @chang time
 * @class describe
 * *****************************************************************/
public class MessageService extends Service {

    @Override
    public void onCreate() {
        //数据发送的基础配置

        super.onCreate();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void SendMessage(){

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
