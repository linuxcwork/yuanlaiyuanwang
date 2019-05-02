package com.example.yang.audio;

import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.asr.SpeechConstant;


import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by fujiayi on 2017/8/15.
 */

public class ActivityMiniWakeUp extends Activity implements EventListener {
    protected TextView txtLog;
    protected TextView txtResult;
    //protected Button btn;
    protected Button stopBtn;

    //private EventManager wakeup;

    private boolean logTime = true;

    /**
     * 测试参数填在这里
     */
    /*private void start() {
        txtLog.setText("");
        Map<String, Object> params = new TreeMap<String, Object>();

        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        String json = null; // 这里可以替换成你需要测试的json
        json = new JSONObject(params).toString();
        wakeup.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
        printLog("输入参数：" + json);
    }*/

    /*private void stop() {
        wakeup.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0); //
    }*/



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //wakeup.send(SpeechConstant.WAKEUP_STOP, "{}", null, 0, 0);
    }

    //   EventListener  回调方法
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        printLog(logTxt);
    }

    private void printLog(String text) {
        if (logTime) {
            text += "  ;time=" + System.currentTimeMillis();
        }
        text += "\n";
        Log.i(getClass().getName(), text);
        txtLog.append(text + "\n");
    }


}
