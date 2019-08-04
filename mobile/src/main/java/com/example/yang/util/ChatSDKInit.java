package com.example.yang.util;

import android.content.Context;

import cn.jpush.im.android.api.JMessageClient;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.util
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/6/8 20:03
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class ChatSDKInit {

    public ChatSDKInit(Context mcontext){
        JMessageClient.setDebugMode(true);
        JMessageClient.init(mcontext,false);
    }
}
