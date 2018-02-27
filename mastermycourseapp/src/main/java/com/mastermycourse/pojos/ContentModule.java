package com.mastermycourse.pojos;
/**
 * Author: Zach Lerman
 *
 * Plain old java object to hold content for content retrieved from the database.
 */
public class ContentModule {
    int pageNumber;
    String style;
    String body;
    String title;
    String chapterTitle;
    int quizId; // end of chapter quiz

    public ContentModule() {

    }

    public ContentModule(int pageNumber, String style, String body, String title, String chapterTitle) {
        this.pageNumber = pageNumber;
        this.style = style;
        this.body = body;
        this.title = title;
        this.chapterTitle = chapterTitle;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

}


