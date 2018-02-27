package com.mastermycourse.pojos;

/**
 * Author: Zach Lerman.
 *
 * Plain old Java Object used to retrieve Question from the database.
 */
public class Question {
    private String questionType; // true/false, exact answer, etc.
    private String question;
    private String answer;
    private String[] prompts;
    private int question_id;
    private int id; // module id
    private int submissions; // how many times a student can submit this question
    private int points; // how many points this question is worth

    // these variables are only used for code questions
    private String template_code;
    private int language;

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getTemplate_code() {
        return template_code;
    }

    public void setTemplate_code(String template_code) {
        this.template_code = template_code;
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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String[] getPrompts() {
        return prompts;
    }

    public void setPrompts(String[] prompts) {
        this.prompts = prompts;
    }

    public String promptsToString() {
        String promptsString = "";
        for (int i = 0; i < prompts.length; i++) {
            promptsString += prompts[i];
            if (i != prompts.length - 1) {
                promptsString += ",";
            }
        }

        return promptsString;
    }
}
