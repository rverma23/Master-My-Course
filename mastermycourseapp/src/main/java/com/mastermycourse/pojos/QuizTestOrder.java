package com.mastermycourse.pojos;

/**
 * Author: Zach Lerman.
 *
 * Plain old Java Object used when retrieving quiz test order from database.
 */
public class QuizTestOrder {
    String title;
    int submissions;
    int points;
    int id;
    int questionId;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionID) {
        this.questionId = questionID;
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

    public int getSubmissions() {
        return submissions;
    }

    public void setSubmissions(int submissions) {
        this.submissions = submissions;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
