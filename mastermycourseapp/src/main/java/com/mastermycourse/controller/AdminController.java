package com.mastermycourse.controller;

import com.mastermycourse.email.Email;
import com.mastermycourse.beans.AdminPortalBean;
import com.mastermycourse.database.AdminDBCommands;
import com.mastermycourse.pojos.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Authors: James DeCarlo, Zach Lerman
 *
 * This class contains controllers for admin actions. These actions include approving teahcers, disable courses,
 * delete courses, enable courses, list all courses, etc.
 *
 */

@Controller
public class AdminController {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    JavaMailSender mailSender;

    /**
     * This method handles the requests by an admin to delete or approve a teacher and redirects to the administration page.
     * @param request contains the actions which are Delete or Approve. Also contains the user emails.
     * @param response is used to redirect back to the administration page
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/AdminApproveDeleteTeachers", method = RequestMethod.POST)
    public void approveDeleteTeachers(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        if(request.getParameter("action").equals("Delete")){
            String[] emails = request.getParameterValues("userEmails");
            if(emails != null) {
                AdminDBCommands adb = new AdminDBCommands(ctx);
                for (String email : emails) {
                    adb.deleteTeacher(email);
                    Email.sendDeleteTeacherEmail((JavaMailSenderImpl) mailSender, email);
                }
                adb.closeConnection();
            }
        } else if(request.getParameter("action").equals("Approve")){
            String[] emails = request.getParameterValues("userEmails");
            if (emails != null) {
                AdminDBCommands adb = new AdminDBCommands(ctx);
                for (String email : emails) {
                    adb.approveTeacher(email);
                    Email.sendTeacherApprovalEmail((JavaMailSenderImpl)mailSender, email);
                }
                adb.closeConnection();
            }
        }

        response.sendRedirect("/administration.htm");
    }

    /**
     * This method is used to manage the request by the admin to disable teacher(s) and redirects to the administration page.
     * @param request contains the user emails to be disabled.
     * @param response is used to redirect the admin back to the administration.htm when everything is done.
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/AdminDisableTeachers", method = RequestMethod.POST)
    public void disableTeachers(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String[] emails = request.getParameterValues("userEmails");
        if(emails != null){
            AdminDBCommands adb = new AdminDBCommands(ctx);
            for(String email: emails){
                adb.disableTeacher(email);
                Email.sendDisabledTeacherEmail((JavaMailSenderImpl)mailSender, email);
            }
            adb.closeConnection();
        }
        response.sendRedirect("/administration.htm");
    }

    /**
     * This method is used to manage the admin request to list all courses and redirects to the administration page.
     * @param request contains the admin portal bean object
     * @param response is used to redirect the admin back to the administration.htm
     * @throws IOException
     */
    @RequestMapping(value = "/ListAllCourses", method = RequestMethod.POST)
    public void listAllCourses(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AdminPortalBean adminPortalBean = (AdminPortalBean) request.getSession().getAttribute("admin");
        if(adminPortalBean != null){
            adminPortalBean.setTeacherId(-1);
        }
        response.sendRedirect("administration.htm");
    }

    /**
     * This method manages the admin request of listing all courses by teacher and redirects to the administration page.
     * @param request contains the teacher's id for whom we are listing the courses.
     * @param response is used to redirect the admin to the administration.htm
     * @throws IOException
     */
    @RequestMapping(value = "/ListTeacherCourses", method = RequestMethod.POST)
    public void listTeacherCourses(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AdminPortalBean adminPortalBean = (AdminPortalBean) request.getSession().getAttribute("admin");
        String teacherId = request.getParameter("teacherId");

        if(adminPortalBean != null && teacherId != null){
            adminPortalBean.setTeacherId(Integer.parseInt(request.getParameter("teacherId")));
        }
        response.sendRedirect("administration.htm");

    }

    /**
     * This method is used to manage the disabling of a course and redirects to the administration page.
     * @param request contains the course id and the course name of the course we are disabling
     * @param response is used to redirect the admin to the administration.htm
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/DisableTeacherCourse", method = RequestMethod.POST)
    public void disableTeacherCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String courseId = request.getParameter("courseId");
        String courseName = request.getParameter("courseName");
        if(courseId != null && courseName != null){
            AdminDBCommands adbc = new AdminDBCommands(ctx);
            adbc.disableCourse(Integer.parseInt(courseId));
            Teacher teacher = adbc.getTeacherByCourseId(Integer.parseInt(courseId));
            adbc.closeConnection();

            Email.sendCourseDisabledEmail((JavaMailSenderImpl) mailSender, teacher.getEmail(), teacher.getName(), courseName);
        }


        response.sendRedirect("administration.htm");
    }

    /**
     * This method is used to manage the enabling of a teacher course and redirects to the administration page.
     * @param request contains the course id and course name of the course we are enabling
     * @param response is used to redirect the admin to the administration.htm
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/EnableTeacherCourse", method = RequestMethod.POST)
    public void enableTeacherCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String courseId = request.getParameter("courseId");
        String courseName = request.getParameter("courseName");
        if(courseId != null && courseName != null){
            AdminDBCommands adbc = new AdminDBCommands(ctx);
            adbc.enableCourse(Integer.parseInt(courseId));
            Teacher teacher = adbc.getTeacherByCourseId(Integer.parseInt(courseId));
            int courseCode = adbc.getCourseCode(Integer.parseInt(courseId));
            adbc.closeConnection();

            Email.sendCourseEnabledEmail((JavaMailSenderImpl) mailSender, teacher.getEmail(), teacher.getName(), courseName, courseCode);
        }


        response.sendRedirect("administration.htm");
    }

    /**
     * This method is used to manage the deleting of a course and redirects to the administration page.
     * @param request contains the course id and course name of the course we are deleting.
     * @param response is used to redirect the admin to the administration.htm
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/DeleteTeacherCourse", method = RequestMethod.POST)
    public void deleteTeacherCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String courseId = request.getParameter("courseId");
        String courseName = request.getParameter("courseName");
        if(courseId != null && courseName != null){
            AdminDBCommands adbc = new AdminDBCommands(ctx);
            Teacher teacher = adbc.getTeacherByCourseId(Integer.parseInt(courseId));
            adbc.deleteCourse(Integer.parseInt(courseId));
            adbc.closeConnection();

            Email.sendCourseDeletedEmail((JavaMailSenderImpl) mailSender, teacher.getEmail(), teacher.getName(), courseName);
        }


        response.sendRedirect("administration.htm");
    }
}
