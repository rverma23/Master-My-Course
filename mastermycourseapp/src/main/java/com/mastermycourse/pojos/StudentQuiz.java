package com.mastermycourse.pojos;

/**
 * Author: Rahul Verma
 *
 * Plain old Java Object used when retrieving Student Quiz from database.
 */
public class StudentQuiz {

    private int userId;
    private Quiz quiz;

    public StudentQuiz(){
        this.quiz= new Quiz();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

}
