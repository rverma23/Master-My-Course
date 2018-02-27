package com.mastermycourse.beans;

import com.mastermycourse.database.AdminDBCommands;
import com.mastermycourse.pojos.Teacher;
import com.mastermycourse.pojos.TeacherCourse;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: James DeCarlo.
 */

/**
 * Session Scoped Bean for the Administrator Pages not available to teachers or students to keep secure
 * implements Serializable and ApplicationContextAware
 */
public class AdminPortalBean implements Serializable, ApplicationContextAware{
    private ApplicationContext ctx;
    private int teacherId = -1;
    private String teacherName;
    private Logger logger = Logger.getLogger(AdminPortalBean.class.getName());
    /**
     * List all teachers that are Pending Admission or Disabled.
     *
     * @return List Collection of Teacher Pojo.
     *
     */
    public List<Teacher> getPendingTeachers(){
        AdminDBCommands adb = new AdminDBCommands(ctx);
        List<Teacher> pendingTeachers = null;
        try {
            pendingTeachers = adb.getPendingTeachers();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            adb.closeConnection();
            return pendingTeachers;
        }
    }

    /**
     * List all teachers that are approved.
     *
     * @return List
     */
    public List<Teacher> getApprovedTeachers() {
        AdminDBCommands adb = new AdminDBCommands(ctx);
        try {
            List<Teacher> approvedTeachers = adb.getApprovedTeachers();
            return approvedTeachers;
        } catch (SQLException e){
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        } finally {
            adb.closeConnection();
        }


    }

    /**
     * Teacher Id must be set by the servlet before returning to this page when teacher is selected in order for it
     * getTeacherCourses to list the appropriate teacher. Set to -1 for all teacher courses to be displayed
     * @param teacherId The Id of the teacher from the id field in Users database or -1
     */
    public void setTeacherId(int teacherId){
        this.teacherId = teacherId;
    }


    /**
     * List of all Teacher Courses if teacherId is set to -1 or or the courses for the teacher that the id is set for.
     * @return List Collection of TeacherCourse
     */
    public List<TeacherCourse> getTeacherCourses() {
        if(teacherId == -1){
            teacherName = "All Teachers";
            AdminDBCommands adbc = new AdminDBCommands(ctx);
            List<TeacherCourse> teacherCourses = null;
            try {
                teacherCourses = adbc.getAllCourses();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            } finally {
                adbc.closeConnection();
                return teacherCourses;
            }
        }

        AdminDBCommands adbc = new AdminDBCommands(ctx);
        List<TeacherCourse> teacherCourses = null;
        try {
            teacherCourses = adbc.getTeacherCourses(teacherId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        adbc.closeConnection();
        if(teacherCourses != null && teacherCourses.size()>0){
            teacherName = teacherCourses.get(0).getTeacherName();
        } else {
            teacherName = "Teacher has No Courses";
        }

        return teacherCourses;
    }

    /**
     * Get the teachers name.
     * @return teachers name
     */
    public String getTeacherName() {
        return teacherName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
