package com.example.yang.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.R;
import com.example.yang.myapplication.chat_contrue;
import com.example.yang.myapplication.sqlite_linkmanmss;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.yang.fragment.messageoff_fragment.MSGTYPECHATTEXT;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_ACTNB;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_NAME;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_ROWID;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_TIME;


/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {
    private final String TAG = "RecommendFriendActivity";
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<Map<String,String>> mlist = new ArrayList<>();

    public HorizontalPagerAdapter(final Context context,List<Map<String,String>> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mlist = list;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        Log.d(TAG,"instantiateItem "+position);
        Map<String,String> map =  mlist.get(position);
        View view = mLayoutInflater.inflate(R.layout.recommend_friend_activity_contant_xml, container, false);
       // Utils.setupItem(view, mlist.get(position));
        final ImageView status = (ImageView) view.findViewById(R.id.reconmmend_friend_txt_card_status);
        if(map.get("status") != null){
            status.setVisibility(View.VISIBLE);
        }
        final ImageView sex = (ImageView) view.findViewById(R.id.reconmmend_friend_activity_sex);
        if (map.get("sex") != null && map.get("sex").equals("1")){

        }else {

        }
        final TextView age = (TextView) view.findViewById(R.id.reconmmend_friend_txt_age);
        age.setText(map.get("age"));
        final TextView tposition = (TextView) view.findViewById(R.id.reconmmend_friend_activity_marital_tatus);
        tposition.setText(map.get("position"));
        final ImageView img = (ImageView) view.findViewById(R.id.reconmmend_friend_img_avatar);
        if (map.get(KEY_ROWID) != null) {
            byte[] bbyte = Base64.decode(map.get(KEY_ROWID).toString(), 0);
            InputStream inputStream = new ByteArrayInputStream(bbyte);
            img.setImageBitmap(BitmapFactory.decodeStream(inputStream));
        }

        if (map.get(KEY_NAME) != null) {
            final TextView txt = (TextView) view.findViewById(R.id.reconmmend_friend_txt_nickname);
            txt.setText(map.get(KEY_NAME).toString());
        }
        final TextView realname = (TextView) view.findViewById(R.id.reconmmend_friend_txt_realname);
        realname.setText(map.get("real_name"));
        final TextView humanity = (TextView) view.findViewById(R.id.reconmmend_friend_txt_temperature_of_human_nature);
        humanity.setText(map.get("credit_value"));
        final TextView credit = (TextView) view.findViewById(R.id.reconmmend_friend_txt_credit);
        credit.setText(map.get("independent_money"));

        final Button remove = (Button) view.findViewById(R.id.reconmmend_friend_txt_remove);
        final Button chatwith = (Button) view.findViewById(R.id.reconmmend_friend_txt_chat_with);
        chatwith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlite_linkmanmss sql = new sqlite_linkmanmss(mContext, "link", null, 1);
                sql.open();
                Cursor cursor = sql.getContact(MainActivity.friendinfotable, sql.KEY_ACTNB, map.get(sql.KEY_ACTNB));
                if(cursor != null && cursor.moveToFirst() && cursor.getCount() == 0) {
                    Map<String, Object> addfri = new HashMap<>();
                    addfri.put(sql.KEY_ROWID, map.get(sql.KEY_ROWID));
                    addfri.put(sql.KEY_TIME, map.get(sql.KEY_TIME));
                    addfri.put(sql.KEY_NAME, map.get(sql.KEY_NAME));
                    addfri.put(sql.KEY_ACTNB, map.get(sql.KEY_ACTNB));
                    addfri.put(sql.EKY_MESSAGETYPE, MSGTYPECHATTEXT);
                    sql.insertContact(MainActivity.friendinfotable, addfri);
                }
                sql.close();
                Bundle bundle = new Bundle();
                bundle.putString("name", map.get(KEY_ACTNB));
                Intent intent = new Intent(mContext, chat_contrue.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        final TextView date = (TextView) view.findViewById(R.id.reconmmend_friend_txt_time);
        date.setText(map.get(KEY_TIME));

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
