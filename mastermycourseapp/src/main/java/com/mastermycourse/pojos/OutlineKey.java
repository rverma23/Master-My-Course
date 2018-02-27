package com.mastermycourse.pojos;

/**
 * Authors: Zach Lerman and James DeCarlo.
 *
 * Plain old Java Object When retrieving the course outline from the database
 */
public class OutlineKey {

    private int pageIndex;
    private String chapter;

    public OutlineKey() {
    }

    public OutlineKey(int pageIndex, String chapter) {
        this.pageIndex = pageIndex;
        this.chapter = chapter;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }
}
