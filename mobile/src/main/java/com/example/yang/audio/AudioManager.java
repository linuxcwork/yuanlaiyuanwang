package com.example.yang.audio;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
/*
* 录音管理类*/
public class AudioManager {
    private final String MTAG = "AudioManager";
    private String mDir;//文件夹名称
    private MediaRecorder mMediaRecorder;
    private String mCurrentFilePath;//文件储存路径
    private static AudioManager mInstance;
    //表明MediaRecorder是否进入prepare状态（状态为true才能调用stop和release方法）
    private boolean isPrepared;
    public AudioManager(String dir) {
        mDir = dir;
    }
    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
    /**
     * 准备完毕接口
     */
    public interface AudioStateListener{
        void wellPrepared();
    }
    public AudioStateListener mListener;
    public void setOnAudioStateListener(AudioStateListener listener){
        mListener = listener;
    }
    /**
     * 单例
     * @return AudioManager
     */
    public static AudioManager getInstance(String dir){
        if (mInstance == null){
            synchronized (AudioManager.class){
                if(mInstance == null){
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 准备
     */
    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(mDir);//创建文件夹
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateFileName();//随机生成文件名
            File file = new File(dir, fileName);//创建文件
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(file.getAbsolutePath());//设置输出文件
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风为音频源
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//设置音频格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepared = true;
            if (mListener != null){
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 随机生成文件的名称
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString()+".amr";
    }
    /**
     * 获取音量等级
     */
    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                //mMediaRecorder.getMaxAmplitude() 范围:1-32767
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;//最大值 * [0,1）+ 1
            } catch (Exception e) {
                Log.e(MTAG,"getVoiceLevel"+e);
            }
        }
        return 1;
    }
    /**
     * 重置
     */
    public void release(){
        if(mMediaRecorder != null){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    /**
     * 取消
     */
    public void cancel(){
        release();
        //删除产生的文件
        if(mCurrentFilePath != null){
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

}
