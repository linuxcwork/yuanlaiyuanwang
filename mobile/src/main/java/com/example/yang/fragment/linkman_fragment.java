package com.example.yang.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amap.map3d.demo.basic.MapLocationPosition;
import com.example.yang.Activity.AnnounceActivity;
import com.example.yang.Activity.ConditionalLookupActivity;
import com.example.yang.Activity.LandOfThought;
import com.example.yang.adapter.ContactAdapter;
import com.example.yang.adapter.ContactScrollerAdapter;
import com.example.yang.myapplication.R;
import com.example.yang.util.Contact;

import java.util.List;

import cdflynn.android.library.scroller.BubbleScroller;
import cdflynn.android.library.scroller.ScrollerListener;

/**
 * Created by yang on 2018/3/18.
 */

public class linkman_fragment extends Fragment implements View.OnClickListener {

    private Views mViews;
    private ContactScrollerAdapter mContactScrollerAdapter;
    private ContactAdapter mContactAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean mProgrammaticScroll = true;
    private final ScrollerListener mScrollerListener = new ScrollerListener() {
        @Override
        public void onSectionClicked(int sectionPosition) {
            int position = mContactScrollerAdapter.positionFromSection(sectionPosition);
            if(position != 10000){
                mViews.recycler.smoothScrollToPosition(
                        mContactScrollerAdapter.positionFromSection(sectionPosition));
                mProgrammaticScroll = true;
            }

        }

        @Override
        public void onScrollPositionChanged(float percentage, int sectionPosition) {
            int position = mContactScrollerAdapter.positionFromSection(sectionPosition);
            if(position != 10000){
                mViews.recycler.smoothScrollToPosition(
                        mContactScrollerAdapter.positionFromSection(sectionPosition));
                mProgrammaticScroll = true;
            }

        }
    };

    static class Views {
        BubbleScroller scroller;
        RecyclerView recycler;

        Views(View activity) {
            scroller = (BubbleScroller) activity.findViewById(R.id.bubble_scroller);
            recycler = (RecyclerView) activity.findViewById(R.id.recycler);
        }
    }

    public linkman_fragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_linkmanxml, container, false);
        LinearLayout map = view.findViewById(R.id.map_search);
        map.setOnClickListener(this);
        LinearLayout article = view.findViewById(R.id.game_makefri);
        article.setOnClickListener(this);
        LinearLayout gathering = view.findViewById(R.id.announ_fri);
        gathering.setOnClickListener(this);
        LinearLayout condition = view.findViewById(R.id.searchfri_bycond);
        condition.setOnClickListener(this);

        mViews = new Views(view);
        List<Contact> contactList = Contact.mocks(getActivity());
        mContactScrollerAdapter = new ContactScrollerAdapter(contactList);
        mContactAdapter = new ContactAdapter(getActivity(), contactList, mContactScrollerAdapter);
        mLayoutManager = new LinearLayoutManager(getContext());
        mViews.scroller.setScrollerListener(mScrollerListener);
        mViews.scroller.setSectionScrollAdapter(mContactScrollerAdapter);
        mViews.recycler.setLayoutManager(mLayoutManager);
        mViews.recycler.setAdapter(mContactAdapter);
        mViews.scroller.showSectionHighlight(0);
        mViews.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mProgrammaticScroll) {
                    mProgrammaticScroll = false;
                    return;
                }
             /*   final int firstVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                mViews.scroller.showSectionHighlight(
                        mContactScrollerAdapter.sectionFromPosition(firstVisibleItemPosition));*/
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_search:
                Intent intent_map = new Intent(getContext(),MapLocationPosition.class);
                intent_map.putExtra("mapresource","linkman_fragment");
                startActivity(intent_map);
                break;
            case R.id.game_makefri:
                Intent intent_thought = new Intent(getContext(), LandOfThought.class);
                intent_thought.putExtra("mapresource","linkman_fragment");
                startActivity(intent_thought);
                break;
            case R.id.announ_fri:
                Intent intent_announce = new Intent(getContext(), AnnounceActivity.class);
                startActivity(intent_announce);
                break;
            case R.id.searchfri_bycond:
                Intent intent_cond = new Intent(getContext(), ConditionalLookupActivity.class);
                startActivity(intent_cond);
                break;
                default:
                    System.out.println("no id");
        }
    }
}
