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

import java.io.File;
import java.lang.reflect.Array;

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
    Context context;
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
        }, context, "O2ygXFpcAkiOh7iuwsxZ2pE0", "b1Sqee3dBB5hMMEtmiI8HHcZQPDN8nGi");
    }

    public void identificationAction(String filePath,Context context,String idCardSide){
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

        // 调用身份证识别服务
        new Thread(new Runnable() {
            @Override
            public void run() {

                OCR.getInstance(context).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
                    @Override
                    public void onResult(IDCardResult result) {
                        // 调用成功，返回IDCardResult对象
                        System.out.println(result);
                        Toast.makeText(context, "chenggong", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(OCRError error) {
                        Log.e(MTAG,"identificationAction"+error);
                        // 调用失败，返回OCRError对象
                    }
                });

            }
        }).start();
    }

    class ResultIdCard{
        //图像方向，当detect_direction=true时存在。-1:未定义，0:正向，1: 逆时针90度， 2:逆时针180度， 3:逆时针270度
        private int direction;
        //唯一的log id，用于问题定位
        private long log_id;
        //定位和识别结果数组，数组元素的key是身份证的主体字段（正面支持：住址、公民身份号码、出生、姓名、性别、
        // 民族，背面支持：签发机关、签发日期、失效日期）。只返回识别出的字段。若身份证号码校验不通过，则不返回
        private Array words_result;
        //识别结果数，表示words_result的元素个数
        private int words_result_num;
        //位置数组（坐标0点为左上角）
        private Array location;
        //表示定位位置的长方形左上顶点的水平坐标
        private int left;
        //表示定位位置的长方形左上顶点的垂直坐标
        private int top;
        //表示定位位置的长方形的宽度
        private int width;
        //表示定位位置的长方形的高度
        private int height;
        //识别结果字符串
        private String words;
    }
}
