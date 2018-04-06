package com.example.yang.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by yang on 2018/3/19.
 */

public class chat_contrue extends Activity implements View.OnClickListener{

    ImageButton return_pre;
    TextView friend_name;
    ImageView chat_friendmessage;
    TextView chat_room;
    ImageView chat_voice;
    ImageView chat_picture_view;
    ImageView else_funtion;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat);

        return_pre = (ImageButton)  findViewById(R.id.chat_return);
        friend_name = (TextView) findViewById(R.id.chat_friendname);
        chat_friendmessage = (ImageView) findViewById(R.id.chat_friendmessage);
        chat_room = (TextView) findViewById(R.id.chatroom);
        chat_voice = (ImageView) findViewById(R.id.chat_voice);
        chat_picture_view = (ImageView) findViewById(R.id.chat_picture);
        else_funtion = (ImageView) findViewById(R.id.chat_else_function);


        return_pre.setOnClickListener(this);
        chat_friendmessage.setOnClickListener(this);
        chat_voice.setOnClickListener(this);
        chat_picture_view.setOnClickListener(this);
        else_funtion.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name = bundle.getString("key");
        friend_name.setText(name);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.chat_return:
                /*
                * 返回主菜单中的message*/
                Intent intent = new Intent(chat_contrue.this,MainActivity.class);
                chat_contrue.this.startActivity(intent);
            break;
            case R.id.chat_friendmessage:
                /*
                *跳转到正在聊天的人员的个人信息页面*/

                break;
            case R.id.chat_voice:
                /*
                * 进入语音聊天模式*/
                break;
            case R.id.chat_picture:
                /*
                * 聊天动画*/
                break;
            case R.id.chat_else_function:
                /*
                * 聊天辅助功能*/
                break;
                default:
                    break;
        }

    }
}
