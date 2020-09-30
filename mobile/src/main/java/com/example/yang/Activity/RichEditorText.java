package com.example.yang.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.example.yang.Activity.AlbumSelectChat;
import com.example.yang.Aichar.IdCardIdtfcation;
import com.example.yang.audio.RecogResult;
import com.example.yang.myapplication.R;
import com.example.yang.network.OkHttpManager;
import com.example.yang.richeditor.RichEditText;
import com.example.yang.richeditor.callback.OnImageClickListener;
import com.example.yang.richeditor.enumtype.RichTypeEnum;
import com.example.yang.richeditor.model.BlockImageSpanVm;
import com.example.yang.richeditor.model.RichEditorBlock;
import com.example.yang.richeditor.model.StyleBtnVm;
import com.example.yang.richeditor.span.BlockImageSpan;
import com.example.yang.util.FileOperationUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RichEditorText extends AppCompatActivity implements View.OnClickListener, EventListener {
    private RichEditText mEditor;
    private final int ANNOUNCEIMAGECODE = 0;
    private final int ANNOUNCEOCRGETTEXTCODE = 1;
    private BlockImageSpanVm blockImageSpanVm = new BlockImageSpanVm(this);
    private boolean isshoweditot = false;
    private OkHttpManager http = new OkHttpManager();
    private IdCardIdtfcation idCardIdtfcation;
    private Uri imageuri;
    private String photopath;
    private boolean isRecording = false;
    private EventManager asr;
    private AudioRecord audioRecord;
    private int frequency = 44100;;
    private int channelConfiguration= AudioFormat.CHANNEL_CONFIGURATION_MONO;;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;;
    private int recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

    //开始定位，这里模拟一下定位
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ANNOUNCEIMAGECODE:
                    mEditor.insertBlockImage(msg.obj.toString(), blockImageSpanVm, new OnImageClickListener() {
                        @Override
                        public void onClick(BlockImageSpan blockImageSpan) {

                        }
                    });
                    break;
                case ANNOUNCEOCRGETTEXTCODE:
                    mEditor.setText(msg.obj.toString());
                    break;
            }
            return false;
        }
        });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editthought);
        mEditor = findViewById(R.id.rechditorthought);
        blockImageSpanVm.setWidth(320);
        blockImageSpanVm.setMaxHeight(480);
        idCardIdtfcation = new IdCardIdtfcation(this);

        LinearLayout editsetting = findViewById(R.id.Edit_thought_text_setting_linearlayout);
        editsetting.setVisibility(View.GONE);
        LinearLayout isedit = findViewById(R.id.texteditor);
        ImageView redo = findViewById(R.id.editthought_redo);
        redo.setOnClickListener(this);
        ImageView undo = findViewById(R.id.editthought_undo);
        undo.setOnClickListener(this);
        ImageView bold = findViewById(R.id.editthought_bold);
        EditText textsize = findViewById(R.id.edit_thought_text_size);
        TextView publish = findViewById(R.id.editreport);
        publish.setOnClickListener(this);
        LinearLayout ocr_gettext = findViewById(R.id.editthoughtphoneinput);
        ocr_gettext.setOnClickListener(this);
        LinearLayout voicetotext = findViewById(R.id.textthoughtvoice);
        voicetotext.setOnClickListener(this);
        LinearLayout tempsave = findViewById(R.id.editthoughtsave);
        tempsave.setOnClickListener(this);
        textsize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(textsize.getText() != null && !textsize.getText().toString().equals("") ) {
                    mEditor.setTextSize(Integer.parseInt(textsize.getText().toString()));
                }
            }
        });
        // 加粗, 仅包含图标（见demo）
        StyleBtnVm styleBtnVm = new StyleBtnVm.Builder()
                .setType(RichTypeEnum.BOLD)
                .setIvIcon(bold)
                .setIconNormalResId(R.mipmap.ic_launcher)
                .setIconLightResId(R.mipmap.ic_launcher)
                .setClickedView(bold)
                .build();
        mEditor.initStyleButton(styleBtnVm);
        ImageView italic = findViewById(R.id.editthought_italic);
        // 加粗, 仅包含图标（见demo）
        StyleBtnVm styleBtnVm_italic = new StyleBtnVm.Builder()
                .setType(RichTypeEnum.ITALIC)
                .setIvIcon(italic)
                .setIconNormalResId(R.mipmap.ic_launcher)
                .setIconLightResId(R.mipmap.ic_launcher)
                .setClickedView(italic)
                .build();
        mEditor.initStyleButton(styleBtnVm);

        isedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isshoweditot) {
                    isshoweditot = false;
                    editsetting.setVisibility(View.GONE);
                }else {
                    isshoweditot = true;
                    editsetting.setVisibility(View.VISIBLE);
                }
            }
        });
        LinearLayout image = findViewById(R.id.editthoughtselectimage);
        image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editthoughtselectimage:
                /* 开启Pictures画面Type设定为image */
                Intent intent = new Intent(this, AlbumSelectChat.class);
                //Intent intent = new Intent();
                //intent.setType("image/*");
                /*使用Intent.ACTION_GET_CONTENT这个Action */
                //intent.setAction(Intent.ACTION_PICK);
                /* 取得相片后返回本画面 */
                intent.putExtra("activity","announceaddactivity");
                startActivityForResult(intent,ANNOUNCEIMAGECODE);
                break;
            case R.id.editthought_redo:
                mEditor.redo();
                break;
            case R.id.editthought_undo:
                mEditor.undo();
                break;
            case R.id.editreport:
                //文章发表
                EditText titleview = findViewById(R.id.editthought_text_title);
                String titletext = titleview.getText().toString();
                List<RichEditorBlock>  listtext = mEditor.getContent();
                break;
            case R.id.editthoughtphoneinput:
                // android 7.0系统解决拍照的问题
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    builder.detectFileUriExposure();
                }
                FileOperationUtil.CreateDir(FileOperationUtil.MAINDIRPATH);
                FileOperationUtil.CreateDir(FileOperationUtil.SECONDMESSAFEDIRPATH);
                //相片保存地址
                photopath = FileOperationUtil.SECONDMESSAFEDIRPATH+File.separator+"123.jpg";
                Intent intent_cap = new Intent();
                // 指定开启系统相机的Action
                intent_cap.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //从这
                intent_cap.addCategory(Intent.CATEGORY_DEFAULT);
                imageuri = Uri.fromFile(FileOperationUtil.CreateFile(photopath));
                // 设置系统相机拍摄照片完成后图片文件的存放地址
                intent_cap.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                //到这不用设置的话会在onActivityResult方法里，在意图获取一个处理过的bitmap
                startActivityForResult(intent_cap, ANNOUNCEOCRGETTEXTCODE);
                break;
            case R.id.textthoughtvoice:
                isRecording = !isRecording;
                if(!isRecording) {
                    audioRecord.stop();
                    audioRecord.release();
                    asr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
                    asr.unregisterListener(this);
                }else {
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,frequency,channelConfiguration,audioEncoding,recBufSize);
                    asr = EventManagerFactory.create(this, "asr");
                    asr.registerListener(this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String, Object> params = new HashMap<>();
                            while (isRecording) {
                                byte[] buffer = new byte[recBufSize];
                                audioRecord.startRecording();
                                int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);
                                params.put(SpeechConstant.IN_FILE, Base64.encodeToString(buffer, 0));
                                params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音

                                String json = new JSONObject(params).toString();
                                asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
                            }
                        }
                    }).start();
                }
                break;
            case R.id.editthoughtsave:

                break;
        }
    }

    /**********************************************************************************************
     * 创建Html 文件
     * ********************************************************************************************/
    public void createHtml(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            //位置返回数据处理
            if(requestCode == ANNOUNCEIMAGECODE){
                //图片返回数据处理
                Bundle bundle= data.getExtras();
                ArrayList mdata =(ArrayList) bundle.getSerializable("picture");
                if(mdata == null || mdata.size() == 0){
                    return ;
                }
                Message message = new Message();
                message.obj = mdata.get(0).toString();
                message.what = ANNOUNCEIMAGECODE;
                handler.sendMessage(message);
            }else if(requestCode == ANNOUNCEOCRGETTEXTCODE){
                try {
                    Map<String, Object> map = new HashMap<String,Object>();
                    Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageuri));
                    recGeneralBasic(photopath, new ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            Message message = new Message();
                            message.obj = result;
                            message.what = ANNOUNCEOCRGETTEXTCODE;
                            handler.sendMessage(message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void recGeneralBasic(String filePath, final ServiceListener listener) {
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(FileOperationUtil.CreateFile(filePath));
        OCR.getInstance(this).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                StringBuilder sb = new StringBuilder();
                for (WordSimple wordSimple : result.getWordList()) {
                    WordSimple word = wordSimple;
                    sb.append(word.getWords());
                    sb.append("\n");
                }
                listener.onResult(sb.toString());
            }

            @Override
            public void onError(OCRError error) {
                listener.onResult(error.getMessage());
            }
        });
    }

    @Override
    public void onEvent(String s, String params, byte[] data, int offset, int length) {
        System.out.println(data);
        if(data != null){
            Message message = new Message();
            message.obj = data.toString();
            message.what = ANNOUNCEOCRGETTEXTCODE;
            handler.sendMessage(message);
        }
        if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_LOADED)) {
            // listener.onOfflineLoaded();
        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED)) {
            // listener.onOfflineUnLoaded();
        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            // 引擎准备就绪，可以开始说话
            //  listener.onAsrReady();
        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            // 检测到用户的已经开始说话
            //  listener.onAsrBegin();

        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            // 检测到用户的已经停止说话
            //  listener.onAsrEnd();

        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            RecogResult recogResult = RecogResult.parseJson(params);
            // 识别结果
            String[] results = recogResult.getResultsRecognition();
            if (recogResult.isFinalResult()) {
                // 最终识别结果，长语音每一句话会回调一次
                //listener.onAsrFinalResult(results, recogResult);
            } else if (recogResult.isPartialResult()) {
                // 临时识别结果
                //listener.onAsrPartialResult(results, recogResult);
            } else if (recogResult.isNluResult()) {
                // 语义理解结果
                // listener.onAsrOnlineNluResult(new String(data, offset, length));
            }

        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            // 识别结束
            RecogResult recogResult = RecogResult.parseJson(params);
            if (recogResult.hasError()) {
                int errorCode = recogResult.getError();
                int subErrorCode = recogResult.getSubError();
                //listener.onAsrFinishError(errorCode, subErrorCode, recogResult.getDesc(), recogResult);
            } else {
                // listener.onAsrFinish(recogResult);
            }
        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH)) { // 长语音
            // listener.onAsrLongFinish(); // 长语音
        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
            //  listener.onAsrExit();
        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_VOLUME)) {
            // Logger.info(TAG, "asr volume event:" + params);
            //   Volume vol = parseVolumeJson(params);
            //   listener.onAsrVolume(vol.volumePercent, vol.volume);
        } else if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_AUDIO)) {
            if (data.length != length) {
                Log.e("null", "internal error: asr.audio callback data length is not equal to length param");
            }
            //  listener.onAsrAudio(data, offset, length);
        }
    }

    interface ServiceListener {
        public void onResult(String result);
    }
}
