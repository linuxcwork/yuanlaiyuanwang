package com.example.yang.Activity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import es.voghdev.pdfviewpager.library.remote.DownloadFile;

public class PDFViewPage extends ViewPager{

        private Context mContext;
        protected DownloadFile.Listener listener;

        public PDFViewPage(Context context) {
            super(context);
            this.mContext = context;
        }

        public PDFViewPage(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mContext = context;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            try {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e) {
                //捕获，不做处理，避免崩溃
                Log.e("zzj", "PDFViewPager onTouchEvent() IllegalArgumentException");
            }
            return false;
        }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            try {
                return super.onInterceptTouchEvent(event);
            } catch (IllegalArgumentException e) {
                //捕获，不做处理，避免崩溃
                Log.e("zzj", "PDFViewPager onInterceptTouchEvent() IllegalArgumentException");
            }
            return false;
        }
}
