package com.example.yang.richeditor.span;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import com.example.yang.richeditor.enumtype.RichTypeEnum;


/**
 * Title:
 * Description:
 *
 * @author yangzhengqiang
 * @version 2019-04-29
 */
public class ItalicStyleSpan extends StyleSpan implements IInlineSpan {

    private String type;

    public ItalicStyleSpan() {
        super(Typeface.ITALIC);
        type = RichTypeEnum.ITALIC;
    }

    @Override
    public String getType() {
        return type;
    }
}
