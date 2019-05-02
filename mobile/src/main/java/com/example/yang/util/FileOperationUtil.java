package com.example.yang.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.util
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/9/23 17:12
 * @change
 * @chang time
 * @class describe: 文件处理
 *****************************************************************/
public class FileOperationUtil {
    private final static String  TAG = "FileOperationUtil";
    //根目录名
    public static final String MAIN_FOLDER = "YLYW";
    public FileOperationUtil(){

    }

    /**************************************************************************
     * Name ：
     * descript ：创建一个目录，返回目录路径
     * return ：
    ***************************************************************************/
    public static void CreateDir(String name){
        File DirSecendFile = new File(name);
        if(!DirSecendFile.exists()){
            DirSecendFile.mkdirs();
            Log.d(TAG,"create new dir: "+name);
        }
    }
    /**************************************************************************
     * Name ：
     * descript ：创建一个文件，出入文件路径
     * return ：  无返回值
    ***************************************************************************/
    public static void CreateFile(String name){
        File config = new File(name);
        if(!config.exists()){
            try {
                config.createNewFile();
                Log.d(TAG,"create new file: "+name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**************************************************************************
     * Name ：  copyfile
     * descript ：
     *          fromFile  源文件
     *          toFile    目的文件
     *          rewrite   是否创建新文件
     * return ：   null
    ***************************************************************************/
    public static void CopyFile(File fromFile, File toFile, Boolean rewrite ){

        if(!fromFile.exists()){
            return;
        }

        if(!fromFile.isFile()){
            return;
        }
        if(!fromFile.canRead()){
            return;
        }
        if(!toFile.getParentFile().exists()){
            toFile.getParentFile().mkdirs();
        }
        if(toFile.exists() && rewrite){
            toFile.delete();
        }


        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while((c=fosfrom.read(bt)) > 0){
                fosto.write(bt,0,c);
            }
            //关闭输入、输出流
            fosfrom.close();
            fosto.close();


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
