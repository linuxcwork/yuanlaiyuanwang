package com.example.yang.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.yang.fragment.ChatFuntionViewPager;

import static com.example.yang.network.OkHttpManager.TAG;

/**
 * author : yangzhengqiang
 * time : 2018年6月29日 上午11:14:27
 * email : linuxcwork@163.com
 * description :源码来自开源项目https://github.com/dss886/Android-EmotionInputDetector
 *              本人仅做细微修改以及代码解析
 */
public class EmotionKeyboard {
    ViewPager gviewpager;
    View mlinview;
    public static final int FPAGE_ONE = 0;

    private static final String SHARE_PREFERENCE_NAME = "EmotionKeyboard";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";
    private AppCompatActivity mActivity;
    private InputMethodManager mInputManager;//软键盘管理类
    private SharedPreferences sp;
    private View mEmotionLayout;//表情布局
    private EditText mEditText;//
    private View mContentView;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪

    private EmotionKeyboard(){
    }

    /**
     * 外部静态调用
     * @param activity
     * @return
     */
    public static EmotionKeyboard with(AppCompatActivity activity) {
        EmotionKeyboard emotionInputDetector = new EmotionKeyboard();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return emotionInputDetector;
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     * @param contentView
     * @return
     */
    public EmotionKeyboard bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    /**
     * 绑定编辑框
     * @param editText
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    public EmotionKeyboard bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mlinview.isShown()){
                    hideHighLayout(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                  //  lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                    //软件盘显示后，释放内容高度
                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          //  unlockContentHeightDelayed();
                        }
                    }, 200L);
                }
                return false;
            }
        });
        return this;
    }

    /**
     * 绑定表情按钮
     * @param emotionButton
     * @return
     */
    public EmotionKeyboard bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    //lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                    //unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                } else {
                    if(mlinview.isShown()){
                        hideHighLayout(false);
                    }
                    showEmotionLayout();
                }
            }
        });
        return this;
    }

    public void BindToViewPager(Context context,ViewPager viewPager){
        gviewpager = viewPager;
        FragmentManager chatmanager = mActivity.getSupportFragmentManager();
        FragmentTransaction chattransaction = chatmanager.beginTransaction();
        ChatFuntionViewPager  chatmAdapter = new ChatFuntionViewPager(context,chatmanager);
        viewPager.setAdapter(chatmAdapter);
        viewPager.setCurrentItem(0);

    }

    public void BindToAddFuntion(View imageView){
        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mEmotionLayout.isShown()) {
                    hideEmotionLayout(false);
                    showHighLayout();
                }else if(isSoftInputShown()){
                    hideSoftInput();
                    showHighLayout();
                }else {
                    showHighLayout();
                }
            }
        });
    }
    /**
     * 设置表情内容布局
     * @param emotionView
     * @return
     */
    public EmotionKeyboard setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }
    /*****************************************************************
     * 设置高级功能内容布局
     * ***************************************************************/
    public void setPagerView(View pagerview){
         mlinview= pagerview;
    }
    public EmotionKeyboard build(){
//设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE  这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软件盘
        hideSoftInput();
        return this;
    }
    /**
     * 点击返回键时先隐藏表情布局
     * @return
     */
    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }else if(mlinview.isShown()){
            hideHighLayout(false);
            return true;
        }
        return false;
    }


    public void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = getKeyBoardHeight();
        }
        hideSoftInput();
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    public void showHighLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = getKeyBoardHeight();
        }
        hideSoftInput();
        mlinview.getLayoutParams().height = softInputHeight;
        mlinview.setVisibility(View.VISIBLE);
    }
    /**
     * 隐藏表情布局
     * @param showSoftInput 是否显示软件盘
     */
    public void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }
    /**
     * 隐藏表情布局
     * @param showSoftInput 是否显示软件盘
     */
    public void hideHighLayout(boolean showSoftInput) {
        if (mlinview.isShown()) {
            mlinview.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }
    /**
     * 锁定内容高度，防止跳闪
     */
    public void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = getKeyBoardHeight();//mContentView.getHeight();
        params.weight = 0.0F;
    }
    /**
     * 释放被锁定的内容高度
     */
    public void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }
    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }
    /**
     * 隐藏软件盘
     */
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
    /**
     * 是否显示软件盘
     * @return
     */
    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }
    /**
     * 获取软件盘的高度
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;
        Log.d(TAG, "getSupportSoftInputHeight: "+softInputHeight+",screenHeight:"+screenHeight+",getSoftButtonsBarHeight:"+getSoftButtonsBarHeight());
        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
            Log.d(TAG, "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        //存一份到本地
        if (softInputHeight > 0) {
            sp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }
    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 获取软键盘高度
     * @return
     */
    public  int getKeyBoardHeight(){
        return sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 787);
    }
}

