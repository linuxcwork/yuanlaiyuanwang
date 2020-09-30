package com.example.yang.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.yang.myapplication.CurrentFriend;
import com.example.yang.myapplication.R;
import com.example.yang.util.FileOperationUtil;
import com.example.yang.util.SystemUtil;

import java.io.File;
import java.util.ArrayList;

public class SelectbackgroundActivity extends Activity implements View.OnClickListener {

    private final static String TAG = "SelectbackgroundActivity";
    private final int PICUREREQUESTCODE = 0;
    private final int TAKEPHOTOCODE = 1;
    private String photopath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectbackgroundxml);
        Intent getintent = getIntent();
        Bundle bundle = getintent.getExtras();
        bundle.get("name");
        ImageView prereturn = findViewById(R.id.anounce_add_activity_return);
        prereturn.setOnClickListener(this);
        RelativeLayout picture = findViewById(R.id.select_background_picture_RelativeLayout);
        picture.setOnClickListener(this);
        RelativeLayout takephoto = findViewById(R.id.select_background_take_photo_RelativeLayout);
        takephoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.anounce_add_activity_return:
                Intent chatroom = new Intent(this, CurrentFriend.class);
                startActivity(chatroom);
                break;
            case R.id.select_background_picture_RelativeLayout:
                /* 开启Pictures画面Type设定为image */
                Intent intent = new Intent(this, AlbumSelectChat.class);
                //Intent intent = new Intent();
                //intent.setType("image/*");
                /*使用Intent.ACTION_GET_CONTENT这个Action */
                //intent.setAction(Intent.ACTION_PICK);
                /* 取得相片后返回本画面 */
                intent.putExtra("activity","chat_contrue");
                startActivityForResult(intent,PICUREREQUESTCODE);
                break;
            case R.id.select_background_take_photo_RelativeLayout:
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                builder.detectFileUriExposure();
                FileOperationUtil.CreateDir(FileOperationUtil.MAINDIRPATH);
                FileOperationUtil.CreateDir(FileOperationUtil.SECONDMESSAFEDIRPATH);
                //相片保存地址
                photopath = Environment.getExternalStorageDirectory().getPath()+ File.separator+FileOperationUtil.SECONDMESSAFEDIRPATH+File.separator+ SystemUtil.getNumSmallLetter(8)+".jpg";
                Intent intent_cap = new Intent();
                // 指定开启系统相机的Action
                intent_cap.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                //从这
                intent_cap.addCategory(Intent.CATEGORY_DEFAULT);

                // 设置系统相机拍摄照片完成后图片文件的存放地址
                intent_cap.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileOperationUtil.CreateFile(photopath)));
                //到这不用设置的话会在onActivityResult方法里，在意图获取一个处理过的bitmap
                startActivityForResult(intent_cap, TAKEPHOTOCODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //位置返回数据处理
           if (requestCode == PICUREREQUESTCODE) {
                //图片返回数据处理
                Bundle bundle = data.getExtras();
                ArrayList mdata = (ArrayList) bundle.getSerializable("picture");
                if (mdata == null || mdata.size() == 0) {
                    return;
                }

            } else if (requestCode == TAKEPHOTOCODE) {
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
                // iv.setImageBitmap(bitmap);
            }
        }

        /*******************************
         * 未做返回处理
         * ******************************/

    }
}
