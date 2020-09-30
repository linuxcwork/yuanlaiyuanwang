package com.example.yang.Aichar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.example.yang.util.SharedPreferencedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/****************************************************************
 * @name MyApplication
 * @class name：com.baidu.aichar
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/1/13 17:43
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class IdCardIdtfcation {
    private final String MTAG = "IdCardIdtfcation";
    private Context context;
    boolean success = false;
    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();
    public IdCardIdtfcation(Context context){
        this.context = context;
        OCR.getInstance(context).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象

                String token = result.getAccessToken();
            }
            @Override
            public void onError(OCRError error) {
                Log.e(MTAG,"IdCardIdtfcation"+error);
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, context, "h14tS8nu1K3lVccKVTwVklV2", "IhpQ1vHlB1Ns6MfHyleT4ri1jSFyOX6T");
    }

    public boolean identificationAction(String filePath,Context context,String idCardSide){
        File file = new File(filePath);
        if(file == null){
            System.out.println("file is null");
        } else {
            System.out.println(file);
        }
        // 身份证识别参数设置
        IDCardParams param = new IDCardParams();
        param.setImageFile(file);
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance(context).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                // 调用成功，返回IDCardResult对象
                System.out.println(result);
                Map<String, Object> map = getJsonObject(result.toString());
                if(map.get("idNumber") != null) {
                    map.put("isrealname", "yes");
                    sharedPreferencedUtils.UpdateFile(context, map);
                    Toast.makeText(context, "chenggong", Toast.LENGTH_LONG).show();
                    success = true;
                }else {
                    success = false;
                }
            }

            @Override
            public void onError(OCRError error) {
                Log.e(MTAG,"identificationAction"+error);
                // 调用失败，返回OCRError对象
                success = false;
            }
        });
        return success;
    }

    public Map<String, Object> getJsonObject(String responseData) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            JSONObject jsonArray = new JSONObject(responseData);

            Iterator iterator = jsonArray.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jsonArray.getString(key);
                map.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
