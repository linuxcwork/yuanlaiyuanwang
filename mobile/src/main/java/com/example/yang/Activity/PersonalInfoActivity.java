package com.example.yang.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.yang.myapplication.R;
import com.example.yang.util.SharedPreferencedUtils;
import com.example.yang.util.FileOperationUtil;
import com.example.yang.util.XmppConnection;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import rorbin.q.radarview.RadarData;
import rorbin.q.radarview.RadarView;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "PersonalInfoActivity";
    private LineChartView step;
    private LineChartData data;
    private RelativeLayout stagename;
    private  TextView textconnect;
    private RoundedImageView headimageview;
    private TextView heighttext;
    private TextView stagenametext;
    private TextView marrigetext;
    private TextView weighttext;
    private TextView residencetext;
    private TextView mototext;
    private TextView realcartext;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 12;
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;

    private final int REQUESTCODEIMAGE = 0;
    private final int REQUESTCODESETTINGTEXT = 1;
    private File tofile;
    private XmppConnection xmppConnection;

    private Resources res;;
    private final int UPDATEVCARD = 0;

    private Handler mnHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case UPDATEVCARD:
                    Bitmap bitmap = BitmapFactory.decodeFile(msg.obj.toString());
                    byte[] bitdata = flattenBitmap(bitmap);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            xmppConnection.updateOwnIndo("image", Base64.encodeToString(bitdata, 0));
                        }
                    }).start();
                default:
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_information_activity);
        xmppConnection = XmppConnection.getInstance();
        res = getResources();
        FileOperationUtil.CreateDir(FileOperationUtil.SECONDPICTUREDIRPATH);
        tofile = FileOperationUtil.CreateFile(FileOperationUtil.SECONDPICTUREDIRPATH+ File.separator+"headsculpture.jpg");
        initView();
    }

    private void initView(){
        RadarView mind = findViewById(R.id.personale_info_mind_radarView);
        mind.setEmptyHint("无数据");

        List<Integer> layerColor = new ArrayList<>();
        Collections.addAll(layerColor, 0x3300bcd4, 0x3303a9f4, 0x335677fc, 0x333f51b5, 0x33673ab7);
        mind.setLayerColor(layerColor);

        List<String> vertexText = new ArrayList<>();
        Collections.addAll(vertexText, "力量", "敏捷", "速度", "智力", "精神", "耐力", "体力", "魔力", "意志", "幸运");
        mind.setVertexText(vertexText);

        List<Integer> res = new ArrayList<>();
        Collections.addAll(res, R.mipmap.editor_video_mark_icon, R.mipmap.editor_video_mark_icon, R.mipmap.editor_video_mark_icon,
                R.mipmap.editor_video_mark_icon, R.mipmap.editor_video_mark_icon, R.mipmap.editor_video_mark_icon,
                R.mipmap.editor_video_mark_icon, R.mipmap.editor_video_mark_icon, R.mipmap.editor_video_mark_icon, R.mipmap.editor_video_mark_icon);
        mind.setVertexIconResid(res);

        List<Float> values = new ArrayList<>();
        Collections.addAll(values, 3f, 6f, 2f, 7f, 5f, 1f, 9f, 3f, 8f, 5f);
        RadarData data = new RadarData(values);
        mind.addData(data);

        step = findViewById(R.id.personale_info_step_chart);
        generateValues();
        generateData();
        step.setViewportCalculationEnabled(false);
        resetViewport();

        RelativeLayout image = findViewById(R.id.personal_info_head_sculpture_RelativeLayout);
        image.setOnClickListener(this);
        headimageview = findViewById(R.id.personal_info_head_sculpture);
        Glide
                .with(PersonalInfoActivity.this)
                .load(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"headsculpture",null))
                .dontAnimate()
                .centerCrop()
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(headimageview);
        stagename = findViewById(R.id.personal_info_stage_name_RelativeLayout);
        stagenametext = findViewById(R.id.personal_info_stage_name);
        stagenametext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"stagename","Not filled"));
        stagename.setOnClickListener(this);
        TextView account = findViewById(R.id.personal_info_account);
        account.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"account","小舟"));
        RelativeLayout admirers = findViewById(R.id.personal_info_admirers_RelativeLayout);
        TextView sex = findViewById(R.id.personal_info_sex);
        sex.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"sex","nan"));
        TextView name = findViewById(R.id.personal_info_nsme);
        name.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"name","nan"));
        TextView age = findViewById(R.id.personal_info_age);
        age.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"age","nan"));
        RelativeLayout marriage = findViewById(R.id.personal_info_marriage_RelativeLayout);
        marrigetext = findViewById(R.id.personal_info_marriage);
        marrigetext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"marriage","Not filled"));
        marriage.setOnClickListener(this);
        RelativeLayout height = findViewById(R.id.personal_info_height_RelativeLayout);
        heighttext = findViewById(R.id.personal_info_height);
        heighttext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"height","Not filled"));
        height.setOnClickListener(this);
        RelativeLayout weight = findViewById(R.id.personal_info_weight_RelativeLayout);
        weighttext = findViewById(R.id.personal_info_weight);
        weighttext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"weight","Not filled"));
        weight.setOnClickListener(this);
        RelativeLayout residence = findViewById(R.id.personal_info_residence_RelativeLayout);
        residencetext = findViewById(R.id.personal_info_residence);
        residencetext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"residence","Not filled"));
        residence.setOnClickListener(this);
        TextView censusregtext = findViewById(R.id.personal_info_census_register);
        censusregtext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"censusreg","Not filled"));
        RelativeLayout physical_examination = findViewById(R.id.personal_info_physical_examination_relativeLayout);
        physical_examination.setOnClickListener(this);
        RelativeLayout motto = findViewById(R.id.personal_info_motto_relativeLayout);
        mototext = findViewById(R.id.personal_info_motto);
        mototext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"motto","Not filled"));
        motto.setOnClickListener(this);
        RelativeLayout family_members = findViewById(R.id.personal_info_family_members_relativeLayout);
        family_members.setOnClickListener(this);
        RelativeLayout work_content = findViewById(R.id.personal_info_work_content_relativeLayout);
        work_content.setOnClickListener(this);
        RelativeLayout violation_record = findViewById(R.id.personal_info_violation_record_relativeLayout);
        violation_record.setOnClickListener(this);
        RelativeLayout hobbies = findViewById(R.id.personal_info_hobbies_relativeLayout);
        TextView hobbiestext = findViewById(R.id.personal_info_hobbies);
        hobbiestext.setText(sharedPreferencedUtils.getUserInfo(getApplicationContext(),"hobbies","Not filled"));
        hobbies.setOnClickListener(this);
        RelativeLayout checked = findViewById(R.id.personal_info_checked_relativeLayout);
        checked.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String titlename = "test";
        boolean issingletext = false;
        switch (v.getId()){
            case R.id.personal_info_head_sculpture_RelativeLayout:
                /* 开启Pictures画面Type设定为image */
                Intent intent = new Intent(this, AlbumSelectChat.class);
                intent.putExtra("activity","announceaddactivity");
                startActivityForResult(intent,REQUESTCODEIMAGE);
                break;
            case R.id.personal_info_height_RelativeLayout:
                titlename = res.getString(R.string.personal_info_height_string);
                issingletext = true;
                break;
            case R.id.personal_info_stage_name_RelativeLayout:
                titlename = res.getString(R.string.personal_info_stagename_string);
                issingletext = true;
                break;
            case R.id.personal_info_marriage_RelativeLayout:
                titlename = res.getString(R.string.personal_info_marriage_string);
                issingletext = true;
                break;
            case R.id.personal_info_weight_RelativeLayout:
                titlename = res.getString(R.string.personal_info_weight_string);
                issingletext = true;
                break;
            case R.id.personal_info_residence_RelativeLayout:
                titlename = res.getString(R.string.personal_info_residence_string);
                issingletext = true;
                break;
            case R.id.personal_info_physical_examination_relativeLayout:
                //进入体检报告上传删除页
                break;
            case R.id.personal_info_motto_relativeLayout:
                titlename = res.getString(R.string.personal_info_motto_string);
                issingletext = true;
                break;
            case R.id.personal_info_family_members_relativeLayout:
                //添加或删除家庭成员信息
                break;
            case R.id.personal_info_work_content_relativeLayout:
                //工作内容
                break;
            case R.id.personal_info_violation_record_relativeLayout:
                //跳转到违规记录页面
                break;
            case R.id.personal_info_hobbies_relativeLayout:
                titlename = res.getString(R.string.personal_info_hobbies_string);
                issingletext = true;
                break;
            case R.id.personal_info_checked_relativeLayout:
                //跳转到我查看的人员信息页面
                break;
        }
        if(issingletext){
            Intent intenthgt = new Intent(this,PersonalInfoModifitionActivity.class);
            intenthgt.putExtra("name",titlename);
            startActivityForResult(intenthgt,REQUESTCODESETTINGTEXT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            //位置返回数据处理
            Map<String, Object> map = new HashMap<>();
            if(requestCode == REQUESTCODESETTINGTEXT){
              String name = data.getStringExtra("name");
              if(name != null){
                  if(name.equals(res.getString(R.string.personal_info_height_string))){
                      map.put("height",data.getStringExtra("info"));
                      heighttext.setText(data.getStringExtra("info"));
                  }else if(name.equals(res.getString(R.string.personal_info_stagename_string))){
                      stagenametext.setText(data.getStringExtra("info"));
                      map.put("stagename",data.getStringExtra("info"));
                      xmppConnection.updateOwnIndo("nickname",data.getStringExtra("info"));
                  }else if(name.equals(res.getString(R.string.personal_info_marriage_string))){
                      marrigetext.setText(data.getStringExtra("info"));
                      map.put("marriage",data.getStringExtra("info"));
                  }else if(name.equals(res.getString(R.string.personal_info_weight_string))){
                      weighttext.setText(data.getStringExtra("info"));
                      map.put("weight",data.getStringExtra("info"));
                  }else if(name.equals(res.getString(R.string.personal_info_residence_string))){
                      residencetext.setText(data.getStringExtra("info"));
                      map.put("residence",data.getStringExtra("info"));
                      xmppConnection.setWorkAddr("LOCALITY",data.getStringExtra("info"));
                  }else if(name.equals(res.getString(R.string.personal_info_motto_string))){
                      mototext.setText(data.getStringExtra("info"));
                      map.put("motto",data.getStringExtra("info"));
                  }else if(name.equals(res.getString(R.string.personal_info_hobbies_string))){
                      textconnect = findViewById(R.id.personal_info_hobbies);
                      map.put("hobbies",data.getStringExtra("info"));
                  }
                  //textconnect.setText(data.getStringExtra("info"));
              }
            }else if(requestCode == REQUESTCODEIMAGE){
                FileOperationUtil.delete(tofile.getPath());
                tofile = FileOperationUtil.CreateFile(FileOperationUtil.SECONDPICTUREDIRPATH+ File.separator+"headsculpture.jpg");
                Bundle bundle= data.getExtras();
                ArrayList mdata =(ArrayList) bundle.getSerializable("picture");
                if(mdata == null || mdata.size() == 0){
                    return ;
                }
                Glide
                        .with(PersonalInfoActivity.this)
                        .load(mdata.get(0).toString())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(60, 60)
                        .skipMemoryCache(true) // 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .dontAnimate()
                        .centerCrop()
                        .into(headimageview);

                compress(4,mdata.get(0).toString());

                Message message = new Message();
                message.obj = tofile.getPath();
                message.what = UPDATEVCARD;
                mnHandler.sendMessage(message);

                map.put("headsculpture",tofile.getPath());
            }
            sharedPreferencedUtils.UpdateFile(this,map);
        }
    }


    /**
     *
     * @param inSampleSize  可以根据需求计算出合理的inSampleSize
     */
    private void compress(int inSampleSize, String filepath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置此参数是仅仅读取图片的宽高到options中，不会将整张图片读到内存中，防止oom
        options.inJustDecodeBounds = false;

        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filepath,options);
        //Bitmap resultBitmap = BitmapFactory.decodeFile(filepath, options);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Log.d(TAG,"compress"+bos+":"+bitmap);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        try {
            FileOutputStream fos = new FileOutputStream(tofile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static byte[] flattenBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() * 4; //每个像素占32bit，所以*4
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w("Favorite", "Could not write icon");
            return null;
        }
    }

    private void generateValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 100f;
            }
        }
    }
    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(step.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = numberOfPoints - 1;
        step.setMaximumViewport(v);
        step.setCurrentViewport(v);
    }

    private void generateData() {

        List<Line> lines = new ArrayList<>();
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);

            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        step.setLineChartData(data);

    }

}
