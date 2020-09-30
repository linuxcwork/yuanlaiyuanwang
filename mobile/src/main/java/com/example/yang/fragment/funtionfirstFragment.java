package com.example.yang.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.yang.myapplication.R;

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
        lfuntionvideochat.setOnClickListener((View.OnClickListener) mcontext);
        LinearLayout lfuntionmakecall = view.findViewById(R.id.funtionmakecall);
        lfuntionmakecall.setOnClickListener((View.OnClickListener) mcontext);
        LinearLayout lfuntionposition = view.findViewById(R.id.funtionposition);
        lfuntionposition.setOnClickListener((View.OnClickListener) mcontext);
        return view;
    }

}
