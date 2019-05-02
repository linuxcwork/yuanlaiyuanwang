package com.example.yang.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.util
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/5/1 13:13
 * @change
 * @chang time
 * @class describe 短信处理类，发送
 *****************************************************************/
public class SMSCore {
    public static final String ACTION_SMS_SEND = "lab.sodino.sms.send";
    public static final String ACTION_SMS_DELIVERY = "lab.sodino.sms.delivery";
    public static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";
    public static String PhoneNumber="";
    //==============Get Phone Number ==============================

    //get the phone number from sms
    public static String GetPhoneNumberFromSMSText(String sms){

        List<String> list=GetNumberInString(sms);
        for(String str:list){
            if(str.length()==11)
                return str;
        }
        return "";
    }
    public static List<String> GetNumberInString(String str){
        List<String> list=new ArrayList<String>();
        String regex = "\\d*";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(str);

        while (m.find()) {
            if (!"".equals(m.group()))
                list.add(m.group());
        }
        return list;
    }

    //===========================================================
    //
    //
    //=============Send SMS================
    public  void SendSMS(String number,String text,Context context){

        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(context, context.getClass()), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, text, pi, null);

    }

    public void SendSMS2(String number,String text,Context context){
        String SENT = "sms_sent";
        String DELIVERED = "sms_delivered";
        PendingIntent sentPI = PendingIntent.getActivity(context, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getActivity(context, 0, new Intent(DELIVERED), 0);
//
//          //
//           registerReceiver(new BroadcastReceiver(){
//                  @Override
//                  public void onReceive(Context context, Intent intent){
//                      switch(getResultCode())
//                      {
//                          case Activity.RESULT_OK:
//                              Log.i("====>", "RESULT_OK");
//                              System.out.println("RESULT_OK");
//
//                              break;
//                          case Activity.RESULT_CANCELED:
//                              Log.i("=====>", "RESULT_CANCELED");
//                              break;
//                      }
//                  }
//              }, new IntentFilter(DELIVERED));
//            //

        SmsManager smsm = SmsManager.getDefault();
        smsm.sendTextMessage(number, null, text, sentPI, deliveredPI);
    }

    //=====================================
}
