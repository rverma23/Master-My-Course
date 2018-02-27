package com.mastermycourse.pojos;

/**
 * Author: Rahul Verma
 *
 * Plain old Java Object used when retrieving Table of Contents from database.
 */
public class TableOfContentPair {

    private String name;
    private int pageNo;

    public TableOfContentPair(){

    }

    public TableOfContentPair(String name, int pageNo){
        this.setName(name);
        this.setPageNo(pageNo);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
