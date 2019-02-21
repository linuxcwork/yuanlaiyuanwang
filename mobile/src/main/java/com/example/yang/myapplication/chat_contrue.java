package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.map3d.demo.basic.MapLocationPosition;
import com.baidu.speech.audio.MicrophoneServer;
import com.example.yang.adapter.GlobalOnItemClickManagerUtils;
import com.example.yang.adapter.HorizontalRecyclerviewAdapter;
import com.example.yang.adapter.NoHorizontalScrollerVPAdapter;
import com.example.yang.audio.AudioManager;
import com.example.yang.audio.DialogManager;
import com.example.yang.fragment.EmotiomComplateFragment;
import com.example.yang.fragment.Fragment1;
import com.example.yang.fragment.FragmentFactory;
import com.example.yang.network.OkHttpManager;
import com.example.yang.ui.AlbumSelectChat;
import com.example.yang.ui.EmotionKeyboard;
import com.example.yang.ui.EmotionUtils;
import com.example.yang.ui.ImageModel;
import com.example.yang.ui.SharedPreferencedUtils;
import com.example.yang.util.PhotoDeal;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_CONTENT;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_DIRECTION;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_NAME;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_TIME;

/**
 * Created by yang on 2018/3/19.
 */

public class chat_contrue extends AppCompatActivity implements View.OnClickListener {

    private ImageButton return_pre;
    private TextView friend_name;
    ImageView chat_friendmessage;
    ListView chat_room;
    ImageView chat_voice;
    ImageView chat_picture_view;
    ImageView else_funtion;
    LinearLayout high_fun;
    private ViewPager funtionviewPagerclass;
    private TextView send;
    private EditText chin;
    private Date temp = null;
    /*dialog 音量显示*/
    private ImageView mVoice;

    /* intent 返回的请求码*/
    public int requestcodemap = 1;   //地图
    public int requestcodeblu = 2;   //相册
    public int requestcodepic = 3;   //拍照


    private String ret;
    //private AlertDialog.Builder builder = null;
    private AlertDialog alert = null;
    private final ThreadLocal<OkHttpManager> services = new ThreadLocal<OkHttpManager>();
    private chat_adapt adapt;
    //public static String[] messdata;//存储文本框中的信息
    private Context mContext;
    private List<WeChatMessage> mdata;
    private String urlname = "https://192.168.0.105/";
    //当前聊天对象
    private String CurrentObject = null;
    //是否将时间写入到数据库，如果当前消息与上一条消息间隔小于5秒则不加
    private Boolean isWritetodatebase = true;
    //表情面板
    private EmotionKeyboard mEmotionKeyboard;

    //private View mEmotionLayout;//表情布局

    /*
    * 声音相关变量*/
    private boolean isRecording = false;//是否在录音状态
    private DialogManager mDialogManager;
    private AudioManager mAudioManager;
    private float mTime;//记录录音时长
    private static final int MSG_DIALOG_DISMISS = 0x112;
    private static final int MSG_AUDIO_COMPLETE = 0x113;//达到最大时长，自动完成
    private static final int MSG_VOICE_CHANGED = 0x111;

    //数据库操作
    private sqlite_linkmanmss sql;
    Calendar calendar=null;

    List<Fragment> fragments=new ArrayList<>();
    private static final String CURRENT_POSITION_FLAG="CURRENT_POSITION_FLAG";
    private int CurrentPosition=0;
    private RecyclerView recyclerview_horizontal;
    private HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;
    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager viewPager;
    //拍照处理类
    PhotoDeal photoDeal = new PhotoDeal();
    Uri photopath;
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_VOICE_CHANGED:
                    updateVoiceLevel(mAudioManager.getVoiceLevel(7));
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

    public chat_contrue() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentalbum = getIntent();
        Bundle bundlealbum = new Bundle();
        bundlealbum = intentalbum.getBundleExtra("picturer");  //bundle 可以存放键值对
        if(intentalbum.getBundleExtra("picturer") != null) {
            ArrayList arrayList = (ArrayList) bundlealbum.get("picturer");
        }

        setContentView(R.layout.chat);

        init_chat();

        sql = new sqlite_linkmanmss(this,"link",null,1);

        //final Intent okintent = new Intent(this, OkHttpManager.class);

        //bindService(okintent,conn,Service.BIND_AUTO_CREATE);
        return_pre.setOnClickListener(this);
        chat_friendmessage.setOnClickListener(this);
        chat_voice.setOnClickListener(this);
        chat_picture_view.setOnClickListener(this);
        /*else_funtion.setOnClickListener(this);*/
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
                if(!editable.toString().isEmpty()){
                    else_funtion.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                }else{
                    else_funtion.setVisibility(View.VISIBLE);
                    send.setVisibility(View.GONE);
                }
            }
        });

        /********************************************************
         * 表情功能部分
         * ******************************************************/
        mEmotionKeyboard =  EmotionKeyboard.with(this);
        mEmotionKeyboard.setEmotionView(findViewById(R.id.ll_emotion_layout));//绑定表情面板
        mEmotionKeyboard.setPagerView(high_fun);
        mEmotionKeyboard.BindToViewPager(this,funtionviewPagerclass);
        mEmotionKeyboard.BindToAddFuntion(else_funtion);
        mEmotionKeyboard.bindToContent(findViewById(R.id.chat_main));
        mEmotionKeyboard.bindToEmotionButton(findViewById(R.id.chat_picture));//绑定表情按钮
        mEmotionKeyboard.bindToEditText(chin);
        mEmotionKeyboard.build();

        initDatas();

        GlobalOnItemClickManagerUtils globalOnItemClickManager= GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickManager.attachToEditText(chin);

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle = intent.getBundleExtra("signale");
        if(intent.getBundleExtra("signale") != null) {
            CurrentObject = bundle.getString("key");
            friend_name.setText(CurrentObject);
        }

        mdata = LoadData();
        adapt = new chat_adapt(this,mdata);
        chat_room.setAdapter(adapt);
        chat_room.smoothScrollToPositionFromTop(mdata.size(),0);
        chat_room.setSelection(mdata.size());
       // Toast.makeText(this,mdata.size(),Toast.LENGTH_LONG).show();
    }

    private void init_chat(){

        return_pre = findViewById(R.id.chat_return);
        friend_name = findViewById(R.id.chat_friendname);
        chat_friendmessage =  findViewById(R.id.chat_friendmessage);
        chat_room =  findViewById(R.id.chatroom);
        chat_voice =  findViewById(R.id.chat_voice);
        chat_picture_view =  findViewById(R.id.chat_picture);
        else_funtion =  findViewById(R.id.chat_add);
        else_funtion.setVisibility(View.VISIBLE);
        send =  findViewById(R.id.chat_send);
        send.setVisibility(View.GONE);
        chin =  findViewById(R.id.chat_input);
        viewPager= findViewById(R.id.vp_emotionview_layout);
        recyclerview_horizontal= findViewById(R.id.recyclerview_horizontal);
        high_fun = findViewById(R.id.high_funtion);
        funtionviewPagerclass = findViewById(R.id.funtionviewpage);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.chat_return:
                /**********************************************
                 * 返回主菜单中的message
                 * ********************************************/
                Intent intent_return = new Intent(chat_contrue.this,MainActivity.class);
                chat_contrue.this.startActivity(intent_return);
            break;
            case R.id.chat_friendmessage:
                /***********************************************
                 * 跳转到正在聊天的人员的个人信息页面
                 * *********************************************/
                Intent intent_information = new Intent(chat_contrue.this,CurrentFriend.class);
                this.startActivity(intent_information);
                break;
            case R.id.chat_voice:
                /************************************************
                 * 进入语音聊天模式
                 * **********************************************/
            {

                mDialogManager = new DialogManager(this);
                String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "YLYW" + File.separator + "voice";
                mAudioManager = AudioManager.getInstance(path);
                mAudioManager.prepareAudio();
                mDialogManager.showRecordingDialog(new DialogManager.AudioFinishRecorderListener() {
                    @Override
                    public void onFinish(float seconds, String filePath,String flag) {
                        if(flag.equals("send")){
                            WeChatMessage Message = new WeChatMessage(WeChatMessage.MessageType_To,null, null);
                            mdata.add(Message);
                            //更新数据
                            adapt.Refresh();
                            chat_room.smoothScrollToPositionFromTop(mdata.size(), 0);
                            //mDialogManager.dismissDialog();
                            mAudioManager.release();
                        }else if(flag.equals("cancle")){
                           // mDialogManager.dismissDialog();
                            mAudioManager.cancel();
                        }
                    }
                });
                isRecording = true;
                new Thread(mGetVoiceLevelRunnable).start();
            }
                break;
            case R.id.chat_send:
                SendMessage(null);
                break;
            case R.id.chat_add:
                high_fun.setVisibility(View.VISIBLE);
                break;
            /* 聊天界面的获取相册中图片*/
            case R.id.funtionalbum:
                String albumpath = Environment.getExternalStorageDirectory().getPath()+File.separator+"YLYW"+File.separator+"albumtemp";
                /* 开启Pictures画面Type设定为image */
                Intent intent = new Intent(this,AlbumSelectChat.class);
                //Intent intent = new Intent();
                //intent.setType("image/*");
                /*使用Intent.ACTION_GET_CONTENT这个Action */
                //intent.setAction(Intent.ACTION_PICK);
                /* 取得相片后返回本画面 */
                intent.putExtra("activity","chat_contrue");
                startActivityForResult(intent,requestcodeblu);
                break;
            /* 聊天界面的拍照功能 */
            case R.id.funtiontakepicture:
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();
                //相片保存地址
                photopath = photoDeal.createImageUri(chat_contrue.this);//Environment.getExternalStorageDirectory().getPath()+File.separator+"YLYW"+File.separator+"album";
                Intent intent_cap = new Intent();
                // 指定开启系统相机的Action
                intent_cap.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                //从这
                intent_cap.addCategory(Intent.CATEGORY_DEFAULT);

                // 设置系统相机拍摄照片完成后图片文件的存放地址
                intent_cap.putExtra(MediaStore.EXTRA_OUTPUT, photopath);
                //到这不用设置的话会在onActivityResult方法里，在意图获取一个处理过的bitmap
                startActivityForResult(intent_cap, requestcodepic);
                break;
            /* 聊天界面获取当前位置*/
            case R.id.funtionposition:
                MapLocationPosition mapLocationPosition = new MapLocationPosition();
                Intent intentchatmap = new Intent(chat_contrue.this,MapLocationPosition.class);
                intentchatmap.putExtra(mapLocationPosition.MAPRESOURCE,"chat_contrue");
                startActivityForResult(intentchatmap,requestcodemap);
                break;
                default:
                    break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MicrophoneServer.TAG, "onActivityResult: onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            //位置返回数据处理
            if (requestCode == 1) {
                String pathshot = data.getExtras().getString("data");

            } else if(requestCode == 2){
                //图片返回数据处理
                Bundle bundle= data.getExtras();

                System.out.println(bundle);
                /*if (bm != null) {
                    GifTextView Content_to = (GifTextView) mView.findViewById(R.id.To_Content);
                    // Content_to.setSpanText(bm);
                }*/
            }else if (requestCode == 3) {
                //相机返回数据处理
                if(resultCode == Activity.RESULT_CANCELED){
                    System.out.println("onActivityResult RESULT_CANCELED"+photopath);
                    photoDeal.delteImageUri(chat_contrue.this,photopath);
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
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(photopath),options);
                // iv.setImageBitmap(bitmap);
            }
        }
    }

    public void setmVoice(ImageView mVoice) {
        this.mVoice = mVoice;
    }

    public class WeChatMessage
    {
        //定义3种布局类型
        public static final int MessageType_Time=0;
        public static final int MessageType_From=1;
        public static final int MessageType_To=2;
        private int connect_type;


        private Long id;
        private String UserId;
        private String UserName;
        private String UserHeadIcon;
        private String UserContent;
        private String time;
        private int type;
        private int messagetype;
        private float UserVoiceTime;
        private String UserVoicePath;
        private String UserVoiceUrl;
        private int sendState;
        private String imageUrl;
        private String imageIconUrl;
        private String imageLocal;

        public WeChatMessage(int Type,Editable editable,String time)
        {
            this.mType=Type;
            this.mContent=editable;
            this.MessageTime = time;
        }

        public WeChatMessage(Long id, String UserId, String UserName,
                             String UserHeadIcon, String UserContent, String time, int type,
                             int messagetype, float UserVoiceTime, String UserVoicePath,
                             String UserVoiceUrl, int sendState, String imageUrl,
                             String imageIconUrl, String imageLocal){
            this.id = id;
            this.UserId = UserId;
            this.UserName = UserName;
            this.UserHeadIcon = UserHeadIcon;
            this.UserContent = UserContent;
            this.time = time;
            this.type = type;
            this.messagetype = messagetype;
            this.UserVoiceTime = UserVoiceTime;
            this.UserVoicePath = UserVoicePath;
            this.UserVoiceUrl = UserVoiceUrl;
            this.sendState = sendState;
            this.imageUrl = imageUrl;
            this.imageIconUrl = imageIconUrl;
            this.imageLocal = imageLocal;
        }

        //消息类型
        private int mType;
        //对方ID
        private String value;
        //消息内容
        private Editable mContent;
        private String MessageTime;
        //获取类型
        public int getType() {
            return mType;
        }
        //获取消息内容类型
        /*
        * 1 ： 文字
        * 2 ： 图片
        * 3 ： 文本文件
        * 4 ： 视频*/
        public int getConnect_type(){return connect_type;}
        //设置类型
        public void setType(int mType) {
            this.mType = mType;
        }
        //获取对方
        public String getValue() {
            return value;
        }
        //获取内容
        public Editable getContent() {
            return mContent;
        }
        //设置内容
        public void setContent(Editable mContent) {
            this.mContent = mContent;
        }

        //获取内容
        public String getMessageTime() {
            return MessageTime;
        }
        //设置内容
        public void setMessageTime(String mContent) {
            this.MessageTime = mContent;
        }

        public Long getId() {
            return this.id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUserId() {
            return this.UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        public String getUserName() {
            return this.UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public String getUserHeadIcon() {
            return this.UserHeadIcon;
        }

        public void setUserHeadIcon(String UserHeadIcon) {
            this.UserHeadIcon = UserHeadIcon;
        }

        public String getUserContent() {
            return this.UserContent;
        }

        public void setUserContent(String UserContent) {
            this.UserContent = UserContent;
        }

        public String getTime() {
            return this.time;
        }

        public void setTime(String time) {
            this.time = time;
        }

       /* public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }*/

        public int getMessagetype() {
            return this.messagetype;
        }

        public void setMessagetype(int messagetype) {
            this.messagetype = messagetype;
        }

        public float getUserVoiceTime() {
            return this.UserVoiceTime;
        }

        public void setUserVoiceTime(float UserVoiceTime) {
            this.UserVoiceTime = UserVoiceTime;
        }

        public String getUserVoicePath() {
            return this.UserVoicePath;
        }

        public void setUserVoicePath(String UserVoicePath) {
            this.UserVoicePath = UserVoicePath;
        }

        public String getUserVoiceUrl() {
            return this.UserVoiceUrl;
        }

        public void setUserVoiceUrl(String UserVoiceUrl) {
            this.UserVoiceUrl = UserVoiceUrl;
        }

        public int getSendState() {
            return this.sendState;
        }

        public void setSendState(int sendState) {
            this.sendState = sendState;
        }

        public String getImageUrl() {
            return this.imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageIconUrl() {
            return this.imageIconUrl;
        }

        public void setImageIconUrl(String imageIconUrl) {
            this.imageIconUrl = imageIconUrl;
        }

        public String getImageLocal() {
            return this.imageLocal;
        }

        public void setImageLocal(String imageLocal) {
            this.imageLocal = imageLocal;
        }
    }

    public void SendMessage(Object object) {
        if (!chin.getText().toString().isEmpty()) {

            Editable msg = chin.getText();
            Toast.makeText(this,msg, Toast.LENGTH_LONG).show();
            //获取时间
            StringBuilder mBuilder = GetCurrentTime();

            Map map = new HashMap();
            map.put(KEY_CONTENT, msg);
            map.put(KEY_NAME, CurrentObject);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    services.get().postKeyValuePaires(urlname, map, new HttpResponse() {
                        @Override
                        public void succesd(Call call, Object response) {

                        }

                        @Override
                        public void failed(Call call, IOException e) {

                        }
                    });
                    if (ret == null) {
                        //Toast.makeText(this,"no response",Toast.LENGTH_LONG).show();
                    }
                }
            });

            Date mtime = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Map map_SQL = new HashMap();
            map_SQL.put(KEY_CONTENT, msg.toString());
            map_SQL.put(KEY_NAME, CurrentObject);
            map_SQL.put(KEY_DIRECTION, "right");
            if(isWritetodatebase) {
                map_SQL.put(KEY_TIME, sdf.format(mtime));
            }
            WriteToDatebase(map_SQL);

            //构造时间消息
            WeChatMessage Message = new WeChatMessage(WeChatMessage.MessageType_Time, null,mBuilder.toString());
            if (mBuilder.length() > 0) {
                mdata.add(Message);
            }
            //构造输入消息
            Message = new WeChatMessage(WeChatMessage.MessageType_To, msg,null);
            mdata.add(Message);
            //构造返回消息，如果这里加入网络的功能，那么这里将变成一个网络机器人
            Message = new WeChatMessage(WeChatMessage.MessageType_From, msg,null);
            mdata.add(Message);

            //更新数据
            adapt.Refresh();
            //清空输入框
            chin.setText("");
            //关闭输入法
            //滚动列表到当前消息
            chat_room.smoothScrollToPositionFromTop(mdata.size(), 0);

        }
    }

    /**************************************************
     将内容存储到数据库
     **************************************************/
    public void WriteToDatebase(Map massage){
        sql.open();
        if(CurrentObject != null){
            sql.insertContact(CurrentObject,massage);
        }
        sql.close();
    }

    /*******************************************************************
    * 与当前时间比较
     * 如果
     * *****************************************************************/
    public StringBuilder ComparetoCurrent(String time){
        String lastday = "昨天";
        StringBuilder msqlBuilder = new StringBuilder();
        String [] splittime = time.split("-");
        Date mdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String [] Csliptmdate = sdf.format(mdate).split("-");
        Boolean ret = Integer.parseInt(splittime[2]) < (Integer.parseInt(Csliptmdate[2]) - 1);
        if(splittime[0] != Csliptmdate[0]){
            msqlBuilder.append(splittime[0]+"-"+splittime[1]+"-"+splittime[2]+ " "+splittime[3]+":"+splittime[4]+";"+splittime[5]);
            return msqlBuilder;
        }else if((splittime[1] != Csliptmdate[1]) || ret){
            msqlBuilder.append(splittime[1]+"-"+splittime[2]+ " "+splittime[3]+":"+splittime[4]+";"+splittime[5]);
            return msqlBuilder;
        }else if(splittime[2] != Csliptmdate[2]){
            msqlBuilder.append(lastday+ " "+splittime[3]+":"+splittime[4]+";"+splittime[5]);
            return msqlBuilder;
        }else{
            msqlBuilder.append(splittime[3] + ":" + splittime[4] + ";" + splittime[5]);
            return msqlBuilder;
        }
    }
    /******************************************************************
     *获取当前时间
     ******************************************************************/
    private StringBuilder GetCurrentTime(){
        Date mdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        StringBuilder mBuilder = new StringBuilder();

        if((temp != null) && (mdate.getTime() - temp.getTime() < 5000)) {
            isWritetodatebase = false;
        }else {
            mBuilder.append(sdf.format(mdate));
        }
        temp = mdate;
        return mBuilder;
    }
    /*******************************************************************
     * 读取数据库中的数据
     * *****************************************************************/
    private List<WeChatMessage> LoadData()
    {
        WeChatMessage  Message;
        List<WeChatMessage> MessagesSqlite=new ArrayList<WeChatMessage>();

        boolean ret=sql.open();
        //定义数据表结构，并创建数据表，当名字改编后需要修改名字
        String createtable = "create table if not exists"+ " " +CurrentObject+"( _id integer primary key autoincrement, " +
                "name text, " +
                "email text," +
                "actnb text," +
                "telphone text," +
                "id_card text," +
                "the_signature text," +
                "marriage text," +
                "content NONE," +
                "time text," +
                "direction text);";
        Log.d(TAG, "LoadData: "+createtable);
        sql.CreateTable(createtable);
        if(ret) {
            Cursor cu = sql.getContact(CurrentObject,CurrentObject,KEY_CONTENT);
            if (cu != null && cu.moveToFirst()) {
                while (!cu.isAfterLast()) {

                    String time = cu.getString(cu.getColumnIndex(KEY_TIME));
                    if (time != null) {
                        /*StringBuilder timerelate = ComparetoCurrent(time);
                        Message = new WeChatMessage(WeChatMessage.MessageType_Time, null,timerelate.toString());
                        MessagesSqlite.add(Message);*/
                    }
                    String dirction = cu.getString(cu.getColumnIndex(KEY_DIRECTION));
                    if(dirction.equals("right")) {
                        /*byte[] n = cu.getBlob(cu.getColumnIndex(KEY_CONTENT));
                        Message = new WeChatMessage(WeChatMessage.MessageType_To,cu.getBlob(cu.getColumnIndex(KEY_CONTENT)),null);
                        MessagesSqlite.add(Message);*/
                    }else if(dirction.equals("left")){
                        /*Message = new WeChatMessage(WeChatMessage.MessageType_From,(Editable) cu.getBlob(cu.getColumnIndex(KEY_CONTENT)),null);
                        mdata.add(Message);*/
                    }
                    cu.moveToNext();
                }

            }

            cu.close();
            sql.close();
        }

        return MessagesSqlite;
    }

    /***********************************************************************
     * 正常录制结束
     ***********************************************************************/
    private void complete() {
        mDialogManager.dismissDialog();
        mAudioManager.release();
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
    public void updateVoiceLevel(int level){
        if(alert != null && alert.isShowing()){
            int resId = mContext.getResources().getIdentifier("v"+level,"mipmap",mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }

    class Recorder{

        float time;
        String filePath;

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Recorder(float time, String filePath) {
            super();
            this.time = time;
            this.filePath = filePath;
        }
    }

    /*@Override
    protected void onDestroy() {

            mDialogManager.dismissDialog();
            super.onDestroy();
    }*/

    @Override
    public void onBackPressed() {
        if(!mEmotionKeyboard.interceptBackPress()) {
            if(isRecording == true) {
                Log.d(TAG, "onBackPressed: mdialogmanager");
                isRecording = false;
                mDialogManager.dismissDialog();
            }
            super.onBackPressed();
        }
    }

    protected void initDatas(){
        replaceFragment();
        List<ImageModel> list = new ArrayList<>();
        for (int i=0 ; i<fragments.size(); i++){
            if(i==0){
                ImageModel model1=new ImageModel();
                model1.icon= getResources().getDrawable(R.drawable.ic_emotion);
                model1.flag="经典笑脸";
                model1.isSelected=true;
                list.add(model1);
            }else {
                ImageModel model = new ImageModel();
                model.icon = getResources().getDrawable(R.drawable.ic_plus);
                model.flag = "其他笑脸" + i;
                model.isSelected = false;
                list.add(model);
            }
        }


        //记录底部默认选中第一个
        CurrentPosition=0;
        SharedPreferencedUtils.setInteger(this, CURRENT_POSITION_FLAG, CurrentPosition);

        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(this,list);
        recyclerview_horizontal.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        recyclerview_horizontal.setAdapter(horizontalRecyclerviewAdapter);
        recyclerview_horizontal.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        //初始化recyclerview_horizontal监听器
        horizontalRecyclerviewAdapter.setOnClickItemListener(new HorizontalRecyclerviewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<ImageModel> datas) {
                //获取先前被点击tab
                int oldPosition = SharedPreferencedUtils.getInteger(chat_contrue.this, CURRENT_POSITION_FLAG, 0);
                //修改背景颜色的标记
                datas.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                CurrentPosition = position;
                datas.get(CurrentPosition).isSelected = true;
                SharedPreferencedUtils.setInteger(chat_contrue.this, CURRENT_POSITION_FLAG, CurrentPosition);
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(CurrentPosition);
                //viewpager界面切换
                viewPager.setCurrentItem(position,false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {
            }
        });



    }

    private void replaceFragment(){
        FragmentManager fManager;
        fManager = getSupportFragmentManager();
        //创建fragment的工厂类
        FragmentFactory factory=FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotiomComplateFragment f1= (EmotiomComplateFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        Bundle bundle;
        for (int i=0;i<7;i++){
            bundle=new Bundle();
            bundle.putString("Interge","Fragment-"+i);
            Fragment1 fg= Fragment1.newInstance(Fragment1.class,bundle);
            fragments.add(fg);
        }

        NoHorizontalScrollerVPAdapter adapter =new NoHorizontalScrollerVPAdapter(fManager,fragments);
        viewPager.setAdapter(adapter);
    }

}
