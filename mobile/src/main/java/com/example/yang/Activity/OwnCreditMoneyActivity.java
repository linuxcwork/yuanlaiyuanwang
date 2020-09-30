package com.example.yang.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.yang.myapplication.R;

public class OwnCreditMoneyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own_credit_money_xml);
        initView();
    }

    private void initView(){
        RelativeLayout payrel = findViewById(R.id.own_credit_money_pay_type_rel);
        payrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog();
            }
        });
    }

    private void setDialog() {
        Dialog mCameraDialog = new Dialog(this, R.style.owncreditmoneydialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.pay_type_select_dialog, null);
        //初始化视图
       /* root.findViewById(R.id.btn_choose_img).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        root.findViewById(R.id.btn_cancel).setOnClickListener(this);*/
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }
}
