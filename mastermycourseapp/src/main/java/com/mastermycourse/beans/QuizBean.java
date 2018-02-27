package com.mastermycourse.beans;

import com.mastermycourse.database.QuestionDBCommands;
import com.mastermycourse.pojos.Question;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authors: Zach Lerman and James DeCarlo.
 *
 * This bean contains all data and methods for a student taking a quiz.
 * Created by Zach on 4/7/17.
 */
public class QuizBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;
    private ArrayList<Question> question_list; // list of questions in the quiz
    private int quizId;
    private int numQuestions; // number of questions in the quiz
    private int index; // the index of the current question they are on, students answer question_list.get(index);
    private int attempts; // the number of attempts a student has left for the question they are on
    private String title; // the name of the quiz
    private String hacker_response = "";
    private Logger log = Logger.getLogger(QuizBean.class.getName());

    public String getHacker_response() {
        return hacker_response;
    }

    public void setHacker_response(String hacker_response) {
        this.hacker_response = hacker_response;
    }

    public ArrayList<Question> getQuestion_list() {
        return question_list;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuizId() { return quizId; }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    /**
     * Gets the list of questions for the current quiz id. Quiz id must be set before this method can be usee.
     * @return List of Questions for current quiz id
     */
    public ArrayList<Question> getQuestions() {
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);
        ArrayList<Question> questionsList = null;
        try {
            questionsList = qdbc.getQuizQuestions(quizId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        qdbc.closeConnection();
        question_list = questionsList;
        setNumQuestions(questionsList.size());
        return questionsList;
    }

    /**
     * Get the current question the student needs to take in the quiz
     * @return
     */
    public Question getQuestion() {
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);
        Question question = question_list.get(index);

        if (attempts == -1) {
            try {
                attempts = qdbc.getAttempts(quizId, question.getId());
            } catch (SQLException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
            qdbc.closeConnection();
        }

        return question;
    }

    /**
     * Return the title of the quiz
     * @return
     */
    public String getTitle() {
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);
        String title = null;
        try {
            title =  qdbc.getQuizName(quizId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        qdbc.closeConnection();
        return title;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
