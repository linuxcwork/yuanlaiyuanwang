package com.example.yang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.Activity.CommectFriendActivity;
import com.example.yang.Activity.SearchChatHistory;
import com.example.yang.Activity.SelectbackgroundActivity;
import com.example.yang.Activity.SettingRelationshipActivity;
import com.example.yang.util.XmppConnection;
import com.makeramen.roundedimageview.RoundedImageView;


public class CurrentFriend extends Activity implements View.OnClickListener{

    private  String account;
    private sqlite_linkmanmss sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currentfrimessage);
        Intent get_intent = getIntent();
        account = get_intent.getStringExtra(sqlite_linkmanmss.KEY_ACTNB);
        if(account != null){
            //查询临时用户数据库，获取相应数据并显示
        } else {
            Toast.makeText(this,"thie account is null",Toast.LENGTH_LONG).show();
        }
        initView();
    }

    private void initView(){
        ImageView returnpre = findViewById(R.id.person_return);
        returnpre.setOnClickListener(this);
        RoundedImageView head_image = findViewById(R.id.current_friend_image);
        TextView petname = findViewById(R.id.current_friend_petname);
        TextView account = findViewById(R.id.current_friend_account);
        TextView real_name = findViewById(R.id.current_friend_authentication_state);
        TextView credit_values = findViewById(R.id.current_friend_credit_value_amount);
        TextView credit_money = findViewById(R.id.current_friend_credit_money_amount);
        RelativeLayout chat_history = findViewById(R.id.current_friend_chat_history);
        chat_history.setOnClickListener(this);
        RelativeLayout chat_background = findViewById(R.id.current_friend_chat_background);
        chat_background.setOnClickListener(this);
        RelativeLayout clear_chat_history = findViewById(R.id.current_friend_clear_chat_history);
        clear_chat_history.setOnClickListener(this);
        RelativeLayout comment = findViewById(R.id.current_friend_comment);
        comment.setOnClickListener(this);
        RelativeLayout relation = findViewById(R.id.current_friend_relation);
        relation.setOnClickListener(this);
        TextView backlist = findViewById(R.id.current_friend_blacklist);
        backlist.setOnClickListener(this);
        TextView addfriend = findViewById(R.id.current_friend_addfriend_sendmessage);
        addfriend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.person_return:
                Intent chatroom = new Intent(this,chat_contrue.class);
                Bundle bundle = new Bundle();
                bundle.putString("name",account);
                chatroom.putExtras(bundle);
                startActivity(chatroom);
                break;
            case R.id.current_friend_chat_history:
                Intent intentsearchhis = new Intent(this, SearchChatHistory.class);
                intentsearchhis.putExtra("account",account);
                startActivity(intentsearchhis);
                break;
            case R.id.current_friend_chat_background:
                Intent intentbackground = new Intent(this, SelectbackgroundActivity.class);
                Bundle bundlebackground = new Bundle();
                bundlebackground.putString("name",account);
                intentbackground.putExtras(bundlebackground);
                startActivity(intentbackground);
                break;
            case R.id.current_friend_clear_chat_history:
                sql = new sqlite_linkmanmss(this,"link",null,1);
                sql.open();
                sql.deleteContact(sql.DATABASE_TABLE,account,sql.KEY_ACTNB);
                sql.close();
                break;
            case R.id.current_friend_comment:
                Intent comment = new Intent(this, CommectFriendActivity.class);
                Bundle bundcomment = new Bundle();
                bundcomment.putString("name",account);
                comment.putExtras(bundcomment);
                startActivity(comment);
                break;
            case R.id.current_friend_relation:
                Intent relation = new Intent(this, SettingRelationshipActivity.class);
                Bundle bundrelation = new Bundle();
                bundrelation.putString("name",account);
                relation.putExtras(bundrelation);
                startActivity(relation);
                break;
            case R.id.current_friend_blacklist:
                break;
            case R.id.current_friend_addfriend_sendmessage:
                XmppConnection xmppConnection = XmppConnection.getInstance();
                xmppConnection.addFriend(account,null);
                break;
        }
    }
}
