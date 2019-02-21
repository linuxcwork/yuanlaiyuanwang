package com.example.yang.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.util
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/9/30 20:24
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class PhotoDeal {

    /**************************************************************************
     * Name ：
     * descript ：
     * return ：
    ***************************************************************************/
    public PhotoDeal(){

    }

    /**************************************************************************
     * Name     ： createImageUri
     * descript ： 创建文件夹存储拍照图片
     * return   ： 图片存储路径
    ***************************************************************************/
    public Uri createImageUri(Context context) {
        String name = "takePhoto" + System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, name);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        return uri;
    }

    /**************************************************************************
     * Name     ： delteImageUri
     * descript ： 删除指定路径的图片
     * return   ： 无
    ***************************************************************************/
    public void delteImageUri(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null, null);

    }

}
