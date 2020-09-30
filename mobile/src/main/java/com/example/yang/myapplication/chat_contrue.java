package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.map3d.demo.basic.MapLocationPosition;
import com.baidu.speech.audio.MicrophoneServer;
import com.example.yang.Activity.ChatRTCVoiceActivity;
import com.example.yang.adapter.GlobalOnItemClickManagerUtils;
import com.example.yang.adapter.HorizontalRecyclerviewAdapter;
import com.example.yang.adapter.NoHorizontalScrollerVPAdapter;
import com.example.yang.audio.AudioManager;
import com.example.yang.audio.DialogManager;
import com.example.yang.fragment.EmotiomComplateFragment;
import com.example.yang.fragment.Fragment1;
import com.example.yang.fragment.FragmentFactory;
import com.example.yang.network.OkHttpManager;
import com.example.yang.Activity.AlbumSelectChat;
import com.example.yang.item.EmotionKeyboard;
import com.example.yang.util.EmotionUtils;
import com.example.yang.item.ImageModel;
import com.example.yang.util.SharedPreferencedUtils;
import com.example.yang.util.FileOperationUtil;
import com.example.yang.util.XmppConnection;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.trtc.TRTCCloudListener;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_IMAGE;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_STRING;
import static com.example.yang.util.MessageListener.BROADCASTRECVUPDATEFILE;

/**
 * Created by yang on 2018/3/19.
 */

public class chat_contrue extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "chat_contrue";
    private TextView friend_name;
    private static ListView chat_room;
    private ImageView chat_voice;
    private ImageView chat_picture_view;
    private ImageView else_funtion;
    private LinearLayout high_fun;
    private ViewPager funtionviewPagerclass;
    private static EditText chin;
    private Date temp = null;
    /*dialog 音量显示*/
    private ImageView mVoice;
    private String chataccount;

    /* intent 返回的请求码*/
    public int requestcodemap = 1;   //地图
    public int requestcodeblu = 2;   //相册
    public int requestcodepic = 3;   //拍照

    private AlertDialog alert = null;
    private final ThreadLocal<OkHttpManager> services = new ThreadLocal<OkHttpManager>();
    private static chat_adapt adapt;
    private Context mContext;
    private List<Map<String, Object>> mdata = new ArrayList<>();
    //当前聊天对象
    private String CurrentObject = null;
    public static byte[] friendimage;

    //是否将时间写入到数据库，如果当前消息与上一条消息间隔小于5秒则不加
    private Boolean isWritetodatebase = true;
    //表情面板
    private EmotionKeyboard mEmotionKeyboard;

    /*
     * 声音相关变量*/
    private boolean isRecording = false;//是否在录音状态
    private DialogManager mDialogManager;
    private float mTime;//记录录音时长
    private static final int MSG_DIALOG_DISMISS = 0x112;
    private static final int MSG_AUDIO_COMPLETE = 0x113;//达到最大时长，自动完成
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MAG_RECVMSG_DEAL = 0x114;

    //收到图片路径
    private File recvimagepath = null;
    private sqlite_linkmanmss sql;

    private List<Fragment> fragments = new ArrayList<>();
    private static final String CURRENT_POSITION_FLAG = "CURRENT_POSITION_FLAG";
    private int CurrentPosition = 0;
    private RecyclerView recyclerview_horizontal;
    private HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;
    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager viewPager;
    //拍照处理类
    private String photopath;
    public String BROADCASTRECVUPDATE = "com.example.yang.updaterecv";

    private XmppConnection xmppConnection;

    private LocalBroadcastManager localBroadcastManager;
    private CurrMsgUpadteReceiver msgUpadteReceiver;

    /**
     * 获取音量大小
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    if (mTime >= 60f) {//60s自动触发完成录制
                        mHandler.sendEmptyMessage(MSG_AUDIO_COMPLETE);
                    }
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VOICE_CHANGED:
                    //updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                case MSG_AUDIO_COMPLETE:
                    reset();
                    break;
                case MAG_RECVMSG_DEAL:
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    mdata.add(map);
                    //更新数据
                    adapt.Refresh();
                    chin.setText("");
                    chat_room.smoothScrollToPositionFromTop(mdata.size(), 0);
                    break;
                default:
                    break;
            }
        }
    };

    public chat_contrue() {
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        sql = new sqlite_linkmanmss(this, "link", null, 1);
        initUI();
        mContext = getApplicationContext();
        xmppConnection = XmppConnection.getInstance();
        xmppConnection.recieveMessage(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, org.jivesoftware.smack.packet.Message message, Chat chat) {
                System.out.println(from+":"+chataccount);
                System.out.println(message.getTo()+" : "+message.getFrom());
                try {
                    if(from.toString().equals(JidCreate.entityBareFrom(chataccount).toString())) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(sqlite_linkmanmss.KEY_ACTNB, from.toString());
                        map.put(sqlite_linkmanmss.KEY_DIRECTION, "recv");
                        map.put(sqlite_linkmanmss.KEY_CONTENT, message.getBody());
                        map.put(sqlite_linkmanmss.KEY_TIME, "time");
                        map.put(sqlite_linkmanmss.EKY_MESSAGETYPE, KEY_MESSAGE_TYPE_STRING);
                        map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, "0");
                        Message msg = new Message();
                        msg.what = MAG_RECVMSG_DEAL;
                        msg.obj = map;
                        mHandler.sendMessage(msg);
                    }
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
            }
        });

        recvimagepath = FileOperationUtil.CreateDir(FileOperationUtil.SECONDMESSAFEDIRPATH+"recvimage");

        /********************************************************
         * 表情功能部分
         * ******************************************************/
        mEmotionKeyboard = EmotionKeyboard.with(this);
        mEmotionKeyboard.setEmotionView(findViewById(R.id.ll_emotion_layout));//绑定表情面板
        mEmotionKeyboard.setFunctionView(high_fun);
        mEmotionKeyboard.setPagerView(viewPager);
        mEmotionKeyboard.BindToViewPager(this, funtionviewPagerclass);
        mEmotionKeyboard.BindToAddFuntion(else_funtion);
        mEmotionKeyboard.bindToContent(chat_room);
        mEmotionKeyboard.bindToEmotionButton(findViewById(R.id.chat_picture));//绑定表情按钮
        mEmotionKeyboard.bindToEditText(chin);
        mEmotionKeyboard.build();

        initDatas();

        GlobalOnItemClickManagerUtils globalOnItemClickManager = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickManager.attachToEditText(chin);

        Intent intentalbum = getIntent();
        Bundle bundlealbum = intentalbum.getExtras();

        if (bundlealbum != null && bundlealbum.get("picturer") != null) {
            ArrayList arrayList = (ArrayList) bundlealbum.get("picturer");
        } else if (bundlealbum != null && bundlealbum.get("name") != null) {
            chataccount = bundlealbum.getString("name");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                sqlite_linkmanmss tsql = new sqlite_linkmanmss(mContext, "link", null, 1);
                tsql.open();
                int count = 0;
                Map<String, String> map = new HashMap<String, String>();
                map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, String.valueOf(count));
                tsql.updateContact(MainActivity.friendinfotable,sqlite_linkmanmss.KEY_ACTNB,chataccount,map);
                Cursor cursor = tsql.getContact(MainActivity.friendinfotable,sqlite_linkmanmss.KEY_ACTNB,chataccount);
                if(cursor != null && cursor.moveToFirst()){
                    int indeximage = cursor.getColumnIndex(sqlite_linkmanmss.KEY_ROWID);
                    int indexname = cursor.getColumnIndex(sqlite_linkmanmss.KEY_NAME);
                    CurrentObject = cursor.getString(indexname);
                    if(CurrentObject != null){
                        Log.e(TAG,"CurrentObject: "+CurrentObject);
                        friend_name.setText(CurrentObject);
                    }
                    friendimage = cursor.getBlob(indeximage);
                    if(friendimage == null){
                        Log.e(TAG,"friendimage: "+friendimage);
                    }
                }
                tsql.close();
            }
        }).start();


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCASTRECVUPDATEFILE);
        msgUpadteReceiver = new CurrMsgUpadteReceiver();
        //注册广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //本地广播管理器注册广播接收器
        localBroadcastManager.registerReceiver(msgUpadteReceiver, intentFilter);

        mdata = LoadData();
        adapt = new chat_adapt(this, mdata);
        chat_room.setAdapter(adapt);
        chat_room.smoothScrollToPositionFromTop(mdata.size(), 0);
        chat_room.setSelection(mdata.size());
    }


    private class CurrMsgUpadteReceiver extends BroadcastReceiver {
        @SuppressLint("ResourceType")
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("account");
            String path = intent.getStringExtra("path");
            String type = intent.getStringExtra("type");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(sqlite_linkmanmss.KEY_ACTNB, name);
            map.put(sqlite_linkmanmss.KEY_DIRECTION, "recv");
            map.put(sqlite_linkmanmss.KEY_CONTENT, path);
            //map.put(sqlite_linkmanmss.KEY_TIME, "time");
            map.put(sqlite_linkmanmss.EKY_MESSAGETYPE, type);
            map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, "0");

            Message msg = new Message();
            msg.what = MAG_RECVMSG_DEAL;
            msg.obj = map;
            mHandler.sendMessage(msg);
        }
    }

    private void initUI() {

        ImageView return_pre = findViewById(R.id.chat_return);
        friend_name = findViewById(R.id.chat_friendname);
        ImageView chat_friendmessage = findViewById(R.id.chat_friendmessage);
        chat_room = findViewById(R.id.chatroom);
        chat_voice = findViewById(R.id.chat_voice);
        chat_picture_view = findViewById(R.id.chat_picture);
        else_funtion = findViewById(R.id.chat_add);
        else_funtion.setVisibility(View.VISIBLE);
        TextView send = findViewById(R.id.chat_send);
        send.setVisibility(View.GONE);
        chin = findViewById(R.id.chat_input);
        viewPager = findViewById(R.id.vp_emotionview_layout);
        recyclerview_horizontal = findViewById(R.id.recyclerview_horizontal);
        high_fun = findViewById(R.id.high_funtion);
        funtionviewPagerclass = findViewById(R.id.funtionviewpage);

        return_pre.setOnClickListener(this);
        chat_friendmessage.setOnClickListener(this);
        chat_voice.setOnClickListener(this);
        chat_picture_view.setOnClickListener(this);
        send.setOnClickListener(this);

        chin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    else_funtion.setVisibility(View.VISIBLE);
                    send.setVisibility(View.GONE);
                } else {
                    else_funtion.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_return:
                /**********************************************
                 * 返回主菜单中的message
                 * ********************************************/
                Intent intent_return = new Intent(chat_contrue.this, MainActivity.class);
                intent_return.putExtra("num", 0);
                chat_contrue.this.startActivity(intent_return);
                break;
            case R.id.chat_friendmessage:
                /***********************************************
                 * 跳转到正在聊天的人员的个人信息页面
                 * *********************************************/
                Intent intent_information = new Intent(chat_contrue.this, CurrentFriend.class);
                intent_information.putExtra(sqlite_linkmanmss.KEY_ACTNB, friend_name.getText().toString());
                this.startActivity(intent_information);
                break;
            case R.id.chat_voice:
                /************************************************
                 * 进入语音聊天模式
                 * **********************************************/
            {
                mDialogManager = new DialogManager(this);
                String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "YLYW" + File.separator + "voice";
                AudioManager mAudioManager = AudioManager.getInstance(path);
                mAudioManager.prepareAudio();
                mDialogManager.showRecordingDialog(new DialogManager.AudioFinishRecorderListener() {
                    @Override
                    public void onFinish(float seconds, String file, String flag) {
                        if (flag.equals("send")) {
                            updateMessageUI(mAudioManager.getCurrentFilePath(), "send", sqlite_linkmanmss.KEY_MESSAGE_TYPE_VOICE);
                            System.out.println(mAudioManager.getCurrentFilePath());
                            mAudioManager.release();
                        } else if (flag.equals("cancle")) {
                            // mDialogManager.dismissDialog();
                            mAudioManager.cancel();
                            mAudioManager.release();
                        }
                    }
                });
                isRecording = true;
                new Thread(mGetVoiceLevelRunnable).start();
            }
            break;
            case R.id.chat_send:
                try {
                    SendMessage(null);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                break;
            /* 聊天界面的获取相册中图片*/
            case R.id.funtionalbum:
                String albumpath = Environment.getExternalStorageDirectory().getPath() + File.separator + "YLYW" + File.separator + "albumtemp";
                /* 开启Pictures画面Type设定为image */
                Intent intent = new Intent(this, AlbumSelectChat.class);
                //Intent intent = new Intent();
                //intent.setType("image/*");
                /*使用Intent.ACTION_GET_CONTENT这个Action */
                //intent.setAction(Intent.ACTION_PICK);
                /* 取得相片后返回本画面 */
                intent.putExtra("activity", "chat_contrue");
                startActivityForResult(intent, requestcodeblu);
                break;
            /* 聊天界面的拍照功能 */
            case R.id.funtiontakepicture:
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();
                FileOperationUtil.CreateDir(FileOperationUtil.MAINDIRPATH);
                FileOperationUtil.CreateDir(FileOperationUtil.SECONDMESSAFEDIRPATH);
                //相片保存地址
                photopath = Environment.getExternalStorageDirectory().getPath() + File.separator + FileOperationUtil.SECONDPICTUREDIRPATH + File.separator + getNumSmallLetter(8) + ".jpg";
                Intent intent_cap = new Intent();
                // 指定开启系统相机的Action
                intent_cap.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                //从这
                intent_cap.addCategory(Intent.CATEGORY_DEFAULT);

                // 设置系统相机拍摄照片完成后图片文件的存放地址
                intent_cap.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photopath)));
                //到这不用设置的话会在onActivityResult方法里，在意图获取一个处理过的bitmap
                startActivityForResult(intent_cap, requestcodepic);
                break;
            /* 聊天界面获取当前位置*/
            case R.id.funtionposition:
                Intent intentchatmap = new Intent(chat_contrue.this, MapLocationPosition.class);
                intentchatmap.putExtra("mapresource","chat_contrue");
                startActivityForResult(intentchatmap, requestcodemap);
                break;
            case R.id.funtionmakecall:
                Intent intentrtcvoice = new Intent(chat_contrue.this, ChatRTCVoiceActivity.class);
                intentrtcvoice.putExtra("mapresource",chataccount);
                intentrtcvoice.putExtra("roomid",100);
                startActivityForResult(intentrtcvoice, requestcodemap);
                try {
                    xmppConnection.sendMessages(chataccount,"100",sqlite_linkmanmss.KEY_MESSAGE_TYPE_CALL);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /********************************************************************************************************
     *  刷新聊天信息
     * ******************************************************************************************************/
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void updateMessageUI(String msg, String dir, String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(sqlite_linkmanmss.KEY_ACTNB, chataccount);
        map.put(sqlite_linkmanmss.KEY_DIRECTION, dir);
        map.put(sqlite_linkmanmss.KEY_CONTENT, msg);
        map.put(sqlite_linkmanmss.KEY_TIME, "time");
        map.put(sqlite_linkmanmss.EKY_MESSAGETYPE, type);
        sql.open();
        sql.insertContact(sqlite_linkmanmss.DATABASE_TABLE, map);
        sql.close();
        mdata.add(map);
        //更新数据
        adapt.Refresh();
        chin.setText("");
        chat_room.smoothScrollToPositionFromTop(mdata.size(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MicrophoneServer.TAG, "onActivityResult: onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //位置返回数据处理
            if (requestCode == 1) {
                Bundle pathshot = data.getExtras();
                String btext = pathshot.get("image").toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    updateMessageUI(btext, "send", sqlite_linkmanmss.KEY_MESSAGE_TYPE_IMAGE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            xmppConnection.sendFiles(chataccount,btext,KEY_MESSAGE_TYPE_IMAGE);
                        }
                    }).start();
                }
            } else if (requestCode == 2) {
                //图片返回数据处理
                Bundle bundle = data.getExtras();
                ArrayList mdata = (ArrayList) bundle.getSerializable("picture");
                if (mdata == null || mdata.size() == 0) {
                    return;
                }
                int i;
                for (i = 0; i < mdata.size(); i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        updateMessageUI(mdata.get(i).toString(), "send", sqlite_linkmanmss.KEY_MESSAGE_TYPE_IMAGE);
                        int finalI = i;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                xmppConnection.sendFiles(chataccount,mdata.get(finalI).toString(),KEY_MESSAGE_TYPE_IMAGE);
                            }
                        }).start();
                    }
                }

            } else if (requestCode == 3) {
                //相机返回数据处理
                if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("onActivityResult RESULT_CANCELED" + photopath);
                    //photoDeal.delteImageUri(chat_contrue.this,photopath);
                }
//            发现图片在ImageView上无法显示，原因是图片过大导致的，所以要对于图片进行处理。
//            二次采样   对于图片的宽度和高度进行处理，对于图片的质量进行处理

//            1.获取用于设置图片属性的参数
                BitmapFactory.Options options = new BitmapFactory.Options();
//            2.对于属性进行设置，需要解锁边缘
                options.inJustDecodeBounds = true;
//            3.对于图片进行编码处理
                //BitmapFactory.decodeFile(path,options);
//            4.获取原来图片的宽度和高度
                int outHeight = options.outHeight;
                int outWidth = options.outWidth;
//            5.200,200  获得要压缩的比例
                int sampleHeight = outHeight / 200;  //2
                int sampleWidth = outWidth / 200;    //1.5
//            6.获取较大的比例
                int size = Math.max(sampleHeight, sampleWidth);
//            7.设置图片压缩的比例
                options.inSampleSize = size;
                /**图片的质量   1个字节是8位
                 * ARGB_8888  32位     4字节   100*100*4 = 40000 个字节
                 * ARGB_4444  16位     2字节   100*100*2 = 20000 个字节
                 * RGB_565    16位      2字节  100*100*2 = 20000 个字节
                 * Alpha_8    8位       1字节  100*100*1 = 10000 个字节
                 *
                 * 100px*100px  的图片
                 * */
                options.inPreferredConfig = Bitmap.Config.RGB_565;   //设置图片的质量类型
//            8.锁定边缘
                options.inJustDecodeBounds = false;
                System.out.println(photopath);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    updateMessageUI(String.valueOf(photopath), "send", sqlite_linkmanmss.KEY_MESSAGE_TYPE_IMAGE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            xmppConnection.sendFiles(chataccount,photopath,KEY_MESSAGE_TYPE_IMAGE);
                        }
                    }).start();
                }
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(photopath), options);
                // iv.setImageBitmap(bitmap);
            }
        }
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    // 继承 TRTCCloudListener 回调
     class TRTCCloudListenerImpl extends TRTCCloudListener {
        private WeakReference<chat_contrue> mContext;
        public TRTCCloudListenerImpl(chat_contrue activity) {
            super();
            mContext = new WeakReference<>(activity);
        }
        // 错误通知是要监听的，错误通知意味着 SDK 不能继续运行了
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Log.d(TAG, "sdk callback onError");
            chat_contrue activity = mContext.get();
            if (activity != null) {
                Toast.makeText(activity, "onError: " + errMsg + "[" + errCode+ "]" , Toast.LENGTH_SHORT).show();
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    //activity.exitRoom();
                }
            }
        }
    }

    public void setmVoice(ImageView mVoice) {
        this.mVoice = mVoice;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void SendMessage(Object object) throws XmppStringprepException {
        if (!chin.getText().toString().isEmpty()) {
            Editable msg = chin.getText();
            updateMessageUI(msg.toString(), "send", KEY_MESSAGE_TYPE_STRING);
            xmppConnection.sendMessages(chataccount,msg.toString(),"text");
        }
    }

    /**************************************************
     将内容存储到数据库
     **************************************************/
    public void WriteToDatebase(Map massage) {
        sql.open();
        if (CurrentObject != null) {
            MainActivity.sql.insertContact(CurrentObject, massage);
        }
        sql.close();
    }

    /*******************************************************************
     * 与当前时间比较
     * 如果
     * *****************************************************************/
    public StringBuilder ComparetoCurrent(String time) {
        String lastday = "昨天";
        StringBuilder msqlBuilder = new StringBuilder();
        String[] splittime = time.split("-");
        Date mdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String[] Csliptmdate = sdf.format(mdate).split("-");
        Boolean ret = Integer.parseInt(splittime[2]) < (Integer.parseInt(Csliptmdate[2]) - 1);
        if (splittime[0] != Csliptmdate[0]) {
            msqlBuilder.append(splittime[0] + "-" + splittime[1] + "-" + splittime[2] + " " + splittime[3] + ":" + splittime[4] + ";" + splittime[5]);
            return msqlBuilder;
        } else if ((splittime[1] != Csliptmdate[1]) || ret) {
            msqlBuilder.append(splittime[1] + "-" + splittime[2] + " " + splittime[3] + ":" + splittime[4] + ";" + splittime[5]);
            return msqlBuilder;
        } else if (splittime[2] != Csliptmdate[2]) {
            msqlBuilder.append(lastday + " " + splittime[3] + ":" + splittime[4] + ";" + splittime[5]);
            return msqlBuilder;
        } else {
            msqlBuilder.append(splittime[3] + ":" + splittime[4] + ";" + splittime[5]);
            return msqlBuilder;
        }
    }

    /******************************************************************
     *获取当前时间
     ******************************************************************/
    private StringBuilder GetCurrentTime() {
        Date mdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        StringBuilder mBuilder = new StringBuilder();

        if ((temp != null) && (mdate.getTime() - temp.getTime() < 5000)) {
            isWritetodatebase = false;
        } else {
            mBuilder.append(sdf.format(mdate));
        }
        temp = mdate;
        return mBuilder;
    }

    /*******************************************************************
     * 读取数据库中的数据
     * *****************************************************************/
    private List<Map<String, Object>> LoadData() {
        List<Map<String, Object>> MessagesSqlite = new ArrayList<Map<String, Object>>();
        sql.open();
        //定义数据表结构，并创建数据表，当名字改编后需要修改名字

            Cursor cu = sql.getContact(sqlite_linkmanmss.DATABASE_TABLE,sqlite_linkmanmss.KEY_ACTNB, chataccount);
            if(cu != null) {
                int indexdir = cu.getColumnIndex(sqlite_linkmanmss.KEY_DIRECTION);
                int indextype = cu.getColumnIndex(sqlite_linkmanmss.EKY_MESSAGETYPE);
                int indexmsg = cu.getColumnIndex(sqlite_linkmanmss.KEY_CONTENT);
                int indextime = cu.getColumnIndex(sqlite_linkmanmss.KEY_TIME);
                int indexisnew = cu.getColumnIndex(sqlite_linkmanmss.KEY_ISNEWMESSAGE);
                int indexactnb = cu.getColumnIndex(sqlite_linkmanmss.KEY_ACTNB);
                int indexrowid = cu.getColumnIndex(sqlite_linkmanmss.KEY_ROWID);
                if (cu.moveToFirst()) {
                    do {
                        Map<String, Object> map = new HashMap<>();
                        map.put(sqlite_linkmanmss.KEY_DIRECTION, cu.getString(indexdir));
                        map.put(sqlite_linkmanmss.EKY_MESSAGETYPE, cu.getString(indextype));
                        map.put(sqlite_linkmanmss.KEY_CONTENT, cu.getString(indexmsg));
                        map.put(sqlite_linkmanmss.KEY_TIME, cu.getString(indextime));
                        map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, cu.getString(indexisnew));
                        map.put(sqlite_linkmanmss.KEY_ACTNB, cu.getString(indexactnb));
                        map.put(sqlite_linkmanmss.KEY_ROWID, cu.getString(indexrowid));
                        MessagesSqlite.add(map);
                    }while (cu.moveToNext());
                }
            }
            cu.close();
            sql.close();


        return MessagesSqlite;
    }

    /***********************************************************************
     * 正常录制结束
     ***********************************************************************/
    private void complete() {
        mDialogManager.dismissDialog();
        //mAudioManager.release();
        /*if(mAudioFinishRecorderListener != null && !isComplete){
            mAudioFinishRecorderListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
        }*/
    }

    /*********************************************************************
     * 恢复状态和标志位
     *********************************************************************/
    private void reset() {
        isRecording = false;
        //mReady = false;
        mTime = 0;
        /*isComplete = true;
        changeState(STATE_NORMAL);*/
    }

    /********************************************************************
     * 通过level更新音量资源图片
     * @param level
     ********************************************************************/
    public void updateVoiceLevel(int level) {
        if (alert != null && alert.isShowing()) {
            int resId = mContext.getResources().getIdentifier("v" + level, "mipmap", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println("chat_contrue onDestroy");
        localBroadcastManager.unregisterReceiver(msgUpadteReceiver);

        //mDialogManager.dismissDialog();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
		System.out.println("chat_contrue onBackPressed");
        if (!mEmotionKeyboard.interceptBackPress()) {
            if (isRecording == true) {
                Log.d(TAG, "onBackPressed: mdialogmanager");
                isRecording = false;
                mDialogManager.dismissDialog();
            }
            super.onBackPressed();
        }
    }

    protected void initDatas() {
        replaceFragment();
        List<ImageModel> list = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            if (i == 0) {
                ImageModel model1 = new ImageModel();
                model1.icon = getResources().getDrawable(R.drawable.ic_emotion);
                model1.flag = "经典笑脸";
                model1.isSelected = true;
                list.add(model1);
            } else {
                ImageModel model = new ImageModel();
                model.icon = getResources().getDrawable(R.drawable.ic_plus);
                model.flag = "其他笑脸" + i;
                model.isSelected = false;
                list.add(model);
            }
        }


        //记录底部默认选中第一个
        CurrentPosition = 0;
        SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();
        sharedPreferencedUtils.getPreference(this);
        sharedPreferencedUtils.setInteger(CURRENT_POSITION_FLAG, CurrentPosition);

        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(this, list);
        recyclerview_horizontal.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        recyclerview_horizontal.setAdapter(horizontalRecyclerviewAdapter);
        recyclerview_horizontal.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        //初始化recyclerview_horizontal监听器
        horizontalRecyclerviewAdapter.setOnClickItemListener(new HorizontalRecyclerviewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<ImageModel> datas) {
                //获取先前被点击tab
                int oldPosition = sharedPreferencedUtils.getInteger(CURRENT_POSITION_FLAG, 0);
                //修改背景颜色的标记
                datas.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                CurrentPosition = position;
                datas.get(CurrentPosition).isSelected = true;
                sharedPreferencedUtils.setInteger(CURRENT_POSITION_FLAG, CurrentPosition);
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(CurrentPosition);
                //viewpager界面切换
                viewPager.setCurrentItem(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {
            }
        });


    }

    private void replaceFragment() {
        FragmentManager fManager;
        fManager = getSupportFragmentManager();
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotiomComplateFragment f1 = (EmotiomComplateFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        Bundle bundle;
        for (int i = 0; i < 7; i++) {
            bundle = new Bundle();
            bundle.putString("Interge", "Fragment-" + i);
            Fragment1 fg = Fragment1.newInstance(Fragment1.class, bundle);
            fragments.add(fg);
        }

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(fManager, fragments);
        viewPager.setAdapter(adapter);
    }

    /**
     * 数字与小写字母混编字符串
     *
     * @param size
     * @return
     */
    public static String getNumSmallLetter(int size) {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            if (random.nextInt(2) % 2 == 0) {//字母
                buffer.append((char) (random.nextInt(27) + 'a'));
            } else {//数字
                buffer.append(random.nextInt(10));
            }
        }
        return buffer.toString();
    }
}
