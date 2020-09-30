package com.example.yang.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.yang.adapter.OwnCreditValueAdapt;
import com.example.yang.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class OwnCreditValueActivity extends AppCompatActivity {
    private ExpandableListView listview;
    private List<TitleInfo> mList = new ArrayList<>();
    public String[] titleStrings = {"我的文章", "我的评论", "我发布的活动", "我参加的活动"};

    public String[][] nameStrings = {
            {"苍井空", "波多野结衣", "小泽玛莉亚", "龙泽罗拉"},
            {"鹿晗", "李易峰", "吴亦凡", "王俊凯"},
            {"张先生", "刘先生", "李先生", "杜先生", "小弟弟"},
            {"奥巴驴", "小学僧", "儿童劫", "托儿索"}};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_credit_value_xml);
        listview = findViewById(R.id.own_credit_value_list);
        listview.setGroupIndicator(null);
        listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        initList();
        initAdapter();
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        OwnCreditValueAdapt mAdapter = new OwnCreditValueAdapt(mList, this);
        listview.setAdapter(mAdapter);
        for(int i = 0;i<mList.size();i++){
            listview.expandGroup(i);
        }
    }

    /**
     * 初始化数据源
     */
    private void initList() {

        for (int i = 0; i < titleStrings.length; i++) {
            //创建组对象
            TitleInfo info= new TitleInfo();
            //循环添加组的标题名
            info.setTitle(titleStrings[i]);
            //创建子对象数据源
            List<ContentInfo> list = new ArrayList<>();
            for (int j = 0; j < nameStrings.length; j++) {
                //创建子对象
                ContentInfo info2 = new ContentInfo();
                //添加用户名或者头像
                info2.setName(nameStrings[i][j]);
                //将子对象添加到数据源
                list.add(info2);
            }
            //将子对象数据源复制给组对象
            info.setInfo(list);
            //将组对象添加到总数据源中
            mList.add(info);

        }

    }

    public class TitleInfo{
        private String            title;
        private List<ContentInfo> info;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<ContentInfo> getInfo() {
            return info;
        }

        public void setInfo(List<ContentInfo> info) {
            this.info = info;
        }
    }

    public class ContentInfo{
        public String name;
        private String qianming;
        private String icon;

        public String getQianming() {
            return qianming;
        }

        public void setQianming(String qianming) {
            this.qianming = qianming;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
