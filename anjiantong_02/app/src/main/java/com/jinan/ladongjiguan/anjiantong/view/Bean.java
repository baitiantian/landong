package com.jinan.ladongjiguan.anjiantong.view;


public class Bean {

    private int iconId;
    private String title;
    private CharSequence content;
    private String comments;

//    public Bean( String title,CharSequence content) {
//        this.title = title;
//        this.content = content;
//    }

    public Bean( String title) {
        this.title = title;
    }
//    public Bean( CharSequence content) {
//        this.content = content;
//    }
    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CharSequence getContent() {
        return content;
    }

    public void setContent(CharSequence content) {
        this.content = content;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
