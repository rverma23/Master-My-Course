package com.mastermycourse.pojos;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

/**
 * Authors: Zach Lerman and Rahul Verma.
 *
 * Plain old Java Object used when retrieving quizzes from database.
 */
public class Quiz {
    String title;
    int id;
    ArrayList<QuizTestOrder> questions;
    String teacherNotes;
    private int totalpoints;
    private int actualpoints;
    private double computedGrade;
    private String date;

    public String getTeacherNotes() {
        return StringEscapeUtils.escapeEcmaScript(teacherNotes);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTeacherNotes(String teacherNotes) {
        this.teacherNotes = teacherNotes;
    }

    public Quiz() {
        questions = new ArrayList<QuizTestOrder>();
    }

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

    public ArrayList<QuizTestOrder> getQuestions() {
        return questions;
    }

    public int getTotalpoints() {
        return totalpoints;
    }

    public void setTotalpoints(int totalpoints) {
        this.totalpoints = totalpoints;
    }

    public int getActualpoints() {
        return actualpoints;
    }

    public void setActualpoints(int actualpoints) {
        this.actualpoints = actualpoints;
    }

    public double getComputedGrade() {
        return computedGrade;
    }

    public void setComputedGrade(double computedGrade) {
        this.computedGrade = computedGrade;
    }
}
