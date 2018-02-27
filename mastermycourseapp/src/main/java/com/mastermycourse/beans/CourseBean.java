package com.mastermycourse.beans;

import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.pojos.Note;
import com.mastermycourse.pojos.Outline;
import com.mastermycourse.pojos.Student;
import com.mastermycourse.pojos.TA;
import com.mastermycourse.settings.Prices;
import org.springframework.beans.BeansException;
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
 * Author: James DeCarlo, Jose Rodriguez, Zach Lerman.
 *
 * Session Scoped Bean used for teacher course control on teacher pages
 * implements Serializable and ApplicationContextAware
 */
public class CourseBean implements Serializable, ApplicationContextAware {
    private int courseId = -1;
    private ApplicationContext ctx;
    private String email;
    private int contentModuleId = -1;
    private Logger log = Logger.getLogger(CourseBean.class.getName());
    private Note currentNote;

    /**
     * Gets a deep copy of this bean
     * @return CourseBean
     */
    public CourseBean copy(){
        CourseBean courseBean = new CourseBean();
        courseBean.setCourseId(this.courseId);
        courseBean.setApplicationContext(this.ctx);
        courseBean.setEmail(this.email);
        courseBean.setContentModuleId(this.contentModuleId);
        return courseBean;
    }

    /**
     * Returns the course code if the course is private
     * 0 otherwise
     * @return the course code or 0 if the course is private
     */
    public int getCourseCode() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        try {
            int courseCode = tdbc.getCourseCode(courseId);
            return courseCode;
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * This must be set for other methods in this class depend on
     * @param email The logged in users email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the course id for the current course if hasn't been set fetches the initial course to display from the
     * database. Email must be set for this method to work.
     * @return The Current Course Id
     * @throws SQLException
     */
    public int getCourseId() throws SQLException {

        if(courseId == -1){
            TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
            courseId = tdbc.getInitialCourseId(email);
            tdbc.closeConnection();
        }
        return courseId;
    }

    /**
     * Gets the current text to speech pricing from the Prices class.
     * @return current text to speech price.
     */
    public double getTextToSpeechPrice(){
        return Prices.textToSpeech;
    }

    /**
     * Gets the current text to speech pricing from the Prices class as formatted String.
     * @return current text to speech formatted string price.
     */
    public String getTextToSpeechPriceString(){
        return String.format("%.2f", Prices.textToSpeech);
    }

    /**
     * Gets the current course name course id would hat to have to have been set.
     * @return Current course name or null if error
     */
    public String getCourseName() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        String name = null;
        try {
            name = tdbc.getCourseName(courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        return name;
    }

    /**
     * Gets the list of courses of the teacher. The current logged in user email needs to be set in this bean.
     * @return Map of current teacher courses teacher courses if error returns null
     */
    public Map<String,Integer> getCourses() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        Map<String, Integer> map = null;
        try {
            map = tdbc.getCourses(email);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        return map;
    }

    /**
     * Sets the current course id. This or getCourseId must be called for this bean to work correctly other methods
     * depend on it.
     * @param courseId the current course id
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * Gets the current content module id referring to the current page.
     * @return the current content module id
     */
    public int getContentModuleId() {
        return contentModuleId;
    }

    /**
     * Set the current content module id.
     * @param contentModuleId the current content module id
     */
    public void setContentModuleId(int contentModuleId) {
        this.contentModuleId = contentModuleId;
    }

    /**
     * Get the current course outline from the database
     * @return List collection of a List Collection of Outlines or null if error
     */
    public List<List<Outline>> getOutline(){
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        List<Outline> outlines = null;
        try {
            outlines = tdbc.getCourseOutline(courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        if(outlines == null || outlines.size() == 0){
            return null;
        }

        List<List<Outline>> retList = new ArrayList<>();
        String chapterTitle = null;
        List<Outline> outlineChapter = new ArrayList<>();
        for(Outline outline: outlines){
            if(chapterTitle == null || !chapterTitle.equals(outline.getChapterTitle())){
                chapterTitle = outline.getChapterTitle();
                outlineChapter = new ArrayList<>();
                retList.add(outlineChapter);
            }
            outlineChapter.add(outline);
        }

        return retList;
    }


    /**
     * Gets the current pages image from database or returns null if error
     * @return the current page image as base 64 string or null if error
     */
    public String getPageImage() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        byte[] body = new byte[0];
        try {
            body = tdbc.getContentModuleHtml(contentModuleId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        if(body == null){
            body = new byte[0];
        }
        tdbc.closeConnection();
        return DatatypeConverter.printBase64Binary(body);
    }

    /**
     * Gets a list of students within a particular class
     * @return list of students in the class selected by the professor
     */
    public List<Student> getStudents() {
        TeacherDBCommands adb = new TeacherDBCommands(ctx);
        List<Student> students = null;
        try {
            students = adb.getStudentList(this.courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        adb.closeConnection();
        return students;
    }

    /**
     * Get TA's for current course current course id would have to be set or get course id would have to be called
     * before you use this method.
     * @return List Collection of TA's
     */
    public List<TA> getTAs() {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        List<TA> taList = null;
        try {
            taList = tdbc.getTAs(this.courseId);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        tdbc.closeConnection();
        return taList;
    }

    public boolean isEnabled() throws SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        boolean isEnabled = tdbc.isCourseEnabled(courseId);
        tdbc.closeConnection();
        return isEnabled;
    }


    /**
     * Application Context
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }


}
