package com.mastermycourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mastermycourse.beans.CourseBean;
import com.mastermycourse.beans.StudentCourseBean;
import com.mastermycourse.beans.StudentMetricsBean;
import com.mastermycourse.database.StudentDBCommands;
import com.mastermycourse.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Authors: James DeCarlo, Zach Lerman, Rahul Verma, and Jose Rodriguez.
 *
 * This page contains the various controls for a students interactions with the website.  This includes joining a course,
 * redirecting to a selected course, joining a chat, etc.
 */
@Controller
public class StudentController {
    @Autowired
    ApplicationContext ctx;

    /**
     * This method controls the ability of a student to join a public course and redirects the student to the course.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/JoinCourse", method = RequestMethod.POST)
    public void joinCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean studentCourseBean = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        String email = request.getParameter("email");
        String courseId = request.getParameter("courseId");

        if(studentCourseBean != null && email != null && courseId != null) {

            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            sdbc.joinCourse(email, Integer.parseInt(courseId));
            sdbc.closeConnection();
            studentCourseBean.setCourseId(Integer.parseInt(courseId));
            studentCourseBean.setContentModuleId(-1);
            response.sendRedirect("course.htm");

        } else {
            response.sendRedirect("joinCourse.htm");
        }
    }

    /**
     * Redirects to the course page.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/redirectToCourse", method = RequestMethod.POST)
    public void redirectToCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.sendRedirect("course.htm");
    }

    /**
     * This method controls the ability of a student to join a private course and redirects the student to the course.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/JoinPrivateCourse", method = RequestMethod.POST)
    public void joinPrivateCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean studentCourseBean = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String email = studentCourseBean.getEmail();

        int courseCode = Integer.valueOf(request.getParameter("classCode"));
        int courseId = sdbc.validateCourseCode(courseCode);

        if(courseId > 0 && studentCourseBean != null && email != null){
            sdbc.joinCourse(email, courseId);
            sdbc.closeConnection();
            studentCourseBean.setCourseId(courseId);
            studentCourseBean.setContentModuleId(-1);
            response.sendRedirect("course.htm");

        } else {
            response.sendRedirect("joinCourse.htm");
        }

    }

    /**
     * This method is used to allow a student to change from one course to another.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/StudentChangeCourse", method = RequestMethod.POST)
    public void changeCourse(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean course = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        course.setCourseId(Integer.parseInt(request.getParameter("courseId")));
        course.setContentModuleId(-1);
        response.sendRedirect("/course.htm");
    }
    /**
     * Controls the students pdf page movement and brings the student to the requested page.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/StudentChangePage", method = RequestMethod.POST)
    public void changePage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean course = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        String contentModuleId = request.getParameter("contentModuleId");
        if(course != null && contentModuleId != null){
            course.setContentModuleId(Integer.parseInt(contentModuleId));
        }
        response.sendRedirect("/course.htm");
    }

    /**
     * Controls the students pdf page movement and brings the student to the next pdf page.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/StudentNextPage", method = RequestMethod.POST)
    public void nextPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean courseBean = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        if(courseBean != null){
            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            int nextContentModuleId = sdbc.getNextCourseModuleId(courseBean.getCourseId(), courseBean.getContentModuleId());
            sdbc.closeConnection();
            courseBean.setContentModuleId(nextContentModuleId);
        }
        response.sendRedirect("course.htm");
    }

    /**
     * Controls the students pdf page movement and brings the student to the previous pdf page.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/StudentPreviousPage", method = RequestMethod.POST)
    public void previousPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean courseBean = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        if(courseBean != null){
            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            int previousContentModuleId = sdbc.getPreviousCourseModuleId(courseBean.getCourseId(), courseBean.getContentModuleId());
            sdbc.closeConnection();
            courseBean.setContentModuleId(previousContentModuleId);
        }
        response.sendRedirect("course.htm");
    }

    /**
     * This method redirects a student to a course if they are a TA.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/TACourse", method = RequestMethod.POST)
    public void Course(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        // set the teachers course bean as the student will now have teacher level access for grading that course
        CourseBean course = (CourseBean)request.getSession().getAttribute("course");
        course.setCourseId(Integer.parseInt(request.getParameter("courseId")));

        response.sendRedirect("/teachertool.htm");
    }

    /**
     * This method initiates the student metrics bean for the session that is active.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/SetStudentMetrics", method = RequestMethod.POST)
    public void setStudentMetrics(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {

        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String email = request.getParameter("email");
        StudentMetricsBean smb = (StudentMetricsBean)request.getSession().getAttribute("studentMetrics");
        StudentCourseBean scb = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        User u = sdbc.getUserByEmail(email);
        smb.setStudentId(u.getId());
        smb.setCourseId(scb.getCourseId());
        response.sendRedirect("/studenttool.htm");
    }



    /**
     * Adds note to the database
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/AddNote", method = RequestMethod.POST)
    public void addNote(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean studentCourseBean = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        String title = request.getParameter("titleNote");
        String note = request.getParameter("noteText");

        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        User u = sdbc.getUserByEmail(studentCourseBean.getEmail());

        /*Add to database*/
        sdbc.addNote(u.getId(), studentCourseBean.getCourseId(), title ,note);
        sdbc.closeConnection();
        response.sendRedirect("/course.htm");
    }

    /**
     * Removes note to the database
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/DeleteNote", method = RequestMethod.POST)
    public void removeNote(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentCourseBean studentCourseBean = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
        String title = request.getParameter("title");
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        User u = sdbc.getUserByEmail(studentCourseBean.getEmail());

        /*Add to database*/
        sdbc.deleteNote(studentCourseBean.getCourseId(), u.getId(), title);
        sdbc.closeConnection();
        response.sendRedirect("/course.htm");
    }

    /**
     * This method updates the time a student has spend on a particular page of a pdf they are viewing.
     * @param request
     * @param response
     * @throws IOException
     */

    @RequestMapping(value = "/UpdateStudentTimeOnPage", method = RequestMethod.POST)
    public void updateStudentTimeOnPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String errorStatus = null;

        try{
            StudentCourseBean courseBean = (StudentCourseBean)request.getSession().getAttribute("studentCourse");
            int seconds = Integer.parseInt(request.getParameter("seconds"));
            String email = courseBean.getEmail();
            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            User user = sdbc.getUserByEmail(email);
            sdbc.insertUpdateStudentTimeOnPage(user.getId(), courseBean.getContentModuleId(), seconds);
            sdbc.closeConnection();

        } catch (Exception e){
            errorStatus = e.getMessage();
        }


        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        if(errorStatus == null){
            objectNode.put("status", "success");
        } else {
            objectNode.put("status", errorStatus);
        }
        PrintWriter out = response.getWriter();
        out.write(objectNode.toString());
    }

    /**
     * Edits a note given a new string
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/EditNote", method = RequestMethod.POST)
    public void editNote(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int userId = Integer.parseInt(request.getParameter("userId"));
        String title = request.getParameter("title");
        String text = request.getParameter("noteText");

        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        sdbc.updateNote(userId, courseId, title, text);
        sdbc.closeConnection();
        response.sendRedirect("/course.htm");
    }

    /**
     * Validates the note title on blur to make sure it does not use a same title already in use
     * @param request
     * @param response
     */
    @RequestMapping(value = "/ValidateNoteTitle", method = RequestMethod.POST)
    public void validateNoteTitle(HttpServletRequest request, HttpServletResponse response){
        String title = request.getParameter("title");
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int userId = Integer.parseInt(request.getParameter("userId"));
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        try {
            PrintWriter out = response.getWriter();
            if (title == null || title.length() < 1) {
                out.print("False");
                return;
            }

            if(sdbc.checkNoteTitleExists(userId, courseId, title)){
                out.print("False");
            }else{
                out.print("True");
            }

        }catch (IOException e){

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
