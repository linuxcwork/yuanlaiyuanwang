package com.example.yang.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.util
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/5/1 13:12
 * @change
 * @chang time
 * @class describe  短信监听
 *****************************************************************/
public class SMS_Receiver extends BroadcastReceiver {
    final String GetNumberAddress="10001";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){

            Object[] pdus=(Object[])intent.getExtras().get("pdus");
            //不知道为什么明明只有一条消息，传过来的却是数组，也许是为了处理同时同分同秒同毫秒收到多条短信
            //但这个概率有点小
            SmsMessage[] message=new SmsMessage[pdus.length];
            StringBuilder sb=new StringBuilder();
            System.out.println("pdus长度"+pdus.length);
            String address="";
            for(int i=0;i<pdus.length;i++){
                //虽然是循环，其实pdus长度一般都是1
                message[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
                sb.append("接收到短信来自:\n");
                address=message[i].getDisplayOriginatingAddress();
                sb.append(address+"\n");
                sb.append("内容:"+message[i].getDisplayMessageBody());
            }
            System.out.println(sb.toString());
            if(SMSCore.PhoneNumber==""&&address.equals(GetNumberAddress)){
                SMSCore.PhoneNumber=SMSCore.GetPhoneNumberFromSMSText(sb.toString());
            }
        }
    }
}
