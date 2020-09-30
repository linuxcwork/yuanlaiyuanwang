package com.example.yang.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.yang.adapter.HorizontalPagerAdapter;
import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.sqlite_linkmanmss;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.yang.fragment.messageoff_fragment.MSGTYPECHATTEXT;

public class RecommendFriendActivity extends AppCompatActivity {
    private final String TAG = "RecommendFriendActivity";

    private sqlite_linkmanmss sql = new sqlite_linkmanmss(this, "link", null, 1);;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_friend_activity_xml);
		final HorizontalInfiniteCycleViewPager infiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.hicvp);
        infiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(this, getData()));

        Map<String, String> map = new HashMap<>();
        map.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, "0");
        sql.open();
        sql.updateContact(MainActivity.friendinfotable, sqlite_linkmanmss.EKY_MESSAGETYPE,"recommend",map);
        sql.close();

      /*  infiniteCycleViewPager.setScrollDuration(500);
        infiniteCycleViewPager.setInterpolator(...);
        infiniteCycleViewPager.setMediumScaled(true);
        infiniteCycleViewPager.setMaxPageScale(0.8F);
        infiniteCycleViewPager.setMinPageScale(0.5F);
        infiniteCycleViewPager.setCenterPageScaleOffset(30.0F);
        infiniteCycleViewPager.setMinPageScaleOffset(5.0F);
        infiniteCycleViewPager.setOnInfiniteCyclePageTransformListener(...);*/
    }

    private List<Map<String,String>> getData(){
        List<Map<String,String>> list = new ArrayList<>();
        sql.open();
        Cursor cu = sql.getAllContacts("blescanresult");
        if (cu.getCount() > 0) {
            cu.moveToFirst();
            int indeximage = cu.getColumnIndex(sql.KEY_ROWID);
            int indexactnb = cu.getColumnIndex(sql.KEY_ACTNB);
            int indextime = cu.getColumnIndex(sql.KEY_TIME);
            int indexname = cu.getColumnIndex(sql.KEY_NAME);
            int indexistatus = cu.getColumnIndex("status");
            int indexiage = cu.getColumnIndex("age");
            int indexisex = cu.getColumnIndex("sex");
            int indexihumanity = cu.getColumnIndex("credit_value");
            int indexirealname = cu.getColumnIndex("real_name");
            int indexicredit = cu.getColumnIndex("independent_money");
            int indexiposition = cu.getColumnIndex("position");
            do {
                if(cu.getString(indexactnb) == null || cu.getString(indexname) == null){
                    continue;
                }
                Map<String, String> map = new HashMap<>();
                map.put(sql.KEY_ROWID, cu.getString(indeximage));
                map.put("status", cu.getString(indexistatus));
                map.put(sql.KEY_TIME, cu.getString(indextime));
                map.put("age", cu.getString(indexiage));
                map.put(sql.KEY_NAME, cu.getString(indexname));
                map.put(sql.KEY_ACTNB, cu.getString(indexactnb));
                map.put("sex", cu.getString(indexisex));
                map.put("credit_value", cu.getString(indexihumanity));
                map.put("real_name", cu.getString(indexirealname));
                map.put("independent_money", cu.getString(indexicredit));
                map.put("position", cu.getString(indexiposition));
                list.add(map);
                Log.d(TAG, "" + cu.getString(indeximage));
            } while (cu.moveToNext());
        }
        return list;
    }
}
