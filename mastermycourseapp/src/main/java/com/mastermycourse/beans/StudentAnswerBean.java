package com.mastermycourse.beans;

import com.mastermycourse.database.QuestionDBCommands;
import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.pojos.StudentAnswer;
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
 * This is a session scoped bean to for the student to hold what quiz and the students id and returns the Answers
 * Implements Serializable and ApplicationContextAware
 */
public class StudentAnswerBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;
    private int studentId;
    private int quizId;
    private Logger log = Logger.getLogger(StudentAnswerBean.class.getName());

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    /**
     * Returns a list of StudentAnswer objects for a given student id and
     * quiz id.
     * @return a list of StudentAnswer objects
     */
    public ArrayList<StudentAnswer> getAnswers() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);

        ArrayList<StudentAnswer> answers = null;
        try {
            answers = tdbc.getStudentAnswers(this.studentId, this.quizId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();

        if(answers != null){

            for (int i = 0; i < answers.size(); i++) {
                try {
                    answers.get(i).setQuestion(qdbc.getQuestionById(answers.get(i).getQuizTestModuleQuestionId()));
                } catch (SQLException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                    break;
                }
            }
        }
        qdbc.closeConnection();
        return answers;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}