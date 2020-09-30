package com.example.yang.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    //主目录
    public static String MAINDIRPATH = "loveyou";
    //二级目录
    public static String SECONDAUDIODIRPATH = MAINDIRPATH+ File.separator +"audio";          //音频
    public static String SECONDVIDEODIRPATH = MAINDIRPATH+ File.separator +"video";          //视频
    public static String SECONDPICTUREDIRPATH = MAINDIRPATH+ File.separator +"picture";      //图片
    public static String SECONDMESSAFEDIRPATH = MAINDIRPATH+ File.separator +"message";      //消息
    public static String SECONDUSERINFODIRPATH = MAINDIRPATH+ File.separator +"userinfo";    //用户信息
    public static String SECONDCONFIGDIRPATH = MAINDIRPATH+ File.separator +"cfg";           //配置信息
    public static String SECONDFILEDIRPATH =  MAINDIRPATH+ File.separator +"file";           //文件目录
    public static String SECONDCACHEDIRPATH =  MAINDIRPATH+ File.separator +"cache";           //文件目录
    public static String THIRDANNOUNCEADDDIRPATH =  SECONDFILEDIRPATH+File.separator+"announceadd";           //文件目录


    public FileOperationUtil(){

    }

    /**************************************************************************
     * Name ：
     * descript ：创建一个目录，返回目录路径
     * return ：
    ***************************************************************************/
    public static File CreateDir(String name){
        String dirpath = Environment.getExternalStorageDirectory().getPath()+File.separator+name;
        File dir = new File(dirpath);
        if(!dir.exists()){
            dir.mkdirs();
            Log.d(TAG,"create new dir: "+dirpath);
        }
        return  dir;
    }

    /**************************************************************************
     * Name ：
     * descript ：创建一个文件，出入文件路径
     * return ：  无返回值
    ***************************************************************************/
    public static File CreateFile(String name){
        String filepath = Environment.getExternalStorageDirectory().getPath()+File.separator+name;
        File config = new File(filepath);
        if(!config.exists()){
            try {
                config.createNewFile();
                Log.d(TAG,"create new file: "+filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    /**************************************************************************
     * Name ：  copyfile
     * descript ：
     *          fromFile  源文件
     *          toFile    目的文件
     *          rewrite   是否创建新文件
     * return ：   null
    ***************************************************************************/
    public static void CopyFile(String fromFile, String toFile){
        try {
            int byteread = 0;
            File oldfile = new File(fromFile);
            File newFile = new File(toFile);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(fromFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(toFile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }

    }

    /** 删除文件，可以是文件或文件夹
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
//            Toast.makeText(HnUiUtils.getContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            Log.e("delete","删除文件失败:" + delFile + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Log.e("deleteSingleFile","删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            Log.e("deleteSingleFile","删除单个文件失败：" + filePath$Name + "不存在！");
            return false;
        }
    }

    /** 删除目录及目录下的文件
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Log.e("deleteDirectory","删除目录失败：" + filePath + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Log.e("deleteDirectory","删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
            return true;
        } else {
            Log.e("deleteDirectory","删除目录：" + filePath + "失败！");
            return false;
        }
    }

    public static void saveSingleJson(Map<String, String> map,String jsonFile) throws IOException {
        //打开要写入的json文件
        FileOutputStream fos = new FileOutputStream(jsonFile);
        //创建JsonWrite对象
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, "utf-8"));
        writer.setIndent("    ");
        writer.beginArray();
        // 获取所有键值对对象的集合
       Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry<String, String> me : set){
            writer.beginObject();
            writer.name(me.getKey()).value(me.getValue());
            writer.endObject();
        }
        writer.endArray();
        writer.close();
    }
    public static List<Map<String, Object>> getJson(String jsonFile) throws IOException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String,Object> map=new HashMap<String,Object>();
        FileInputStream fis = new FileInputStream(jsonFile);
        JsonReader reader = new JsonReader(new InputStreamReader(fis,"utf-8"));
        reader.beginArray();
        while (reader.hasNext()){
            reader.beginObject();
            while (reader.hasNext()) {
                map.put(reader.nextName(), reader.nextString());
            }
            list.add(map);
            reader.endObject();
        }

        reader.endArray();
        reader.close();
        return list;
    }

    /**
     * 把batmap 转file
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 根据路径 转bitmap
     * @param urlpath
     * @return
     */
    public static Bitmap getBitMBitmap(String urlpath) {

        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
