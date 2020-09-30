package com.example.yang.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.sqlite_linkmanmss;
import com.example.yang.util.XmppConnection;

import org.jxmpp.stringprep.XmppStringprepException;

import static com.example.yang.Activity.ChatRTCVoiceActivity.CHATRTCVOICE;

public class ChatRTCVoiceIncommingActivity extends AppCompatActivity {
    private String account = null;
    private String roomid = null;
    private byte[] avaimag;
    private String name;
    private XmppConnection xmppConnection;
    private sqlite_linkmanmss sql;

    private LocalBroadcastManager localBroadcastManager;
    private RTCVoiceStateReceiver msgUpadteReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_rtc_voice_recv_xml);
        Intent callring = getIntent();
        xmppConnection = XmppConnection.getInstance();
        sql = new sqlite_linkmanmss(this, "link", null, 1);
        account = callring.getStringExtra("account");
        sql.open();
        Cursor cursor = sql.getContact(MainActivity.friendinfotable, sql.KEY_ACTNB, account);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            int indeximage = cursor.getColumnIndex(sql.KEY_ROWID);
            avaimag =  cursor.getBlob(indeximage);
            int indexname = cursor.getColumnIndex(sql.KEY_NAME);
            name = cursor.getString(indexname);
        }
        sql.close();
        roomid = callring.getStringExtra("roomid");
        initView();

        //广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CHATRTCVOICE);
        msgUpadteReceiver = new RTCVoiceStateReceiver();
        //注册广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //本地广播管理器注册广播接收器
        localBroadcastManager.registerReceiver(msgUpadteReceiver, intentFilter);
    }

    private class RTCVoiceStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //如果是当前用户，则结束通话
            String name = intent.getStringExtra("account");
            if(name != null && name.equals(account)){
                finish();
            }
        }
    }

    private void initView(){
        //头像
        ImageView avatar = findViewById(R.id.chat_rtc_voice_initiate_avatar_img);
        Glide
                .with(this)
                .load(avaimag)
                .fallback(R.drawable.defaulpict)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(120, 160)
                .dontAnimate()
                .centerCrop()
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(avatar);
        //昵称
        TextView nameview = findViewById(R.id.chat_rtc_voice_initiate_textUserName);
        nameview.setText(name);
        //呼叫状态
        TextView callstate = findViewById(R.id.chat_rtc_voice_initiate_textstatue);
        callstate.setText("等待接听....");
        //挂断
        RelativeLayout handup = findViewById(R.id.callkit_callhang_up_left);
        handup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    xmppConnection.sendMessages(account,"拒绝通话",sqlite_linkmanmss.KEY_MESSAGE_TYPE_REJECT_CALL);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        //接听
        RelativeLayout answer = findViewById(R.id.rc_voip_call_answer_rel);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent incomming = new Intent(ChatRTCVoiceIncommingActivity.this,ChatRTCVoiceActivity.class);
                incomming.putExtra("mapresource",account);
                incomming.putExtra("roomid",roomid);
                startActivity(incomming);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(msgUpadteReceiver);
    }
}
