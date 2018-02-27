package com.mastermycourse.pojos;

/**
 *  Authors: Zach Lerman and James DeCarlo.
 *
 * Plain old Java Object used when retrieving objects from the database.
 */
public class Outline {
    private int contentModuleId;
    private String pageTitle;
    private String chapterTitle;
    int quizId;

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public Outline() {
    }

    public Outline(int contentModuleId, String pageTitle, String chapterTitle) {
        this.contentModuleId = contentModuleId;
        this.pageTitle = pageTitle;
        this.chapterTitle = chapterTitle;
    }

    public int getContentModuleId() {
        return contentModuleId;
    }

    public void setContentModuleId(int contentModuleId) {
        this.contentModuleId = contentModuleId;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }
}
