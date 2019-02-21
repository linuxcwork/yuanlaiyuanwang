package com.example.yang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



public class CurrentFriend extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currentfrimessage);
        Intent get_intent = getIntent();
    }
}
