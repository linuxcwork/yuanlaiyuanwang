package com.example.yang.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;


/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.myapplication
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/9/17 19:24
 * @change
 * @chang time
 * @class describe：图片裁剪
 *****************************************************************/
public class PictureClipp {

    private int CAMERA_OK = 0;
    private int CUT_OK = 0;
    public PictureClipp(){

    }

    /**************************************************************************
     * Name ： clipPhoto
     * descript ： 裁剪图片
     * return ： void
    **************************************************************************
     * @param uri
     * @param type*/
    public void clipPhoto(Uri uri, int type) {


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);

        /**
         * 此处做一个判断
         * １，相机取到的照片，我们把它做放到了定义的目录下。就是file
         * ２，相册取到的照片，这里注意了，因为相册照片本身有一个位置，我们进行了裁剪后，要给一个裁剪后的位置，
         * 　　不然onActivityResult方法中，data一直是null
         */
        if(type==CAMERA_OK)
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }else {
           // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        //startActivityForResult(intent,0);
    }

}
