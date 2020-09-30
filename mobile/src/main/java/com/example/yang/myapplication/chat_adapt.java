package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.adapter.SpanStringUtils;
import com.example.yang.util.EmotionUtils;
import com.example.yang.item.GifTextView;
import com.example.yang.util.SharedPreferencedUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2018/4/6.
 */

public class chat_adapt extends BaseAdapter{
    private Context mContext;
    private List<Map<String,Object>> mData;

    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();;
    public final static String DIRETION = "diretion";
    public final static String MESGTYPE = "messagetype";
    public final static String MESGTIME = "time";
    public final static String MESGCONTENT = "content";
    public final static String MESGSTATE = "state";
    public final static String MESGIMAGEPath = "imagepaht";

    public chat_adapt(Context context,List<Map<String,Object>> data)
    {
        this.mContext=context;
        this.mData=data;
    }

    public void Refresh()
    {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int Index)
    {
        return mData.get(Index);
    }

    @Override
    public long getItemId(int Index)
    {
        return Index;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int Index, View mView, ViewGroup mParent)
    {
        TextView Content;
        Object type = mData.get(Index).get(sqlite_linkmanmss.KEY_DIRECTION);
        Object time = mData.get(Index).get(sqlite_linkmanmss.KEY_TIME);
		
        if(type != null) {
            if (type.toString().equals("recv")) {
                mView = LayoutInflater.from(mContext).inflate(R.layout.chat_room_recv, null);
                RoundedImageView headimage = mView.findViewById(R.id.recvHeader);
                if(mData.get(Index).get(sqlite_linkmanmss.KEY_ROWID) != null) {
                    Glide
                            .with(mContext)
                            .load(chat_contrue.friendimage)
							.fallback(R.drawable.defaulpict)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(120, 160)
                            .dontAnimate()
                            .centerCrop()
                            .skipMemoryCache(true) // 不使用内存缓存
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                            .into(headimage);
                }
                RoundedImageView recv_image =  mView.findViewById(R.id.chat_recv_image);
                recv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                VideoView recv_video =  mView.findViewById(R.id.chat_recv_mvideo);
                recv_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                LinearLayout msglin = mView.findViewById(R.id.chat_item_layout_content);
                GifTextView Content_from =  mView.findViewById(R.id.From_Content);
                ImageView voiceimage = mView.findViewById(R.id.chat_item_voice);
                voiceimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                TextView voicetime = mView.findViewById(R.id.chat_item_voice_time);
                switch (mData.get(Index).get(sqlite_linkmanmss.EKY_MESSAGETYPE).toString()){
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_STRING:
                        msglin.setVisibility(View.VISIBLE);
                        Content_from.setVisibility(View.VISIBLE);
                        Content_from.setText(SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,mContext,Content_from,mData.get(Index).get(MESGCONTENT).toString()));
                        break;
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_IMAGE:
                        recv_image.setVisibility(View.VISIBLE);
                        Glide
                                .with(mContext)
                                .load(mData.get(Index).get(MESGCONTENT).toString())
                                .dontAnimate()
                                .centerCrop()
                                .into(recv_image);
                        break;
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_VOICE:
                        msglin.setVisibility(View.VISIBLE);
                        voiceimage.setVisibility(View.VISIBLE);
                        voicetime.setVisibility(View.VISIBLE);
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        System.out.println(mData.get(Index).get(MESGCONTENT));
                        try {
                            mediaPlayer.setDataSource(String.valueOf(mData.get(Index).get(MESGCONTENT)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String rvoicetime = String.valueOf(mediaPlayer.getDuration() / 1000) + "\"";
                        voicetime.setText(rvoicetime);
                        msglin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mediaPlayer.start();
                            }
                        });
                        break;
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_VIDEO:
                        recv_video.setVisibility(View.VISIBLE);
                        recv_video.setVideoPath(Environment.getExternalStorageDirectory().getPath()+ File.separator+"Android"+File.separator+"test.mp4");
                        recv_video.setMediaController(new MediaController(mContext));
                        recv_video.start();
                        break;
                    default:
                }
            } else if (type.toString().equals("send")) {
                mView = LayoutInflater.from(mContext).inflate(R.layout.chat_room_send, null);
                GifTextView Content_to =  mView.findViewById(R.id.To_Content);
                RoundedImageView headimage = mView.findViewById(R.id.sendHeader);
                LinearLayout chat_item_layout_content = mView.findViewById(R.id.chat_item_layout_content);
                ImageView chat_item_voice = mView.findViewById(R.id.chat_item_voice);
                TextView chat_item_voice_time= mView.findViewById(R.id.chat_item_voice_time);
                ImageView sendfail = mView.findViewById(R.id.chat_item_fail);

                Glide
                            .with(mContext)
                            .load(sharedPreferencedUtils.getUserInfo(mContext,"headsculpture",null))
							.fallback(R.drawable.defaulpict)
                            .dontAnimate()
                            .centerCrop()
                            .skipMemoryCache(true) // 不使用内存缓存
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                            .into(headimage);
                RoundedImageView recv_image =  mView.findViewById(R.id.chat_send_image);
                VideoView recv_video =  mView.findViewById(R.id.chat_send_mvideo);
                switch (mData.get(Index).get(sqlite_linkmanmss.EKY_MESSAGETYPE).toString()){
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_STRING:
                        chat_item_layout_content.setVisibility(View.VISIBLE);
                        Content_to.setVisibility(View.VISIBLE);
                        Content_to.setText(SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE,mContext,Content_to,mData.get(Index).get(MESGCONTENT).toString()));
                        break;
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_IMAGE:
                        recv_image.setVisibility(View.VISIBLE);
                        Glide
                                .with(mContext)
                                .load(mData.get(Index).get(MESGCONTENT).toString())
                                .dontAnimate()
                                .centerCrop()
                                .into(recv_image);
                        break;
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_VOICE:
                        chat_item_layout_content.setVisibility(View.VISIBLE);
                        chat_item_voice.setVisibility(View.VISIBLE);
                        chat_item_voice_time.setVisibility(View.VISIBLE);
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        System.out.println(mData.get(Index).get(MESGCONTENT));
                        try {
                            mediaPlayer.setDataSource(String.valueOf(mData.get(Index).get(MESGCONTENT)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String voicetime = String.valueOf(mediaPlayer.getDuration() / 1000) + "\"";
                        chat_item_voice_time.setText(voicetime);
                        chat_item_layout_content.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mediaPlayer.start();
                            }
                        });
                        break;
                    case sqlite_linkmanmss.KEY_MESSAGE_TYPE_VIDEO:
                        recv_video.setVisibility(View.VISIBLE);
                        recv_video.setVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "test.mp4");
                        recv_video.setMediaController(new MediaController(mContext));
                        recv_video.start();
                        break;
                    default:
                }
            }
        }

        return mView;
    }

}
