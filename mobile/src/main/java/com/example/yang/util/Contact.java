package com.example.yang.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.example.yang.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.yang.adapter.ContactScrollerAdapter.getPingYin;

public class Contact {

    private Drawable mProfileImage;
    private String mFirstName;
    private String mLastName;

    private Contact() {
        //
    }

    public Contact(Drawable profileImage, String firstName, String lastName) {
        mProfileImage = profileImage;
        mFirstName = firstName;
        mLastName = lastName;
    }

    public Drawable getProfileImage() {
        return mProfileImage;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public static List<Contact> mocks(Context c) {
        List<Contact> contacts = new ArrayList<>(60);

        //获取联系人信息
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.app_name, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.login, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.telphone, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.installed, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.popular, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.getver, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.inputver, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.tipspaswwd, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.album_tips, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.album_editable, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.setting_activity_chat_setting, R.string.account_and_secure));
        contacts.add(fromRes(c, R.drawable.d_haha, R.string.setting, R.string.account_and_secure));

        Collections.sort(contacts, COMPARATOR);
        return contacts;
    }

    public static final Comparator<Contact> COMPARATOR = new Comparator<Contact>() {
        @Override
        public int compare(Contact o1, Contact o2) {
            String str1 = getPingYin(o1.getFirstName());
            String str2 = getPingYin(o2.getFirstName());
            int flag = str1.compareTo(str2);
            return flag;
        }
    };

    private static Contact fromRes(Context c, @DrawableRes int img, @StringRes int fn, @StringRes int ln) {
        return new Contact(ContextCompat.getDrawable(c, img), c.getString(fn), c.getString(ln));
    }
}
