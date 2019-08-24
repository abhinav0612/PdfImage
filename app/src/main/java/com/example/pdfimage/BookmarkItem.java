package com.example.pdfimage;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class BookmarkItem {
    private String title;
    private  int  index;

    public BookmarkItem(String title, int index) {
        this.title = title;
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
