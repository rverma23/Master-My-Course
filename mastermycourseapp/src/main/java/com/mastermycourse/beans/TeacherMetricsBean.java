package com.mastermycourse.beans;

import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.json.JsonConverter;
import com.mastermycourse.pojos.ChapterMetrics;
import com.mastermycourse.pojos.Quiz;
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
 * Authors: Zach Lerman, Rahul Verma and James DeCarlo.
 *
 * Session scoped bean used in teacher metrics page to display teacher metrics. The courseId must be set for this bean
 * to work properly. Set studentId when you want to change to the metrics view for a specific student.
 *
 */
public class TeacherMetricsBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;

    private int studentId; // when teacher selects specific student
    private int courseId;
    private Logger log = Logger.getLogger(TeacherMetricsBean.class.getName());

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

    /**
     * Gets all quizzes for a given student
     * @return
     */
    public ArrayList<Quiz> getStudentQuizzes() {
        if (studentId == 0) {
            return null;
        }
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        ArrayList<Quiz> quizzes = null;
        try {
            quizzes = tdbc.getStudentQuizzes(this.studentId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        return quizzes;
    }

    /**
     * Gets a student quizzes that have been graded already
     * @return
     */
    public String getGradedStudentQuizzes() {
        if (studentId == 0) {
            return null;
        }
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
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return ret.replaceAll("\"","'");
    }

    /**
     * Returns list of quizzes as a JSON string of the entire class
     * @return
     */
    public String getGradedWholeClassQuizzes() {

        if(studentId==-100) {
            TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
            String quizzes = null;
            try {
                quizzes = tdbc.getWholeClassQuizzes(this.courseId, false);
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
        return null;
    }

    /**
     * Returns list of quizzes as ArrayList of entire class
     * @return
     */
    public ArrayList<Quiz> getGradedWholeClassQuizzesArrayList() {
        if(studentId==-100) {
            TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
            ArrayList<Quiz> quizzes = null;
            try {
                quizzes = tdbc.getWholeClassQuizzesArrayList(this.courseId);
            } catch (SQLException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
            tdbc.closeConnection();
            return quizzes;
        }
        return null;
    }

    /**
     * Returns a list of quizzes along with a histogram of student performance
     * as a JSON string
     * @return
     */
    public String getHistogramQuizzes() {
        if(studentId==-100) {
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
            if(quizzes == null){
                return null;
            }
            return quizzes.replaceAll("\"","'");
        }
        return null;
    }

    /**
     * Returns all quizzes that still need to be graded for a specific student.
     * @return
     */
    public ArrayList<Quiz> getUngradedStudentQuizzes() {
        if (studentId == 0 ) {
            return null;
        }
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        ArrayList<Quiz> quizzes = null;
        try {
            quizzes = tdbc.getUngradedStudentQuizzes(this.studentId, this.courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        return quizzes;
    }

    /**
     * Returns a JSON string of how long a student has been on each page of a
     * textbook.
     * @return
     */
    public String getTimeMetrics() {
        if (studentId == 0 ) {
            return null;
        }
        String ret = "";
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        ArrayList<ChapterMetrics> chapMets = null;
        try {
            chapMets = tdbc.getStudentTimeMetrics(this.studentId, this.courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            ret = (new JsonConverter()).convertChapterMetricsmArrayListToJson(chapMets);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        tdbc.closeConnection();

        return ret.replaceAll("\"","'");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

}
