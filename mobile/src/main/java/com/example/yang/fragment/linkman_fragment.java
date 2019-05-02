package com.example.yang.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.amap.map3d.demo.basic.MapLocationPosition;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.yang.myapplication.Linkman_Listview_Adapt;
import com.example.yang.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2018/3/18.
 */

public class linkman_fragment extends Fragment implements View.OnClickListener {

    private LinearLayout map_search;
    private LinearLayout game_get_fri;
    private LinearLayout announ_get_fri;
    private LinearLayout condition_find;
    ListView listView;
    public linkman_fragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_linkmanxml, container, false);
        link_init(view);
        List<Map<String, Object>> list=getData();
        listView.setAdapter(new Linkman_Listview_Adapt(getActivity(), list));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void link_init(View view){
        //mImgGlide = (ImageView) findViewById(R.id.img_glide);
        listView = (ListView)view.findViewById(R.id.list_linkman);
        map_search = view.findViewById(R.id.map_search);
        game_get_fri = view.findViewById(R.id.game_makefri);
        announ_get_fri = view.findViewById(R.id.announ_fri);
        condition_find = view.findViewById(R.id.searchfri_bycond);
        map_search.setOnClickListener(this);
        game_get_fri.setOnClickListener(this);
        announ_get_fri.setOnClickListener(this);
        condition_find.setOnClickListener(this);
    }

    /**************************************************************************
     * Name ：      Circleimage
     * descript ：  设置圆形图像
     * return ：    void
    ***************************************************************************/
    private void Circleimage(String mImgUrl, ImageView mImgGlide){
        // 使用 Glide 设置圆形画像
        Glide.with(this)
                .load(mImgUrl)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(mImgGlide) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        RoundedBitmapDrawable roundedBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        roundedBitmapDrawable.setCircular(true);
                        mImgGlide.setImageDrawable(roundedBitmapDrawable);
                    }
                });
    }
    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("image", R.drawable.ic_launcher_background);
            map.put("title", "这是一个标题"+i);
            list.add(map);
        }

        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_search:
                Intent intent_map = new Intent(getContext(),MapLocationPosition.class);
                intent_map.putExtra("mapresource","linkman_fragment");
                startActivity(intent_map);
                break;
            case R.id.game_makefri:

                break;
            case R.id.announ_fri:

                break;
            case R.id.searchfri_bycond:
                break;
                default:
                    System.out.println("no id");
        }
    }
}
