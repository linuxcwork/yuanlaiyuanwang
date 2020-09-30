package com.example.yang.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.yang.adapter.searchhistoryresult;
import com.example.yang.myapplication.CurrentFriend;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.sqlite_linkmanmss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchChatHistory extends Activity {

    private sqlite_linkmanmss sql;
    private List<Map<String,String>> list = new ArrayList<>();
    private List<Map<String,String>> mData = new ArrayList<>();

    private  ListView searchresult;
    public SearchChatHistory() {
        sql = new sqlite_linkmanmss(this,"link",null,1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchchathistoryxml);
        String account = getIntent().getStringExtra("account");
        sql.open();
        Cursor cursor = sql.getContact(sql.DATABASE_TABLE,sqlite_linkmanmss.KEY_ACTNB,account);
        if(cursor != null){
            Map<String,String> map = new HashMap<>();
            int indexmsg = cursor.getColumnIndex(sql.KEY_CONTENT);
            int indextime = cursor.getColumnIndex(sql.KEY_TIME);
            while (cursor.moveToNext()){
                map.put(sql.KEY_CONTENT,cursor.getString(indexmsg));
                map.put(sql.KEY_TIME,cursor.getString(indextime));
                list.add(map);
            }
        }
        sql.close();
        ImageView prelayout = findViewById(R.id.search_history_person_return);
        prelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent precurrentfriendmaglayout = new Intent(SearchChatHistory.this, CurrentFriend.class);
                startActivity(precurrentfriendmaglayout);
            }
        });
        EditText searchfor = findViewById(R.id.search_history_activity_sponsor);
        ImageView deleteinfo = findViewById(R.id.search_history_delete);
        deleteinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchfor.setText("");
            }
        });
        searchresult = findViewById(R.id.search_history_search_result);
        Button startsearch = findViewById(R.id.search_history_start_search);
        startsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    if(list.get(i).get(sql.KEY_CONTENT).contains(searchfor.getText())){
                        mData.add(list.get(i));
                    }
                }
                searchhistoryresult adapt = new searchhistoryresult(SearchChatHistory.this,mData);
                searchresult.setAdapter(adapt);
                searchresult.smoothScrollToPositionFromTop(mData.size(),0);
                searchresult.setSelection(mData.size());
            }
        });


    }
}
