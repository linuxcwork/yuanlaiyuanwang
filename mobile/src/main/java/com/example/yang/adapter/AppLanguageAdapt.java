package com.example.yang.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.yang.myapplication.R;

import java.util.List;

public class AppLanguageAdapt extends BaseAdapter {
    private Context mcontext;
    private List<String> mlist;
    private static int mposition = 0;
    public AppLanguageAdapt(Context context, List<String> list){
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
        ViewHold viewHold = new ViewHold();
        convertView = View.inflate(mcontext, R.layout.app_language_adapt_xml, null);
        viewHold.title = convertView.findViewById(R.id.language_name_text);
        viewHold.isselect = convertView.findViewById(R.id.language_is_select);
        viewHold.title.setText(mlist.get(position));
        if(position == mposition){
            viewHold.isselect.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public static void setmPosition(int position){
        mposition = position;
    }

    public class ViewHold {
        public TextView title;
        public ImageView isselect;
    }
}
