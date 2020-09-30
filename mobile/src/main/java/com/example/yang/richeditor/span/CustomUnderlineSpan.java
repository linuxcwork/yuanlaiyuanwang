package com.example.yang.richeditor.span;

import android.text.style.UnderlineSpan;

import com.example.yang.richeditor.enumtype.RichTypeEnum;


/**
 * Title:
 * Description:
 *
 * @author yangzhengqiang
 * @version 2019-04-30
 */
public class CustomUnderlineSpan extends UnderlineSpan implements IInlineSpan {

    private String type;

    public CustomUnderlineSpan() {
        type = RichTypeEnum.UNDERLINE;
    }

    @Override
    public String getType() {
        return type;
    }

}
