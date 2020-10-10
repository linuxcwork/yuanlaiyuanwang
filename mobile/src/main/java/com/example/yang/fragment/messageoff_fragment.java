package com.example.yang.fragment;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.yang.Activity.ChatRTCVoiceIncommingActivity;
import com.example.yang.Activity.RecommendFriendActivity;
import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.chat_contrue;
import com.example.yang.myapplication.signallistview;
import com.example.yang.myapplication.sqlite_linkmanmss;
import com.example.yang.util.PhotoDeal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.yang.Activity.ChatRTCVoiceActivity.CHATRTCVOICE;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_STRING;

/**
 * Created by yang on 2018/3/18.
 */

public class messageoff_fragment extends Fragment {

    private final String TAG = "messageoff_fragment";
    private ListView listView;
    private List<Map<String, Object>> listall=new ArrayList<Map<String,Object>>();
    private MsgUpadteReceiver msgUpadteReceiver;
    private sqlite_linkmanmss sql;
	private LocalBroadcastManager localBroadcastManager;
    private final int UPDATEUI = 0;
	private static String curname;
	
	private final String MSGTYPERECIMMEND = "recommend";
	public static final String MSGTYPECHATTEXT = "text";
	private final String MSGTYPECHATFILE = "file";
	private final String MSGTYPECHATIMAGE = "image";
	private final String MSGTYPECHATGIF = "gif";

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATEUI:
                    Intent clieckintent = new Intent(getActivity(), chat_contrue.class);;
			/*	if(intent.getAction().equals("com.example.yang.Bluetoothutil")){
                     clieckintent = new Intent(getContext(), RecommendFriendActivity.class); //点击了之后进入的一个Actity
				}else {
					 clieckintent = new Intent(getContext(), chat_contrue.class); //点击了之后进入的一个Actity
				}*/
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, clieckintent, PendingIntent.FLAG_ONE_SHOT);
                    Notification noti = new Notification.Builder(getContext())
                            .setTicker("名")
                            .setContentTitle("标题")
                            .setContentText("时间")
                            .setSmallIcon(R.drawable.default_video_icon)//设置图标
                            .setAutoCancel(true)//点击之后消失
                            .setDefaults(Notification.DEFAULT_SOUND)//设置声音
                            .setContentIntent(pendingIntent)//点击之后的页面
                            .build();
                    NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(1, noti);//id控制第几条，相同的id会替换掉上一次的消息通知*/

                    listView.setAdapter(new signallistview(getActivity(), listall));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public  messageoff_fragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View view= inflater.inflate(R.layout.messageoffriend_fragment, container, false);
        sql = new sqlite_linkmanmss(getActivity(), "link", null, 1);
        listView = (ListView)view.findViewById(R.id.list_signal);
        listView.setAdapter(new signallistview(getActivity(), getData()));
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo am = (AdapterView.AdapterContextMenuInfo) menuInfo;
                int pos = (int)listView.getAdapter().getItemId(am.position);
				if(listall.get(pos).get(sql.KEY_ISNEWMESSAGE).equals("0")) {
                    menu.add(0, 0, menu.NONE, "标记为未读");
                }else {
                    menu.add(0, 3, menu.NONE, "标记为已读");
                }
                menu.add(0,1,menu.NONE,"置顶聊天");
                menu.add(0,2,menu.NONE,"删除该聊天");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listall != null && listall.size() != 0){
                    listall.get(position).put(sqlite_linkmanmss.KEY_ISNEWMESSAGE,"0");
                    listView.setAdapter(new signallistview(getActivity(), listall));
                    Object type = listall.get(position).get(sqlite_linkmanmss.EKY_MESSAGETYPE);
					if(type != null && type.toString().equals(MSGTYPERECIMMEND)){
						Intent intent = new Intent(view.getContext(), RecommendFriendActivity.class);
                        view.getContext().startActivity(intent);
					}else{
                       Object account =  listall.get(position).get(sqlite_linkmanmss.KEY_ACTNB);
                       if(account != null){
                           maeeage_onClick(view,account.toString());
                       }
					}
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.yang.updaterecv");
        intentFilter.addAction("com.example.yang.Bluetoothutil");
        msgUpadteReceiver = new MsgUpadteReceiver();
        //注册广播
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        //本地广播管理器注册广播接收器
        localBroadcastManager.registerReceiver(msgUpadteReceiver, intentFilter);
        return view;
    }

    public class MsgUpadteReceiver extends BroadcastReceiver {
        @SuppressLint("ResourceType")
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("account");
            String type = intent.getStringExtra("type");
            String data = intent.getStringExtra("path");
            String time = intent.getStringExtra("time");
            Log.d(TAG,"MsgUpadteReceiver onReceive: "+name);
			if(name == null){
				return ;
			}

            if (curname != null && curname.equals(name)) {
                int count = 0;
                Map<String, String> map = new HashMap<String, String>();
                map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, String.valueOf(count));
                sql.open();
                sql.updateContact(MainActivity.friendinfotable, sqlite_linkmanmss.KEY_ACTNB, name, map);
                sql.close();
            }

            if (intent.getAction().equals("com.example.yang.updaterecv")) {
                //String type = intent.getStringExtra("type");
                sql.open();
                Map<String, Object> map = new HashMap<>();
                Cursor cursor = sql.getContact(MainActivity.friendinfotable, sql.KEY_ACTNB, name);
                if (cursor != null && cursor.moveToFirst() && cursor.getCount()>0) {
                    map.put(sql.KEY_ROWID, cursor.getInt(cursor.getColumnIndex(sql.KEY_ROWID)));
                    if(type != null && type.equals(KEY_MESSAGE_TYPE_STRING)){
                        map.put(sql.KEY_CONTENT, cursor.getString(cursor.getColumnIndex(sql.KEY_CONTENT)));
                    }else {
                        map.put(sql.KEY_CONTENT,cursor.getString(cursor.getColumnIndex(sql.KEY_NAME))+":[图片]");
                    }

                    map.put(sql.KEY_TIME, cursor.getString(cursor.getColumnIndex(sql.KEY_TIME)));
                    map.put(sql.KEY_ISNEWMESSAGE, cursor.getString(cursor.getColumnIndex(sql.KEY_ISNEWMESSAGE)));
                    map.put(sql.KEY_NAME, cursor.getString(cursor.getColumnIndex(sql.KEY_NAME)));
                    map.put(sql.KEY_ACTNB, cursor.getString(cursor.getColumnIndex(sql.KEY_ACTNB)));
                    map.put(sql.EKY_MESSAGETYPE, MSGTYPECHATTEXT);
                }
                int i = 0;
                for (i = 0; i < listall.size(); i++) {
                    if (Objects.equals(listall.get(i).get(sqlite_linkmanmss.KEY_ACTNB), name)) {
                        remove(i);
                    }
                }
                add(0, map);
                sql.close();
                Message msg = new Message();
                msg.what = UPDATEUI;
                mhandler.sendMessage(msg);
                if(type.equals(sqlite_linkmanmss.KEY_MESSAGE_TYPE_CALL)){
                    Intent callring = new Intent(getActivity(), ChatRTCVoiceIncommingActivity.class);
                    callring.putExtra("account",name);
                    callring.putExtra("roomid",intent.getStringExtra("type"));
                    startActivity(callring);
                }else if(type.equals(sqlite_linkmanmss.KEY_MESSAGE_TYPE_REJECT_CALL) || type.equals(sqlite_linkmanmss.KEY_MESSAGE_TYPE_FINESH_CALL)){
                    Intent recvintent = new Intent();
                    recvintent.setAction(CHATRTCVOICE);
                    recvintent.putExtra("account",name);
                    //创建一个本地广播管理器
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
                    //发送本地广播
                    localBroadcastManager.sendBroadcast(recvintent);
                }
            } else if (intent.getAction().equals("com.example.yang.Bluetoothutil")) {
                sql.open();
                Cursor cursor = sql.getContact(MainActivity.friendinfotable, sqlite_linkmanmss.EKY_MESSAGETYPE, MSGTYPERECIMMEND);

                if (cursor != null && cursor.moveToFirst() && cursor.getCount()>0) {
                    int i;
                    for (i = 0; i < listall.size(); i++) {
                        if (Objects.equals(listall.get(i).get(sqlite_linkmanmss.KEY_NAME), "系统推荐")) {
                            Map<String, Object> map = (Map<String, Object>) listall.get(i);
                            listall.remove(i);
                            int amount =  Integer.parseInt(map.get(sqlite_linkmanmss.KEY_ISNEWMESSAGE).toString()) + 1;
                            map.put(sqlite_linkmanmss.KEY_CONTENT, "推荐 " + name);
                            map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, String.valueOf(amount));
                            add(0, map);
                        }
                    }
                } else {
                    Map<String,Object> map = new HashMap<>();
                    Resources res=getResources();
                    Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.album);
                    byte[] bitdata = PhotoDeal.flattenBitmap(bmp);
                    map.put(sql.KEY_ROWID, Base64.encodeToString(bitdata, 0));
                    map.put(sqlite_linkmanmss.KEY_CONTENT, "推荐 " + name);
                    map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, "1");
                    map.put(sqlite_linkmanmss.KEY_NAME,"系统推荐");
                    map.put(sql.EKY_MESSAGETYPE, MSGTYPERECIMMEND);
                    add(0, map);
                    sql.insertContact(MainActivity.friendinfotable, map);
                }
                sql.close();
                Message msg = new Message();
                msg.what = UPDATEUI;
                mhandler.sendMessage(msg);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int pos = (int)listView.getAdapter().getItemId(menuInfo.position);
        Map<String, Object> map= listall.get(pos);
        switch (item.getItemId()){
            case 0:
				remove(pos);
			    map.put(sql.KEY_ISNEWMESSAGE,"1");
                add(0,map);
                break;
            case 1:
				remove(pos);
                add(0,map);					
                break;
            case 2:
                if(listall.remove(pos) != null){
                    System.out.println("succese");
                }                
                break;
			case 3:
			    remove(pos);
			    map.put(sql.KEY_ISNEWMESSAGE,"0");
                add(0,map);
				break;
        }
		listView.setAdapter(new signallistview(getActivity(), listall));
        return super.onContextItemSelected(item);
    }

    public synchronized void maeeage_onClick(View view,String name) {
		//如果当前为推荐信息，则跳转到推荐页面
		if(name.equals("tuijain")){
			
		}else{
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            Intent intent = new Intent(view.getContext(), chat_contrue.class);
            intent.putExtras(bundle);
            view.getContext().startActivity(intent);
        }
    }

    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        listall.clear();
        Log.d(TAG,"getData SIZE "+listall.size());
        boolean ret = sql.open();
        //定义数据表结构，并创建数据表，当名字改编后需要修改名字
        if (ret) {
            Cursor cu = sql.getAllContacts(MainActivity.friendinfotable);
            if(cu != null) {
                int indeximage = cu.getColumnIndex(sql.KEY_ROWID);
                int indexactnb = cu.getColumnIndex(sql.KEY_ACTNB);
                int indexmsg = cu.getColumnIndex(sql.KEY_CONTENT);
                int indextime = cu.getColumnIndex(sql.KEY_TIME);
                int indexisnew = cu.getColumnIndex(sql.KEY_ISNEWMESSAGE);
                int indexname = cu.getColumnIndex(sql.KEY_NAME);
                int indextype = cu.getColumnIndex(sql.EKY_MESSAGETYPE);
                if (cu.moveToFirst()) {
                     do {
                        Map<String, Object> map = new HashMap<>();
                        map.put(sql.KEY_ROWID, cu.getString(indeximage));
                        map.put(sql.KEY_CONTENT, cu.getString(indexmsg));
                        map.put(sql.KEY_TIME, cu.getString(indextime));
                        map.put(sql.KEY_ISNEWMESSAGE, cu.getString(indexisnew));
                        map.put(sql.KEY_NAME, cu.getString(indexname));
                        map.put(sql.KEY_ACTNB, cu.getString(indexactnb));
                        map.put(sql.EKY_MESSAGETYPE, cu.getString(indextype));
                        System.out.println(map);
                        list.add(map);
                    }while (cu.moveToNext());
                }
            }else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(sql.KEY_ROWID, R.drawable.ic_launcher_background);
                    map.put(sql.KEY_NAME, "系统服务");
                    map.put(sql.KEY_CONTENT, "您好");
                    map.put(sql.KEY_ISNEWMESSAGE, 0);
                    map.put(sql.KEY_ACTNB, "系统服务");
                    sql.insertContact(MainActivity.friendinfotable, map);
                    list.add(map);
            }
            sql.close();
        }
        listall = list;
        return list;
    }
	
	//往特定位置，添加一个元素
    private void add(int position,Map<String, Object> data){
      if (listall == null) {
        listall = new LinkedList<>();
      }
      listall.add(position,data);
    }
	
	//删除指定位置元素
    private void remove(int position) {
      if(listall != null) {
          listall.remove(position);
      }
    }
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(msgUpadteReceiver);
    }
}
