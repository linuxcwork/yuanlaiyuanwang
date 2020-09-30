package com.example.yang.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.yang.myapplication.R;

public class MessageView extends View {
    private Paint mPiant=null;
    private Paint mTextPiant=null;//画文字的笔
    private int rate=6;//圆和正方形的比例
    private int square_color;//正方形的颜色
    private int circle_color;
    private int text_color;
    private boolean isShow;//是否显示圆圈消息
    private String text;
    public MessageView(Context context) {
        this(context,null);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public synchronized void setText(String text) {
        this.text = text;
        init();
        invalidate();
    }
    public synchronized void setShow(boolean show) {
        isShow = show;
        init();
        invalidate();
    }
    private void init() {
        mPiant = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPiant = new Paint(Paint.ANTI_ALIAS_FLAG);
        square_color = getResources().getColor(R.color.yellow);
        circle_color = getResources().getColor(R.color.red);
        text_color = getResources().getColor(R.color.white);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = Math.min(getWidth(),getHeight());
        int h = Math.min(getWidth(),getHeight());

        //获取文字的宽度和高度
        Rect rect1=new Rect();
        mTextPiant.setColor(text_color);
        mTextPiant.setTextSize(30);
        mTextPiant.setFakeBoldText(true);
        mTextPiant.getTextBounds(text,0,text.length(),rect1);

        //获取指定图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.lau);

        //已知图片的尺寸，根据比例获取圆的半径
        int r = bitmap.getWidth() / rate;//圆的半径
        int c = bitmap.getWidth();//正方形周长

        //判断文字和圆的半径的大小关系，如果文字长度比圆半径大，那么就需要改变圆的半径
        int x_diff = rect1.centerX() -  r;
        int y_diff = rect1.centerY() -  r;
        if(x_diff>0||y_diff>0){
            r = (int) Math.sqrt(rect1.centerX()*rect1.centerX()+rect1.centerY()*rect1.centerY());
        }

        //画图形
        canvas.drawBitmap(bitmap,0,r,mPiant);

        if(isShow){//如果需要展示图标
            drawCircle(canvas, r, c);//画圆圈
            drawText(canvas, r, c,rect1);//画文本
        }
    }

    private void drawText(Canvas canvas,int r, int c,Rect rect1) {
        int x = c-rect1.centerX();
        int y = r - rect1.centerY();
        canvas.drawText(text,x,y, mTextPiant);
    }

    private void drawCircle(Canvas canvas, int r, int c) {
        mPiant.setColor(circle_color);
        canvas.drawCircle(c,r,r,mPiant);
    }

}
