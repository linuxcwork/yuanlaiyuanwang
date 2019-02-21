package com.example.yang.audio;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.yang.myapplication.R;


public class AudioRecordButton extends android.support.v7.widget.AppCompatButton {
    private static final int STATE_NORMAL = 1;//默认状态
    private static final int STATE_RECORDING = 2;//录音状态
    private static final int STATE_WANT_CANCEL = 3;//想取消状态
    private static final int DISTANCE_Y_CANCEL = 50;//定义上滑取消距离
    private int mCurState = STATE_NORMAL;//记录当前状态
    private boolean isRecording = false;//是否在录音状态
    private DialogManager mDialogManager;
    private AudioManager mAudioManager;
    private float mTime;//记录录音时长
    private boolean mReady;//是否触发OnLongClick事件
    private boolean isComplete = true;//是否已经完成
    public AudioRecordButton(Context context) {
        this(context,null);
    }
    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
        String dir = Environment.getExternalStorageDirectory()+"/TiddlerLiu/recorder/audios";//最好判断SD卡是否存在可读
        mAudioManager = AudioManager.getInstance(dir);
       // mAudioManager.setOnAudioStateListener(this);
        /*setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {*/
                mReady = true;
                mAudioManager.prepareAudio();
              /*  return false;
            }
        });*/
    }
    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds,String filePath);
    }
    private AudioFinishRecorderListener mAudioFinishRecorderListener;
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mAudioFinishRecorderListener = listener;
    }
    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DISMISS = 0x112;
    private static final int MSG_AUDIO_COMPLETE = 0x113;//达到最大时长，自动完成
    /**
     * 获取音量大小
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    if(mTime >= 60f){//60s自动触发完成录制
                        mHandler.sendEmptyMessage(MSG_AUDIO_COMPLETE);
                    }
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    //显示应该在audio end prepared以后
                    //mDialogManager.showRecordingDialog();
                    isRecording = true;
                    isComplete = false;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                case MSG_AUDIO_COMPLETE:
                    complete();
                    reset();
                    break;
                default:
                    break;
            }
        }
    };

    //@Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
       int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording){
                    //根据(x,y)坐标，判断是否想要取消
                    if (wantToCancel(x,y)){
                        changeState(STATE_WANT_CANCEL);
                    }else{
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!isComplete){//没有执行超时自动完成逻辑
                    if (!mReady) {//还未触发OnLongClick事件
                        reset();
                        return super.onTouchEvent(event);
                    }
                    if (!isRecording || mTime < 0.6f) {//还未开始录音 或者 录制时长过短
                        mDialogManager.tooShort();
                        mAudioManager.cancel();
                        mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);//1.3秒后关闭对话框
                    } else if (mCurState == STATE_RECORDING) {//正常录制结束
                        complete();
                    } else if (mCurState == STATE_WANT_CANCEL) {//想要取消状态
                        mDialogManager.dismissDialog();
                        mAudioManager.cancel();
                    }
                    reset();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    /**
     * 正常录制结束
     */
    private void complete() {
        mDialogManager.dismissDialog();
        mAudioManager.release();
        if(mAudioFinishRecorderListener != null && !isComplete){
            mAudioFinishRecorderListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
        }
    }
    /**
     * 恢复状态和标志位
     */
    private void reset() {
        isRecording = false;
        mReady = false;
        mTime = 0;
        isComplete = true;
        changeState(STATE_NORMAL);
    }
    /**
     * 根据(x,y)坐标，判断是否想要取消
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        if(x < 0 || x > getWidth()){//手指移出button范围
            return true;
        }
        if(y < DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL){//手指移出Y轴设定范围
            return true;
        }
        return false;
    }
    /**
     * 改变状态
     * @param state
     */
    private void changeState(int state) {
        if(mCurState != state){
            mCurState = state;
           /* switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    if(isRecording){
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_want_cancel);
                    mDialogManager.wantToCancel();
                    break;
                default:
                    break;
            }*/
        }
    }
}
