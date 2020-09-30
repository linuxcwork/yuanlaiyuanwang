package com.example.yang.Activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.Activity.AlbumSelectChat;
import com.example.yang.fragment.PictureSlideFragment;
import com.example.yang.myapplication.R;

import java.util.ArrayList;

import me.kareluo.imaging.IMGEditActivity;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.ui
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/10/4 12:08
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class PicturePreview extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private int allcount;
    private ViewPager imagecurr;
    private ArrayList<String> urlList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("PicturePreview onCreate");
        setContentView(R.layout.picture_preview);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        urlList =(ArrayList) bundle.getSerializable("picture");
        allcount = urlList.size();
        if(allcount == 1){
           // Collections.addAll(alldata, urls);
            //System.out.println(Uri.fromFile(FileOperationUtil.CreateFile(alldata.get(0).toString())));
            //imagecurr.setImageURI(FileOperationUtil.CreateFile(alldata.get(0).toString()));
        }else if(allcount > 1){
            //Collections.addAll(urlList, urls);
        }
        initActivity();
    }

    public void initActivity(){
        ImageView imagereturn  = findViewById(R.id.image_return);
        imagereturn.setOnClickListener(this);
        TextView imagepostion = findViewById(R.id.image_position);
        imagepostion.setOnClickListener(this);
        Button imagesend    = findViewById(R.id.image_count);
        imagesend.setOnClickListener(this);
        imagecurr    = findViewById(R.id.image_curr);
        imagecurr.setAdapter(new PictureSlidePagerAdapter(getSupportFragmentManager()));
        imagecurr.addOnPageChangeListener(this);
        GridView imageall     = findViewById(R.id.image_all);
        TextView imageedit    = findViewById(R.id.image_edit);
        imageedit.setOnClickListener(this);
        CheckBox imagecbp     = findViewById(R.id.image_cbp);
        imagecbp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.image_return:
               //返回
               Intent intent_return = new Intent(this, AlbumSelectChat.class);
               startActivity(intent_return);
               break;
           case R.id.image_edit:
               //编辑
               System.out.println(getMediaUriFromPath(urlList.get(0).toString()));
               System.out.println(urlList.get(0).toString());
               Intent intent = new Intent(this, IMGEditActivity.class);
               intent.putExtra("pic", urlList.get(0).toString());
               startActivity(intent);
               finish();
               break;
       }
    }
    public Uri getMediaUriFromPath(String path) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                new String[] {path.substring(path.lastIndexOf("/") + 1)},
                null);

        Uri uri = null;
        if(cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        cursor.close();
        return uri;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private  class PictureSlidePagerAdapter extends FragmentStatePagerAdapter {

        public PictureSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PictureSlideFragment.newInstance(urlList.get(position));
        }

        @Override
        public int getCount() {
            return urlList.size();
        }
    }
}
