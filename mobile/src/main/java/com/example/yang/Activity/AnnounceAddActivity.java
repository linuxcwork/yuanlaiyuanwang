package com.example.yang.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.myapplication.R;
import com.example.yang.util.FileOperationUtil;
import com.example.yang.util.PhotoDeal;
import com.example.yang.util.SystemUtil;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocatedCity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.Activity
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/10/13 20:20
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class AnnounceAddActivity extends AppCompatActivity implements View.OnClickListener {
    private final int ANNOUNCEIMAGECODE = 0;
    private final int ANNOUNCEPOSITIONCODE = 1;
    private ImageView billimage;
    private EditText title;
    private EditText sponsor;
    private EditText detialaddr;
    private EditText telephone;
    private EditText account;
    private EditText charge;
    private EditText note;
    private TextView announcedatestart;
    private TextView announcetimestart;
    private TextView announcedateend;
    private TextView announcetimeend;
    private TextView city;
    private Calendar calendar= Calendar.getInstance(Locale.CHINA);
    private AnnounceMsg announceMsg = new AnnounceMsg();
    private int tyear = calendar.get(Calendar.YEAR);
    private int tmonth = calendar.get(Calendar.MONTH);
    private int tday = calendar.get(Calendar.DAY_OF_MONTH);
    private int thour = calendar.get(Calendar.HOUR_OF_DAY);
    private Map<String,String> map=new HashMap<String,String>();
    private File file;

    //开始定位
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case ANNOUNCEIMAGECODE:
                    Glide
                            .with(AnnounceAddActivity.this)
                            .load(msg.obj.toString())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(120, 160)
                            .dontAnimate()
                            .centerCrop()
                            .into(billimage);
                    break;
                case ANNOUNCEPOSITIONCODE:
                    city.setText(msg.obj.toString());
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announce_add_activity);
        initView();
        FileOperationUtil.CreateDir(FileOperationUtil.SECONDFILEDIRPATH);
        FileOperationUtil.CreateDir(FileOperationUtil.THIRDANNOUNCEADDDIRPATH);
        file = FileOperationUtil.CreateFile(FileOperationUtil.THIRDANNOUNCEADDDIRPATH+File.separator+ SystemUtil.getNumSmallLetter(32)+".json");
    }

    private void initView(){
        ImageView returnimage = findViewById(R.id.anounce_add_activity_return);
        billimage = findViewById(R.id.announce_add_activity_bill);
        title = findViewById(R.id.announce_add_activity_title);
        sponsor = findViewById(R.id.announce_add_activity_sponsor);
        announcedatestart = findViewById(R.id.announce_add_activity_date_start);
        announcetimestart = findViewById(R.id.announce_add_activity_time_start);
        announcedateend = findViewById(R.id.announce_add_activity_date_end);
        announcetimeend = findViewById(R.id.announce_add_activity_time_end);
        city = findViewById(R.id.announce_add_activity_addr);
        detialaddr = findViewById(R.id.announce_add_activity_detial_addr);
        telephone = findViewById(R.id.announce_add_activity_telephone);
        account = findViewById(R.id.announce_add_activity_account);
        charge = findViewById(R.id.announce_add_activity_charge);
        note = findViewById(R.id.announce_add_activity_note);
        Button cancel = findViewById(R.id.announce_add_activity_cancel);
        Button publish = findViewById(R.id.announce_add_activity_publish);
        charge.setVisibility(View.GONE);
        returnimage.setOnClickListener(this);
        billimage.setOnClickListener(this);
        city.setOnClickListener(this);
        cancel.setOnClickListener(this);
        publish.setOnClickListener(this);
        announcedatestart.setOnClickListener(this);
        announcedatestart.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
        announcedateend.setOnClickListener(this);
        announcedateend.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
        announcetimestart.setOnClickListener(this);
        announcetimestart.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
        announcetimeend.setOnClickListener(this);
        announcetimeend.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
    }

    /***************************************************************
     * 上传announce信息
     * *************************************************************/
    public void uploadAnnounce(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(title.getText() != null) {
                    map.put(announceMsg.ANNOUNCETITLE, title.getText().toString());
                }else{
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce title",Toast.LENGTH_LONG);
                    Looper.loop();
                }
                if(sponsor.getText() != null){
                    map.put(announceMsg.ANNOUNCESPONSOR,sponsor.getText().toString());
                }else{
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce sponsor",Toast.LENGTH_LONG);
                    Looper.loop();
                }
                if(city.getText() != null){
                    map.put(announceMsg.ANNOUNCEADDR,city.getText().toString());
                }else{
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce city",Toast.LENGTH_LONG);
                    Looper.loop();
                }
                if(detialaddr.getText() != null){
                    map.put(announceMsg.SNNOUNCEDETIALED,detialaddr.getText().toString());
                }else {
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce detialed",Toast.LENGTH_LONG);
                    Looper.loop();
                }
                if(note.getText() != null){
                    map.put(announceMsg.ANNOUNCENOTE,note.getText().toString());
                }else {
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce note",Toast.LENGTH_LONG);
                    Looper.loop();
                }
                if(map.get(announceMsg.ANNOUNCEDATESTART) == null || map.get(announceMsg.ANNOUNCEDATEEND) == null){
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce date",Toast.LENGTH_LONG);
                    Looper.loop();
                }
                if(map.get(announceMsg.ANNOUNCETIMESTART) == null || map.get(announceMsg.ANNOUNCETIMEEND) == null){
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce time",Toast.LENGTH_LONG);
                    Looper.loop();
                }
                if(map.get(announceMsg.ANNOUNCEIMAGE) == null){
                    Looper.prepare();
                    Toast.makeText(AnnounceAddActivity.this,"please intput this announce image",Toast.LENGTH_LONG);
                    Looper.loop();
                }

                try {
                    FileOperationUtil.saveSingleJson(map,file.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.anounce_add_activity_return:
            case R.id.announce_add_activity_cancel:
                Intent imagereturn = new Intent(this,AnnounceActivity.class);
                startActivity(imagereturn);
                break;
            case R.id.announce_add_activity_bill:
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
            case R.id.announce_add_activity_date_start:
                new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    // 绑定监听器(How the parent is notified that the date is set.)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 此处得到选择的时间，可以进行你想要的操作
                        if(year == tyear && monthOfYear < tmonth+2 && monthOfYear >= tmonth) {
                            String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            tyear = year;
                            tmonth = monthOfYear;
                            tday = dayOfMonth;
                            announcedatestart.setText(date);
                            map.put(announceMsg.ANNOUNCEDATESTART,date);
                        }
                    }
                }
                        // 设置初始日期
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
            case R.id.announce_add_activity_date_end:
                new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    // 绑定监听器(How the parent is notified that the date is set.)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if(year == tyear && ((monthOfYear <tmonth+2 && monthOfYear > tmonth) || (tmonth == monthOfYear && dayOfMonth > tday))) {
                            String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            announcedateend.setText(date);
                            map.put(announceMsg.ANNOUNCEDATEEND,date);
                        }
                    }
                }
                        // 设置初始日期
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
            case R.id.announce_add_activity_time_start: {
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute;
                                announcetimestart.setText(time);
                                thour = hourOfDay;
                                map.put(announceMsg.ANNOUNCETIMESTART,time);
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
            }
                break;
            case R.id.announce_add_activity_time_end: {
                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(thour < hourOfDay) {
                                    String time = hourOfDay + ":" + minute;
                                    announcetimeend.setText(time);
                                    map.put(announceMsg.ANNOUNCETIMEEND,time);
                                }
                            }
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true).show();
            }
            break;
            case R.id.announce_add_activity_addr:
                getAddress();
                break;
            case R.id.announce_add_activity_publish:
                uploadAnnounce();
                break;
        }
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
               Bitmap bitmap = BitmapFactory.decodeFile(mdata.get(0).toString());
               byte[] bitdata = PhotoDeal.flattenBitmap(bitmap);
               map.put(announceMsg.ANNOUNCEIMAGE,Base64.encodeToString(bitdata, 0));
            }
        }
    }

    private void getAddress()   {
        List<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));

        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())  //此方法必须调用
                .enableAnimation(true)  //启用动画效果
                // .setAnimationStyle()  //自定义动画
                .setLocatedCity(new LocatedCity(announceMsg.city, announceMsg.province, announceMsg.code))
                .setHotCities(hotCities)  //指定热门城市
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        Message message = new Message();
                        message.obj = data.getName();
                        message.what = ANNOUNCEPOSITIONCODE;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onLocate() {

                    }
                })
                .show();
    }

    /*******************************************************************
     * 获取位置信息
     * *****************************************************************/
    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            if(location.getCity() != null) {
                Message message = new Message();
                message.obj = location.getCity();
                message.what = ANNOUNCEPOSITIONCODE;
                handler.sendMessage(message);

                announceMsg.province = location.getProvince();     //获取省份
                announceMsg.city = location.getCity();    //获取城市
                announceMsg.code = location.getAdCode();  //获取code
            }
        }
    }

    /*******************************************************************
     * 将图片和文字和入到json文件中
     * *****************************************************************/
    private String getJsonString(String name, String age, String cardId,
                                 String filepath) {
        try {
            JSONObject object = new JSONObject();
            object.put("name", name);
            object.put("age", age);
            object.put("cardId", cardId);
            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            byte[] data = PhotoDeal.flattenBitmap(bitmap);
            object.put("image", Base64.encodeToString(data, 0));
            return object.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    class AnnounceMsg {
        public String ANNOUNCETITLE = "title";
        public String ANNOUNCEIMAGE = "image";
        public String ANNOUNCEDATESTART = "datestart";
        public String ANNOUNCETIMESTART = "timestart";
        public String ANNOUNCEDATEEND = "dateend";
        public String ANNOUNCETIMEEND = "timeend";
        public String ANNOUNCESPONSOR = "sponsor";
        public String ANNOUNCEADDR = "addr";
        public String ANNOUNCECHARGE = "charge";
        String SNNOUNCEDETIALED = "detialed";
        String ANNOUNCETELP = "telephone";
        String ANNOUNCESUM = "sum";
        String ANNOUNCENOTE = "note";
        String province;
        String city;
        String code;
    }
}
