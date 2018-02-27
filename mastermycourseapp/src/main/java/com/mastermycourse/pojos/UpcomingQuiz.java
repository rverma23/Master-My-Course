package com.mastermycourse.pojos;

/**
 * Author: Zach Lerman.
 */
public class UpcomingQuiz {
    private int id;  // quizid
    private String title; // the quiz title
    private String teachersNote; // teachers notes on the quiz
    private String date; // the date as a string

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeachersNote() {
        return teachersNote;
    }

    public void setTeachersNote(String teachersNote) {
        this.teachersNote = teachersNote;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
