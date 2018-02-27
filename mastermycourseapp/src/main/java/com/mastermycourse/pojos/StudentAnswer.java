package com.mastermycourse.pojos;

/**
 * Author: Zach Lerman.
 *
 * Plain old Java Object used when retrieving Student Answer from database.
 */
public class StudentAnswer {
    int userId;
    int quizTestModuleId;
    int quizTestModuleQuestionId;
    String answer;
    boolean isCorrect;
    String teacherComment;
    Question question; // the question the student was answering

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuizTestModuleId() {
        return quizTestModuleId;
    }

    public void setQuizTestModuleId(int quizTestModuleId) {
        this.quizTestModuleId = quizTestModuleId;
    }

    public int getQuizTestModuleQuestionId() {
        return quizTestModuleQuestionId;
    }

    public void setQuizTestModuleQuestionId(int quizTestModuleQuestionId) {
        this.quizTestModuleQuestionId = quizTestModuleQuestionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public StudentAnswer() {

    }

    public StudentAnswer(int userId, int quizTestModuleQuestionId, String answer, boolean isCorrect) {
        this.userId = userId;
        this.quizTestModuleQuestionId = quizTestModuleQuestionId;
        this.answer = answer;
        this.isCorrect = isCorrect;
    }
}
