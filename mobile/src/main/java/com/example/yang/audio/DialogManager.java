package com.example.yang.audio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.myapplication.R;

import static com.example.yang.network.OkHttpManager.TAG;

public class DialogManager {
    private Dialog mDialog;
    private ImageView mVoice;
    private Context mContext;
    private Button send;
    private Button cancle;
    public DialogManager(Context context) {
        mContext = context;
       // mDialog = new Dialog(context,R.style.Theme_AudioDialog);
    }


    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds,String filePath,String flag);
    }
    private AudioFinishRecorderListener mAudioFinishRecorderListener;
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mAudioFinishRecorderListener = listener;
    }
    /**
     * 显示Dialog
     */
    public void showRecordingDialog(AudioFinishRecorderListener listener){
        //将布局应用于Dialog
        mDialog = new Dialog(mContext,R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder,null);
        mDialog.setContentView(view);
        mDialog.setTitle("正在录音......");
        //成员控件赋值
        send = (Button) mDialog.findViewById(R.id.voice_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFinish(10,null,"send");
                mDialog.hide();
            }
        });
        cancle = (Button) mDialog.findViewById(R.id.voice_canle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFinish(10,null,"cancle");
                mDialog.hide();
            }
        });
        mVoice = (ImageView) mDialog.findViewById(R.id.recorder_dialog_voice);
        mDialog.show();
    }

    public boolean DialogIsShowing(){
        return ((mDialog != null) && (mDialog.isShowing()));
    }
    public void recording(){
        if(mDialog != null && mDialog.isShowing()){
            //mIcon.setVisibility(View.VISIBLE);
            //mVoice.setVisibility(View.VISIBLE);
            //mLabel.setVisibility(View.VISIBLE);
            //mIcon.setImageResource(R.mipmap.recorder);
            //mLabel.setText("手指上滑，取消发送");
        }
    }
    public void wantToCancel(){
        if(mDialog != null && mDialog.isShowing()){
            //mIcon.setVisibility(View.VISIBLE);
           // mVoice.setVisibility(View.GONE);
           /* mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.mipmap.cancel);
            mLabel.setText("松开手指，取消发送");*/
        }
    }
    public void tooShort(){
        if(mDialog != null && mDialog.isShowing()){
           // mIcon.setVisibility(View.VISIBLE);
            //mVoice.setVisibility(View.GONE);
            /*mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.mipmap.voice_to_short);
            mLabel.setText("录音时间过短");*/
        }
    }
    public void dismissDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }
    /**
     * 通过level更新音量资源图片
     * @param level
     */
    public void updateVoiceLevel(int level){
        if(mDialog != null && mDialog.isShowing()){
            int resId = mContext.getResources().getIdentifier("v"+level,"mipmap",mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}
