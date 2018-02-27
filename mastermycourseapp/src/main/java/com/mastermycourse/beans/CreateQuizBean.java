package com.mastermycourse.beans;

import com.mastermycourse.database.QuestionDBCommands;
import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.pojos.Question;
import com.mastermycourse.pojos.Quiz;
import org.apache.commons.lang3.StringEscapeUtils;
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
 * This session scoped bean contains all the controls for a teacher who is creating a quiz
 * implements Serializable and ApplicationContextAware
 */
public class CreateQuizBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;
    private ArrayList<Question> question_list; // The questions available in this course
    private ArrayList<Question> quiz_questions; // questions for a specific quiz
    private ArrayList<Quiz> quizzes; // list of quizzes with the course id of courseId
    private boolean editQuiz; // If true, then the page will display controls to edit an existing quiz
    private String quizName; // The name of the quiz you are editing, only valid when editQuiz = true
    private int quizId; // The id of the quiz you are editing, only valid when editQuiz = true
    private int courseId;
    private String date = "";
    private String teacherNotes;
    private Logger log = Logger.getLogger(CreateQuizBean.class.getName());

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacherNotes() {
        return StringEscapeUtils.escapeEcmaScript(teacherNotes);
    }

    public void setTeacherNotes(String teacherNotes) {
        this.teacherNotes = teacherNotes;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public ArrayList<Question> getQuestion_list() {
        return question_list;
    }

    public ArrayList<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(ArrayList<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public void setQuestion_list(ArrayList<Question> question_list) {
        this.question_list = question_list;
    }

    public ArrayList<Question> getQuiz_questions() {
        return quiz_questions;
    }

    public void setQuiz_questions(ArrayList<Question> quiz_questions) {
        this.quiz_questions = quiz_questions;
    }

    public boolean isEditQuiz() {
        return editQuiz;
    }

    public void setEditQuiz(boolean editQuiz) {
        this.editQuiz = editQuiz;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * This method gets all the questions with the given course id of this
     * class's courseId property and sets the question_list property to it this list.
     */
    public void initQuestions(){
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);
        try {
            question_list = qdbc.getQuestions(courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        qdbc.closeConnection();
    }

    /**
     * This method gets all questions with the quiz id of the parameter passed in
     * @param quizId the quiz id to get the questions from
     * @return a list of questions that is in the quiz
     */
    public ArrayList<Question> getQuizQuestions(int quizId) {
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);
        ArrayList<Question> questionsList = null;
        try {
            questionsList = qdbc.getQuizQuestions(quizId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        qdbc.closeConnection();
        return questionsList;
    }

    /**
     * This method gets all the quizzes with the given course id of this
     * class's courseId property and sets the quizzes property to it this list.
     */
    public void initQuizzes() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        try {
            quizzes = tdbc.getQuizzes(courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
