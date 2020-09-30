package com.example.yang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.myapplication.R;

import java.util.List;
import java.util.Map;

public class AnnounceAdapt extends BaseAdapter {
    Context mcontext;
    List<Map<String, Object>> mlist;
    public AnnounceAdapt(Context context, List<Map<String, Object>> list){
        mcontext = context;
        mlist = list;
    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold vendor = new ViewHold();
        //String type = mlist.get(position).get("type").toString();
        //convertView = View.inflate(mcontext, R.layout.announce_advert_adapt_activity, null);
        convertView = View.inflate(mcontext, R.layout.announce_event_adapt_activity, null);
        vendor.image = convertView.findViewById(R.id.announce_event_adapt_image);
        vendor.title = convertView.findViewById(R.id.announce_event_adapt_title);
        vendor.sponsor = convertView.findViewById(R.id.announce_event_adapt_sponsor);
        vendor.time = convertView.findViewById(R.id.announce_event_adapt_time);
        vendor.num = convertView.findViewById(R.id.announce_event_adapt_num);
        vendor.image.setBackgroundResource((Integer)mlist.get(position).get("image"));
        vendor.title.setText(mlist.get(position).get("title").toString());
        vendor.sponsor.setText(mlist.get(position).get("sponsor").toString());
        vendor.time.setText(mlist.get(position).get("startdate").toString());
        vendor.num.setText(mlist.get(position).get("num").toString());
        return convertView;
    }

    class ViewHold {
        public ImageView image;
        public TextView title, sponsor, time,num;

    }
}
