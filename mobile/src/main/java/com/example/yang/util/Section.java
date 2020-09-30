package com.example.yang.util;

public class Section {
    private int mIndex;
    private String mTitle;
    private int mWeight = 1;
    private int mposition;
    
    public Section(int index, String title, int position) {
        mIndex = index;
        mTitle = title;
        mposition = position;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getWeight() {
        return mWeight;
    }

    public int getPosition(){
        return mposition;
    }

    @Override
    public String toString() {
        return "Section{" +
                "mIndex=" + mIndex +
                ", mTitle='" + mTitle + '\'' +
                ", mWeight=" + mWeight +
                '}';
    }
}
