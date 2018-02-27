package com.mastermycourse.beans;


import com.mastermycourse.database.QuestionDBCommands;
import com.mastermycourse.database.StudentDBCommands;
import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.json.JsonConverter;
import com.mastermycourse.pojos.Quiz;
import com.mastermycourse.pojos.TeacherNote;
import com.mastermycourse.pojos.UpcomingQuiz;
import com.mastermycourse.pojos.User;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Authors: Rahul Verma, James DeCarlo, and Zach Lerman.
 *
 * Session scoped bean used to handle student metrics. Used in student metrics page.
 * Implements Serializable and ApplicationContextAware.
 *
 * ApplicationContext, studentId and courseId must be set prior to using the other methods in this bean.
 */
public class StudentMetricsBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;

    private int studentId; // when teacher selects specific student
    private int courseId;
    private String email; // the users email
    private Logger log = Logger.getLogger(StudentMetricsBean.class.getName());

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public ArrayList<Quiz> getGradedQuizzes() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        ArrayList<Quiz> quizzes = null;
        try {
            quizzes = tdbc.getGradedStudentQuizzes(studentId,courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        return quizzes;
    }

    public String getQuizzesHistogram() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        String quizzes = null;
        try {
            quizzes = tdbc.getWholeClassQuizzes(this.courseId, true);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        return quizzes.replaceAll("\"","'");
    }

    public String getGradedCalculatedQuizzes() {

        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        ArrayList<Quiz> quizzes = null;
        try {
            quizzes = tdbc.getGradedStudentQuizzes(this.studentId, this.courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        if(quizzes == null){
            return null;
        }

        String ret = null;
        try {
            ret = (new JsonConverter()).convertQuizArrayListToJSON(quizzes);
            return ret.replaceAll("\"","'");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;

    }

    public String getWholeClassQuizzes() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        String quizzes = null;
        try {
            quizzes = tdbc.getWholeClassQuizzes(courseId,false);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        if(quizzes == null){
            return null;
        }
        return quizzes.replaceAll("\"","'");
    }

    /**
     * Get student notes for given quiz id
     * @return
     */
    public String getNotes(int quizId) throws SQLException, IOException {
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        try {
            User user = sdbc.getUserByEmail(email);
            ArrayList<TeacherNote> notes = qdbc.getNotes(quizId, user.getId());
            String json_notes = JsonConverter.convertToJson(notes);
            return json_notes.replaceAll("\"", "'");
        } catch(Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return "";
        }
    }

    /**
     * Get all the upcoming Quizzes to display to the student
     * @return a string of upcoming quizzes
     */
    public String upcoming() throws SQLException, IOException {
        QuestionDBCommands qdbc = new QuestionDBCommands(ctx);
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        try {
            ArrayList<UpcomingQuiz> quizzes = qdbc.getUpcomingQuizzes(this.courseId);
            ArrayList<UpcomingQuiz> non_taken = new ArrayList<UpcomingQuiz>();
            for (int i = 0; i < quizzes.size(); i++) {
                UpcomingQuiz quiz = quizzes.get(i);
                if (sdbc.tookQuiz(quiz.getId(), email) != 1) {
                    non_taken.add(quiz);
                }
            }
            String json_quizzes = JsonConverter.convertToJson(non_taken);
            return json_quizzes.replaceAll("\"", "'");
            //return StringEscapeUtils.escapeEcmaScript(json_quizzes);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return "";
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

}
