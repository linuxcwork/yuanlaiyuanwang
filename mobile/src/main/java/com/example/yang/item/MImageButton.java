package com.example.yang.item;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/****************************************************************
 * @name MyApplication
 * @class name：com.example.yang.item
 * @class describe
 * @anthor yangz QQ:452164774
 * @time 2019/1/20 19:28
 * @change
 * @chang time
 * @class describe
 *****************************************************************/
public class MImageButton extends android.support.v7.widget.AppCompatImageButton {
    private int color;
    public MImageButton(Context context) {
        super(context);
        color = Color.parseColor("#80a3a4a7");
    }

    public MImageButton(Context context, AttributeSet attrs) {

        super( context, attrs );
    }

    public MImageButton(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        // 画边框
        Rect rec = canvas.getClipBounds();
        String TAG = null;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawRect(rec, paint);
    }

}
