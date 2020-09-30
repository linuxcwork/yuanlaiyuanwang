package com.example.yang.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.sqlite_linkmanmss;
import com.example.yang.util.SharedPreferencedUtils;
import com.example.yang.util.RTCGenerateUserSig;
import com.example.yang.util.XmppConnection;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;

import org.jxmpp.stringprep.XmppStringprepException;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import static com.tencent.trtc.TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL;

public class ChatRTCVoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private TRTCCloud trtcCloud;              /// TRTC SDK 实例对象
    private TRTCCloudListener trtcListener;    /// TRTC SDK 回调监听
    private TRTCCloudDef.TRTCParams             trtcParams;                // 进房参数

    public static String CHATRTCVOICE = "com.example.yang.rtcvoice";

    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();

    private int SDKAppid = 1400281617;
    private String userId;
    private int roomId;
    private boolean isspeaker = true;
    private boolean ismute = true;
    private TextView talktime;
    private static TextView talkstatus;
    private static Timer timer;

    private XmppConnection xmppConnection;
    private LocalBroadcastManager localBroadcastManager;
    private RTCVoiceStateReceiver msgUpadteReceiver;

    private static long time = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                //回到主线程执行结束操作
                long second = time;
                long hours = second / 3600;//转换小时数
                second = second % 3600;//剩余秒数
                long minutes = second / 60;//转换分钟
                second = second % 60;//剩余秒数
                String timetext = null;
                if(hours != 0){
                    timetext = unitFormat(hours)+":"+unitFormat(minutes)+":"+unitFormat(second);
                    talktime.setText(timetext);
                }else {
                    timetext = unitFormat(minutes)+":"+unitFormat(second);
                    talktime.setText(timetext);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_rtc_voice_xml);
        initView();

        Intent intent = getIntent();
        userId = intent.getStringExtra("mapresource");
        roomId = intent.getIntExtra("roomid",100);
        time = 0;

        trtcListener = new TRTCCloudListenerImpl(this);
        trtcCloud = TRTCCloud.sharedInstance(this);
        trtcCloud.setListener(trtcListener);

        timer = new Timer(true);
        timer.schedule(timerTask, 0, 1000); //延时1000ms后执行，1000ms执行一次
        xmppConnection = XmppConnection.getInstance();

        //广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CHATRTCVOICE);
        msgUpadteReceiver = new RTCVoiceStateReceiver();
        //注册广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //本地广播管理器注册广播接收器
        localBroadcastManager.registerReceiver(msgUpadteReceiver, intentFilter);
        enterRoom();
    }

    private class RTCVoiceStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是当前用户，则结束通话
            String name = intent.getStringExtra("account");
            if(name != null && name.equals(userId)){
                exitRoom();
                finish();
            }
        }
    }

    public void initView(){
        ImageView avatarimg = findViewById(R.id.chat_rtc_voice_initiate_avatar_img);
        Glide
                .with(ChatRTCVoiceActivity.this)
                .load(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"headsculpture",null))
                .dontAnimate()
                .centerCrop()
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(avatarimg);
        TextView nickname = findViewById(R.id.chat_rtc_voice_initiate_textUserName);
        nickname.setText(sharedPreferencedUtils.getUserInfo(this, "stagename","null"));
        LinearLayout mute = findViewById(R.id.chat_rtc_voice_initiate_mute_layout);
        mute.setOnClickListener(this);
        LinearLayout handup = findViewById(R.id.chat_rtc_voice_initiate_hang_up_layout);
        handup.setOnClickListener(this);
        LinearLayout handfree = findViewById(R.id.chat_rtc_voice_initiate_hands_free_layout);
        handfree.setOnClickListener(this);
        talktime = findViewById(R.id.chat_rtc_voice_initiate_textTime);
        talkstatus = findViewById(R.id.chat_rtc_voice_initiate_textstatue);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_rtc_voice_initiate_mute_layout:
                enableAudioMute(ismute);
                break;
            case R.id.chat_rtc_voice_initiate_hang_up_layout:
                exitRoom();
                try {
                    xmppConnection.sendMessages(userId,"通话结束",sqlite_linkmanmss.KEY_MESSAGE_TYPE_FINESH_CALL);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case R.id.chat_rtc_voice_initiate_hands_free_layout:
                enableAudioHandFree(isspeaker);
                break;
        }
    }

    private void enterRoom() {

        // 预览前配置默认参数
        setTRTCCloudParam();

        //TRTCParams 定义参考头文件TRTCCloudDef.java
        trtcParams = new TRTCCloudDef.TRTCParams();
        trtcParams.sdkAppId = SDKAppid;
        trtcParams.userId   = userId;
        trtcParams.userSig  = RTCGenerateUserSig.genTestUserSig(userId);
        trtcParams.roomId   = 908; //输入你想进入的房间
        trtcCloud.enterRoom(trtcParams, TRTC_APP_SCENE_VIDEOCALL);
    }

    private void exitRoom() {
        if (trtcCloud != null) {
            trtcCloud.exitRoom();
        }
        /*ChatRTCVoiceActivity activity = ;
        if (activity != null) {
            activity.finishActivity();
        }*/
    }

    /**
     * 是否静音
     *
     * @param bEnable
     */
    private void enableAudioMute(boolean bEnable) {
        if (bEnable) {
            ismute = false;
        } else {
            ismute = true;
        }
        trtcCloud.muteLocalAudio(bEnable);
    }

    /**
     * 是否开启免提
     *
     * @param bEnable
     */
    private void enableAudioHandFree(boolean bEnable) {
        if (bEnable) {
            isspeaker = false;
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
        } else {
            isspeaker = true;
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
        }
    }

    void setTRTCCloudParam() {
        // 大画面的编码器参数设置
        // 设置视频编码参数，包括分辨率、帧率、码率等等，这些编码参数来自于 TRTCSettingDialog 的设置
        // 注意（1）：不要在码率很低的情况下设置很高的分辨率，会出现较大的马赛克
        // 注意（2）：不要设置超过25FPS以上的帧率，因为电影才使用24FPS，我们一般推荐15FPS，这样能将更多的码率分配给画质
/*        TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
        encParam.videoResolution = settingDlg.getResolution();
        encParam.videoFps = settingDlg.getVideoFps();
        encParam.videoBitrate = settingDlg.getVideoBitrate();
        encParam.videoResolutionMode = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT;
        trtcCloud.setVideoEncoderParam(encParam);*/

        // 不开启视频采集预览
       /* TXCloudVideoView localVideoView = mVideoViewLayout.getCloudVideoViewByIndex(0);
        localVideoView.setUserId(trtcParams.userId);
        localVideoView.setVisibility(View.VISIBLE);
        trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
        trtcCloud.startLocalPreview(true, localVideoView);*/
        //只开启音频采集
        trtcCloud.startLocalAudio();
        trtcCloud.setGSensorMode(TRTCCloudDef.TRTC_GSENSOR_MODE_UIFIXLAYOUT);
    }

    //静态内部类，重写监听事件
    static class TRTCCloudListenerImpl extends TRTCCloudListener {

        private WeakReference<ChatRTCVoiceActivity> mContext;
        public TRTCCloudListenerImpl(ChatRTCVoiceActivity activity) {
            super();
            mContext = new WeakReference<>(activity);
        }
        @Override
        public void onEnterRoom(long elapsed) {
            //TODO
        }
        @Override
        public void onExitRoom(int reason) {
            //TODO
        }
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            //TODO
        }
        @Override
        public void onWarning(int warningCode, String warningMsg, Bundle extraInfo) {
            //TODO
        }
        @Override
        public void onUserEnter(String userId) {
            //TODO
            time = 0;
            talkstatus.setVisibility(View.GONE);
        }

        @Override
        public void onUserExit(String s, int i) {
            super.onUserExit(s, i);
        }
    }

    TimerTask timerTask = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            time++;
            handler.sendMessage(message);
        }
    };

    private static String unitFormat(long i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    // 销毁 trtcCloud 实例
    @Override
    protected void onDestroy() {
        super.onDestroy();
        exitRoom();
        localBroadcastManager.unregisterReceiver(msgUpadteReceiver);
        //销毁 trtc 实例
        if (trtcCloud != null) {
            trtcCloud.setListener(null);
            //trtcCloud.destroy();
            TRTCCloud.destroySharedInstance();
        }
        trtcCloud = null;
    }
}
