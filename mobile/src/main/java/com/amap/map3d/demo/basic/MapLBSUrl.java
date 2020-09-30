package com.amap.map3d.demo.basic;

/****************************************************************
 * @name MyApplication
 * @class name：com.amap.map3d.demo.basic
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/4/7 19:04
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class MapLBSUrl {
    //创建表（create geotable）接口
    public static String create_table = "http://api.map.baidu.com/geodata/v4/geotable/create";
    //查询表（list geotable）接口
    public String list_table = "http://api.map.baidu.com/geodata/v4/geotable/list";
    //查询指定id表（detail geotable）接口
    public String detail_table = "http://api.map.baidu.com/geodata/v4/geotable/detail";
    //修改表（update geotable）接口
    public String update_table = "http://api.map.baidu.com/geodata/v4/geotable/update";
    //删除表（delete geotable）接口
    public String delete_table = "http://api.map.baidu.com/geodata/v4/geotable/delete";
    //创建列（create column）接口
    String create_column = "http://api.map.baidu.com/geodata/v4/column/create";
    //查询列（list column）接口
    public String list_column = "http://api.map.baidu.com/geodata/v4/column/list";
    //查询指定id列（detail column）接口
    public String detail_column = "http://api.map.baidu.com/geodata/v4/column/detail";
    //修改指定条件列（update column）接口
    public String update_column = "http://api.map.baidu.com/geodata/v4/column/update";
    //删除指定条件列（delete column）接口
    public String delete_column = "http://api.map.baidu.com/geodata/v4/column/delete";
    //创建数据（create poi）接口
    public static String create_poi = "http://api.map.baidu.com/geodata/v3/poi/create";
    //查询指定条件的数据（poi）列表接口
    public static String list_poi = "http://api.map.baidu.com/geodata/v4/poi/list";
    //查询指定id的数据（poi）列表接口
    public String list_id_poi = "http://api.map.baidu.com/geodata/v4/poi/detail";
    //修改数据（update poi）列表接口
    public static String update_poi = "http://api.map.baidu.com/geodata/v3/poi/update";
    //删除数据（delete poi）列表接口
    public String delete_poi = "http://api.map.baidu.com/geodata/v4/poi/delete";
    //批量上传数据（upload poi）列表接口
    String update_list_poi = "http://api.map.baidu.com/geodata/v4/poi/upload";
    //批量操作任务查询（list job）接口
    String list_job = "http://api.map.baidu.com/geodata/v4/job/list";

    public String getUrl(int action){
        String url = null;
        switch (action){

        }
        return url;
    }
}
