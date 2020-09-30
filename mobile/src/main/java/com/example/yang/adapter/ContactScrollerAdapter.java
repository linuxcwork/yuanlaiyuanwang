package com.example.yang.adapter;

import com.example.yang.util.Contact;
import com.example.yang.util.Section;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cdflynn.android.library.scroller.SectionScrollAdapter;

public class ContactScrollerAdapter implements SectionScrollAdapter {

    private List<Contact> mContacts;
    private List<Section> mSections;
    private String[] indexArry = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};

    public ContactScrollerAdapter(List<Contact> contacts) {
        initWithContacts(contacts);
    }

    @Override
    public int getSectionCount() {
        return mSections.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return mSections.get(position).getTitle();
    }

    @Override
    public int getSectionWeight(int position) {
        return mSections.get(position).getWeight();
    }

    public Section fromSectionIndex( int sectionIndex) {
        return mSections.get(sectionIndex);
    }

    public Section fromItemIndex(int itemIndex) {
        for (Section s : mSections) {
            final int range = s.getPosition();
            if (itemIndex == range) {
                return s;
            }
        }

        return null;
    }

    public int positionFromSection(int sectionIndex) {
        return mSections.get(sectionIndex).getPosition();
    }

    public int sectionFromPosition(int positionIndex) {
        Section s = null;
        Contact contact = mContacts.get(positionIndex);
        String name = getPingYin(contact.getFirstName());
        String firstLetter = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
        if(firstLetter.charAt(0) >= 'A' && firstLetter.charAt(0) <= 'Z') {
            return firstLetter.charAt(0) - 'A';
        }

        return mSections.size()-1;
    }

    private void initWithContacts(List<Contact> contacts) {
        mContacts = contacts;
        mSections = new ArrayList<>();
        String sectionTitle = null;
        Collections.sort(mContacts, Contact.COMPARATOR);
        Contact contact;
        int itemCount = 0;

        for(String obj : indexArry) {
            String s = (String) obj;
            mSections.add(itemCount,new Section(itemCount, obj,10000 ));
            itemCount++;
        }
        itemCount = 0;

        for (int i = 0; i < mContacts.size(); i++) {

            contact = mContacts.get(i);
            String name = getPingYin(contact.getFirstName());
			String firstLetter = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);

            if (sectionTitle == null || sectionTitle.compareTo(firstLetter) != 0) {
                if(firstLetter.charAt(0) >= 'A' && firstLetter.charAt(0) <= 'Z') {
                    int index = mSections.get(firstLetter.charAt(0) - 'A').getIndex();
                    mSections.set(firstLetter.charAt(0) - 'A', new Section(index, firstLetter, itemCount));
                }else {
                    mSections.set(indexArry.length-1, new Section(indexArry.length-1, "#", itemCount));
                }
            }

            sectionTitle = firstLetter;
            itemCount++;
        }
    }

    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] input = inputString.trim().toCharArray();
        String output = "";
        try {
            for (char curchar : input) {
                if (java.lang.Character.toString(curchar).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, format);
                    output += temp[0];
                } else {
                    output += java.lang.Character.toString(curchar);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }
}
