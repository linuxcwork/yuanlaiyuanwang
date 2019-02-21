package com.example.yang.InterfaceClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.InterfaceClass
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2018/9/28 20:16
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public interface sendDataToAvtivityInterface {

    /**************************************************************************
     * Name     ： dataCon
     * descript ： 回传adapt中已选择图片给Activity,及图片数量
     * return   ： void
    ***************************************************************************/
    public void dataCon(int count, ArrayList selecedata, List<Map<String, Object>> alldata);
}
