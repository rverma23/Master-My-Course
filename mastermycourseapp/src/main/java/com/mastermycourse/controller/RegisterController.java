package com.mastermycourse.controller;

import com.mastermycourse.email.Email;
import com.mastermycourse.database.StudentDBCommands;
import com.mastermycourse.database.TeacherDBCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * Author: Zach Lerman and James DeCarlo.
 *
 * This class provides method for handling the registration of a user. The types of users that can register are
 * teachers and students.  We provide methods for both of these to handle the registration.
 */

@Controller
public class RegisterController {
    @Autowired
    ApplicationContext ctx;

    @Autowired
    JavaMailSender mailSender;

    /**
     * This method handles registration of a student. It redirects the user to the homepage or coursepage after registration
     * @param request contains the credentials of the student user
     * @param response is used to redirect after the registration is done.
     * @throws Exception
     */
    @RequestMapping(value = "/StudentRegistration", method = RequestMethod.POST)
    public void studentRegistration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getSession().getAttribute("userEmail").toString();
        String name = request.getSession().getAttribute("userName").toString();
        String imageUrl = request.getSession().getAttribute("userImage").toString();

        try {
            StudentDBCommands dbCommands = new StudentDBCommands(ctx);
            boolean insertResult = dbCommands.registerStudent(email, name, imageUrl);
            dbCommands.closeConnection();

            if (insertResult) {
                // send welcome email for student
                Email.sendStudentWelcomeEmail((JavaMailSenderImpl) mailSender, email, name);
                response.sendRedirect("course.htm");
            } else {
                response.sendRedirect("register.htm");
            }
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
            // should remove and redirect to login

        } catch (IllegalArgumentException ex) {
            response.sendRedirect("/");
        }
    }

    /**
     * This method handles registration of the teacher. It redirects the user to the appropriate
     * page after registration is complete.  The user can be redirected to a pending page, homepage or course page depending
     * on how the registration went. If this is a new teacher, the teacher must wait for verification and goes the the pending
     * page.
     * @param request contains the users credentials for registration
     * @param response is used to redirect the user to the appropriate page.
     * @throws Exception
     */
    @RequestMapping(value = "/TeacherRegistration", method = RequestMethod.POST)
    public void teacherRegistration(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getSession().getAttribute("userEmail").toString();
        String name = request.getSession().getAttribute("userName").toString();
        String imageUrl = request.getSession().getAttribute("userImage").toString();
        String schoolName = request.getParameter("schoolName").toString();
        String description = request.getParameter("description").toString();

        try {
            TeacherDBCommands dbCommands = new TeacherDBCommands(ctx);
            boolean insertResult = dbCommands.registerTeacher(email, name, imageUrl, schoolName, description);
            dbCommands.closeConnection();

            if (insertResult) {
                // send welcome email for student
                Email.sendTeacherWelcomeEmail((JavaMailSenderImpl) mailSender, email, name);
                response.sendRedirect("/pending.htm");

            } else {
                response.sendRedirect("/register.htm");
            }
        } catch (SQLException e) {
            throw new Exception(e.getMessage());

        } catch (IllegalArgumentException ex) {
            response.sendRedirect("/");
        }  catch(Exception e) {
            response.sendRedirect("/");
        }
    }
}