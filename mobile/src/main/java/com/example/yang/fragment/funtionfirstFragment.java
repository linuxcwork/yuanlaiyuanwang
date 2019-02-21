package com.example.yang.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.example.yang.myapplication.R;
import com.example.yang.myapplication.chat_contrue;
import com.example.yang.ui.AlbumSelectChat;
import com.example.yang.ui.GifTextView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.baidu.speech.audio.MicrophoneServer.TAG;

@SuppressLint("ValidFragment")
public class funtionfirstFragment extends Fragment{
    View mView;
    Context mcontext;
    public funtionfirstFragment(Context context){
        mcontext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "funtionfirstFragment: onCreateView");
        View view= inflater.inflate(R.layout.chatfuntionfirst, container, false);
        mView= inflater.inflate(R.layout.chat_room_time, null);
        LinearLayout lfuntionalbum = view.findViewById(R.id.funtionalbum);
        lfuntionalbum.setOnClickListener((View.OnClickListener) mcontext);

        LinearLayout lfuntiontakepicture = view.findViewById(R.id.funtiontakepicture);
        lfuntiontakepicture.setOnClickListener((View.OnClickListener) mcontext);
        LinearLayout lfuntionvideochat = view.findViewById(R.id.funtionvideochat);

        LinearLayout lfuntionmakecall = view.findViewById(R.id.funtionmakecall);

        LinearLayout lfuntionposition = view.findViewById(R.id.funtionposition);
        lfuntionposition.setOnClickListener((View.OnClickListener) mcontext);
        return view;
    }

}
