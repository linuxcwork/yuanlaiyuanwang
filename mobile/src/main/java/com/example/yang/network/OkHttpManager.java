package com.example.yang.network;

import android.util.Log;

import com.example.yang.myapplication.HttpResponse;
import com.example.yang.myapplication.chat_contrue;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yang on 2018/4/16.
 */

public class OkHttpManager{
    public static final String TAG = "OkHttpManger";
    private static OkHttpClient okHttpClient;
    private static OkHttpManager manager;
    //private LocalBinder binder = new LocalBinder();
    private String ret;
    HttpResponse responsehandle;
    public OkHttpManager() {
       super();
        okHttpClient = new OkHttpClient();
        //File cacheDir = new File(getCacheDir(), "okhttp_cache");
        //File cacheDir = new File(getExternalCacheDir(), "okhttp");
        //Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5*1000, TimeUnit.MILLISECONDS) //链接超时
                .readTimeout(10*1000,TimeUnit.MILLISECONDS) //读取超时
                .writeTimeout(10*1000,TimeUnit.MILLISECONDS) //写入超时
                .build();
                /* .cache(cache)  //设置缓存
               .addInterceptor(new HttpHeadInterceptor()) //应用拦截器：统一添加消息头
                .addNetworkInterceptor(new NetworkspaceInterceptor())//网络拦截器
               .addInterceptor(loggingInterceptor)//应用拦截器：打印日志*/
    }

//    /**
//     * 创建Binder对象，返回给客户端即Activity使用，提供数据交换的接口
//     */
//    public static class LocalBinder extends Binder {
//        // 声明一个方法，getService。（提供给客户端调用）
//        public OkHttpManager getService() {
//            // 返回当前对象LocalService,这样我们就可在客户端端调用Service的公共方法了
//            return OkHttpManager.this;
//        }
//    }
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        File cacheDir = new File(getCacheDir(), "okhttp_cache");
//        Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);
//
//        okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(5*1000, TimeUnit.MILLISECONDS) //链接超时
//                .readTimeout(10*1000,TimeUnit.MILLISECONDS) //读取超时
//                .writeTimeout(10*1000,TimeUnit.MILLISECONDS) //写入超时
//                .cache(cache)  //设置缓存
//                .build();
//        okHttpClient.eventListenerFactory();
//                //**//*.addInterceptor(new HttpHeadInterceptor()) //应用拦截器：统一添加消息头
//                /*.addNetworkInterceptor(new NetworkspaceInter//应用拦截器：打印日志*//**//**//**//*ceptor())//网络拦截器
//                .addInterceptor(new loggingInterceptor())//应用拦截器：打印日志*//**//**//**//**/
//
//    }

//    @Override
//    public boolean onUnbind(Intent intent) {
//        Log.i(TAG,"this is onUnbind");
//        return super.onUnbind(intent);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }

    public static OkHttpManager getInstance() {
        synchronized (OkHttpManager.class) {
            if (manager == null) {
                manager = new OkHttpManager();
                return manager;
            }
        }
        return manager;
    }
/*
* 使用时要在线程,同步get请求*/
    public void sync_get(String url) throws Exception{

        //通过Builder辅助类构建一个Request对象
        Request request = new Request.Builder().get().url(url).build();
        //通过同步执行获取一个Response对象
        Response response = okHttpClient.newCall(request).execute();
        //判断响应是否成功,如果成功的话,响应的内容会放在response.body()中
        if (response.isSuccessful()) {
            //字符串类型
            Log.i(TAG, "getData: " + response.body().string());
            //字节数组类型
            Log.i(TAG, "getData: " + response.body().bytes());
            //字节流类型
            Log.i(TAG, "getData: " + response.body().byteStream());
            //字符流类型
            Log.i(TAG, "getData: " + response.body().charStream());
        }
    }
/*
* 异步 get请求*/
    public void async_get(String url){
        //通过Builder辅助类构建一个Request对象
        Request request = new Request.Builder().get().url(url).build();
        //通过入队的方式,进行异步操作
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 请求失败的时候调用该方法!");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //字符串类型
                Log.i(TAG, "getData: " + response.body().string());
                //字节数组类型
                Log.i(TAG, "getData: " + response.body().bytes());
                //字节流类型
                Log.i(TAG, "getData: " + response.body().byteStream());
                //字符流类型
                Log.i(TAG, "getData: " + response.body().charStream());
            }
        });
    }

    /**
     * 使用post方式提交json字符串
     *  @param url     提交的路径
     * @param content 提交的内容
     */
    public String postString(String url, String content,HttpResponse responsehandle) {
        //构建一个RequestBody对象,,因为提交的是json字符串需要添加一个MediaType为"application/json",
        // 普通的字符串直接是null就可以了

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json,charset=UTF-8"), content);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());
                ret = response.body().string();
            }
        });
        return ret;
    }

    /**
     * 提交单个键值对
     *
     * @param url
     * @param //key
     * @param //value
     */
    public static void postKeyValuePaire(String url, chat_contrue.WeChatMessage mesg,HttpResponse responsehandle) {
        //提交键值对需要用到FormBody,因为FormBody是继承RequestBody的,所以拥有RequestBody的一切属性
        FormBody formBody = new FormBody.Builder()
                //添加键值对
                .add("id", mesg.getValue())
                .add("content",mesg.getContent().toString())
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responsehandle.failed(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());
                responsehandle.succesd(call,response);
            }
        });
    }

    /**
     * 提交多个键值对
     * @param url 提交的路径
     * @param map 用来放置键值对,map的key对应键,value对应值
     */
    public static void postKeyValuePaires(String url, Map<String, Object> map,HttpResponse responsehandle) {
        FormBody.Builder build = new FormBody.Builder();

        if (map != null) {
            //增强for循环遍历
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                build.add(entry.getKey(),entry.getValue().toString());
            }
        }

        FormBody formBody = build.build();

        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responsehandle.failed(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //response 数据流再度去一次后就会关闭
                String ret = response.body().string();
                System.out.println(ret);
                responsehandle.succesd(call,ret);
            }
        });

    }

    /**
     * 根据文件的名称判断文件的Mine值
     */
    private String getMediaType(String fileName) {
        FileNameMap map = URLConnection.getFileNameMap();
        String contentTypeFor = map.getContentTypeFor(fileName);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 上传单一文件
     */
    public void upLoadFile(String url, File file) {
        // //提交键值对需要用到MultipartBody,因为MultipartBody是继承RequestBody的,
        // 所以拥有RequestBody的一切属性,类似于javaEE中的表单提交
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody fileBody = RequestBody.create(
                MediaType.parse(getMediaType(file.getName())), file);
        //这里的uploadfile是文件上传的标识,用于服务器识别文件
        builder.addFormDataPart("uploadfile", file.getName(), fileBody);
        MultipartBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    /**
     * 上传多个文件
     */
    public void upLoadFiles(String url, File[] files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < files.length; i++) {
            RequestBody fileBody = RequestBody.create(MediaType.parse(getMediaType(files[i].getName())), files[i]);
            builder.addFormDataPart("uploadfile", files[i].getName(), fileBody);
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    /**
     * 上传多个文件和参数
     */
    public void upLoadMultiFiles(String url, File[] files, Map<String, Object> map) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //添加文件
        if (files.length != 0) {
            for (int i = 0; i < files.length; i++) {
                RequestBody fileBody = RequestBody.create(
                        MediaType.parse(getMediaType(files[i].getName())), files[i]);
                builder.addFormDataPart("uploadfile", files[i].getName(), fileBody);
            }
        }
        //添加参数
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue().toString());
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: "+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "onResponse: " + response.body().string());
            }
        });
    }


}
