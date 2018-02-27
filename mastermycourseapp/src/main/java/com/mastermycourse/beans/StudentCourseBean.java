package com.mastermycourse.beans;

import com.mastermycourse.database.StudentDBCommands;
import com.mastermycourse.pojos.Note;
import com.mastermycourse.pojos.Outline;
import com.mastermycourse.pojos.TeacherCourse;
import com.mastermycourse.pojos.User;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authors: James DeCarlo and Zach Lerman.
 * .
 * Session scoped bean for the student course control email application context and course id must be set for this
 * to work properly. Set content module id to change the page the course is on.
 * Implements Serializable and ApplicationContextAware.
 */
public class StudentCourseBean implements Serializable, ApplicationContextAware{
    private int courseId = -1;
    private ApplicationContext ctx;
    private String email;
    private int contentModuleId = -1;
    private Logger log = Logger.getLogger(StudentCourseBean.class.getName());

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCourseId() {

        if(courseId == -1){
            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            try {
                courseId = sdbc.getInitialCourseId(email);
            } catch (SQLException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
            sdbc.closeConnection();
            setContentModuleId(-1);
        }
        return courseId;
    }

    public String getCourseName() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String name = null;
        try {
            name = sdbc.getCourseName(courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        sdbc.closeConnection();
        return name;
    }

    /**
     * Get all courses a student is taking based on their email
     * @return
     */
    public Map<String,Integer> getCourses() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        Map<String, Integer> map = null;
        try {
            map = sdbc.getCourses(email);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        sdbc.closeConnection();
        return map;
    }

    /**
     * Checks if the user is a TA
     * @return 1 if they are a ta, -1 if not
     */
    public int isTA() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        int isTa = 0;
        try {
            isTa = sdbc.isTA(email);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        sdbc.closeConnection();
        return isTa;
    }

    /**
     * Get all courses this student is a TA in
     * @return
     */
    public Map<String, Integer> getTACourses() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        Map<String, Integer> map = null;
        try {
            map = sdbc.getTACourses(email);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        sdbc.closeConnection();
        return map;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getContentModuleId() {
        return contentModuleId;
    }

    public void setContentModuleId(int contentModuleId) {
        if(contentModuleId == -1) {
            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            try {
                User user = sdbc.getUserByEmail(email);
                this.contentModuleId = sdbc.getInitialCourseContentModuleId(user.getId(), courseId);
            } catch (SQLException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
            sdbc.closeConnection();
            return;
        }
        this.contentModuleId = contentModuleId;
    }

    /**
     * Gets the outline of a PDF so that a student may read it
     * @return
     */
    public List<List<Outline>> getOutline() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        List<Outline> outlines = null;
        try {
            outlines = sdbc.getCourseOutline(courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        sdbc.closeConnection();
        if(outlines == null || outlines.size() == 0){
            return null;
        }

        List<List<Outline>> retList = new ArrayList<>();
        String chapterTitle = null;
        List<Outline> outlineChapter = new ArrayList<>();
        for(Outline outline: outlines) {
            if(chapterTitle == null || !chapterTitle.equals(outline.getChapterTitle())) {
                chapterTitle = outline.getChapterTitle();
                outlineChapter = new ArrayList<>();
                retList.add(outlineChapter);
            }
            outlineChapter.add(outline);
        }

        return retList;
    }

    /**
     * Returns a pdf page as an image
     * @return
     */
    public String getPageImage() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        byte[] body = new byte[0];
        try {
            body = sdbc.getContentModuleHtml(contentModuleId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        sdbc.closeConnection();
        if(body == null){
            body = new byte[0];
        }
        return DatatypeConverter.printBase64Binary(body);
    }

    /**
     * Gets a list of all courses a student is still eligible to join
     * @return
     */
    public List<TeacherCourse> getTeacherCourses() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        List<TeacherCourse> teacherCourses = null;
        try {
            teacherCourses = sdbc.getAllCoursesNotAlreadyIn(email);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        sdbc.closeConnection();
        return teacherCourses;
    }

    // checks if student already took the quiz
    public int tookQuiz(int quizId) {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        int tookQuiz = 0;
        try {
            tookQuiz = sdbc.tookQuiz(quizId, this.email);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return tookQuiz;
    }

    /**
     * Checks if a quiz is past it's due date and thus a student can no longer take it,
     * Will also submit the grade as a 0 if the student did not take it in time
     * @param quizId The quiz to check if past due date
     * @return 1 if past due date, 0 otherwise
     */
    public int pastDueDate(int quizId, int tookQuiz) {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);

        if (tookQuiz == 1) {  // they already took the quiz, cannot be past its due date
            return 0;
        }

        // else they did not take it yet
        try {
            int pastDueDate = sdbc.checkDueDate(quizId);
            if (pastDueDate == 1) {
                return 1;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    /**
     * Returns a page of a PDF (which is normally an image) as raw text
     * that has also been escaped
     * @return
     */
    public String getRawTextEscapedNewLineDoubleQuote(){
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String rawText;
        try {
             rawText = sdbc.getContentRawText(contentModuleId);
        } catch (SQLException e) {
            rawText = "";
        } finally {
            sdbc.closeConnection();
        }
        return StringEscapeUtils.escapeEcmaScript(rawText);
    }

    /**
     * Returns the raw text of pdf page as html
     * @return
     */
    public String getRawTextAsHtml(){
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String rawText;
        try {
            rawText = sdbc.getContentRawText(contentModuleId);
        } catch (SQLException e) {
            rawText = "";
        } finally {
            sdbc.closeConnection();
        }
        rawText = rawText.replace("\n", "<br>");
        rawText = rawText.replace("\"", "\\\"");
        return rawText;
    }

    public int getUserId(){
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        try {
            User user = sdbc.getUserByEmail(email);
            return user.getId();
        } catch (SQLException e) {
            return 0;
        }

    }

    public boolean isEnabled() throws SQLException {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        boolean isEnabled = sdbc.isCourseEnabled(courseId);
        sdbc.closeConnection();
        return isEnabled;
    }

    /**
     * Gets the notes from the database
     * @return
     * @throws SQLException
     */
    public List<Note> getNotes() throws SQLException{
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        List<Note> notesList = null;
        try{
            User user = sdbc.getUserByEmail(email);
            notesList = sdbc.getNotes(this.courseId, user.getId());

        }catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        sdbc.closeConnection();
        return notesList;
    }
}
