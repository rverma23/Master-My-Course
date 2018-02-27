package com.mastermycourse.controller;

import com.mastermycourse.beans.CourseBean;
import com.mastermycourse.beans.MessageBean;
import com.mastermycourse.beans.StudentAnswerBean;
import com.mastermycourse.beans.TeacherMetricsBean;
import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.email.Email;
import com.mastermycourse.pojos.Question;
import com.mastermycourse.pojos.StudentAnswer;
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
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Authors: James DeCarlo, Zach Lerman, Rahul Verma, and Jose Rodriguez.
 *
 * This class contains various controls for the teacher.  These controls include creating a new course, seeing metrics,
 * grading quizzes, etc.
 */
@Controller
public class TeacherController {
    @Autowired
    ApplicationContext ctx;

    @Autowired
    JavaMailSender mailSender;

    /**
     * Method handles creating a new course and redirected the teacher to the new course creation page
     * @param request
     * @param response
     * @throws SQLException
     * @throws IOException
     */
    @RequestMapping(value="/CreateNewCourse", method = RequestMethod.POST)
    public void newCourse(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String email = request.getSession().getAttribute("userEmail").toString();
        String courseName = request.getParameter("name");
        String courseDescription = request.getParameter("description");
        boolean isPublic = request.getParameter("isPublic") != null;

        if(email == null || courseName == null || courseDescription == null){
            throw new IllegalArgumentException("All Fields Required");
        }

        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);

        int courseCode = 0; // 0 indicates no course code, for public courses
        if (!isPublic) {
            // generate 5-digit random course code
            courseCode = ThreadLocalRandom.current().nextInt(10000, 100000);
        }

        int courseId = tdbc.createNewCourse(email, courseName, courseDescription, isPublic, courseCode);
        tdbc.closeConnection();

        if(courseId == -1){
            response.sendRedirect("/newCourse.htm");
        } else {
            CourseBean course = (CourseBean)request.getSession().getAttribute("course");
            course.setCourseId(courseId);
            response.sendRedirect("/courseCreation.htm");
        }
    }

    /**
     * Lets the teacher navigate to other courses that the teacher teaches.
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/ChangeCourse", method = RequestMethod.POST)
    public void changeCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CourseBean course = (CourseBean)request.getSession().getAttribute("course");
        course.setCourseId(Integer.parseInt(request.getParameter("courseId")));

        response.sendRedirect("/courseCreation.htm");
    }

    /**
     * Allows the teacher to change the page it is viewing in the pdf.
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/ChangePage", method = RequestMethod.POST)
    public void changePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CourseBean course = (CourseBean)request.getSession().getAttribute("course");
        String contentModuleId = request.getParameter("contentModuleId");
        if(course != null && contentModuleId != null){
            course.setContentModuleId(Integer.parseInt(contentModuleId));
        }
        response.sendRedirect("/coursePreview.htm");
    }

    /**
     * Allows the teacher to move to the next page in the pdf.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/NextPage", method = RequestMethod.POST)
    public void nextPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        CourseBean courseBean = (CourseBean)request.getSession().getAttribute("course");
        if(courseBean != null){
            TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
            int nextContentModuleId = tdbc.getNextCourseModuleId(courseBean.getCourseId(), courseBean.getContentModuleId());
            tdbc.closeConnection();
            courseBean.setContentModuleId(nextContentModuleId);
        }
        response.sendRedirect("coursePreview.htm");
    }

    /**
     * Allows the teacher to move to the previous page in the pdf.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/PreviousPage", method = RequestMethod.POST)
    public void previousPage(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        CourseBean courseBean = (CourseBean)request.getSession().getAttribute("course");
        if(courseBean != null){
            TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
            int previousContentModuleId = tdbc.getPreviousCourseModuleId(courseBean.getCourseId(), courseBean.getContentModuleId());
            tdbc.closeConnection();
            courseBean.setContentModuleId(previousContentModuleId);
        }
        response.sendRedirect("coursePreview.htm");
    }

    /**
     * Allows a teacher to select a student whose metrics they want to view and redirects to the metrics page
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/SelectStudent", method = RequestMethod.POST)
    public void selectStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        int studentId = Integer.valueOf(request.getParameter("studentId"));
        CourseBean courseBean = (CourseBean)request.getSession().getAttribute("course");
        TeacherMetricsBean teacherMetricsBean = (TeacherMetricsBean)request.getSession().getAttribute("teacherMetrics");
        teacherMetricsBean.setStudentId(studentId);
        teacherMetricsBean.setCourseId(courseBean.getCourseId());
        response.sendRedirect("/teachertool.htm");
    }

    /**
     * Allows teacher to delete a course.
     * @param request
     * @param response
     * @throws SQLException
     * @throws IOException
     */
    @RequestMapping(value = "/TeacherDeleteCourse", method = RequestMethod.POST)
    public void teacherDeleteCourse(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        CourseBean courseBean = (CourseBean)request.getSession().getAttribute("course");
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        tdbc.deleteCourse(courseBean.getCourseId());
        response.sendRedirect("/courseDeletedMessage.htm");
    }

    /**
     * Redirects the teacher to the metrics page
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/GetStudentPage", method = RequestMethod.GET)
    public void registeredStudents(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.sendRedirect("/teachertool.htm");
    }

    /**
     * Allows teacher to grade an oversight questions and redirects to the metrics page.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/GradeOversight", method = RequestMethod.POST)
    public void gradeOversight(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        StudentAnswerBean studentAnswerBean = (StudentAnswerBean)request.getSession().getAttribute("studentAnswers");
        int quizId = studentAnswerBean.getQuizId();
        int studentId = studentAnswerBean.getStudentId();
        ArrayList<StudentAnswer> answers = studentAnswerBean.getAnswers();

        for (int i = 0; i < answers.size(); i++) {
            Question question = answers.get(i).getQuestion();

            if (!question.getQuestionType().equals("oversight"))
                continue;

            String teacherNotesString = question.getQuestion() + "teacherNotes";
            String isCorrectString = question.getQuestion() + "radio";

            String teacherNotes = request.getParameter(teacherNotesString);
            isCorrectString = request.getParameter(isCorrectString);
            boolean isCorrect = isCorrectString.equals("true");

            tdbc.updateStudentAnswer(quizId, question.getId(), studentId, teacherNotes, isCorrect);
        }

        response.sendRedirect("/teachertool.htm");
    }

    /**
     * Removes the specified list of students from a course database
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/RemoveStudents", method = RequestMethod.POST)
    public void approveDeleteTeachers(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        System.out.println("Remove students triggered");
        if(request.getParameter("action").equals("Remove")){
            String[] studentNames = request.getParameterValues("studentName");
            if(studentNames != null) {
                TeacherDBCommands adb = new TeacherDBCommands(ctx);
                for (String studentId : studentNames) {
                    System.out.println("Student selected: "+ studentId);
                    adb.removeStudentFromCourse(Integer.parseInt(studentId));//remove student from course
                    //do other things to notify students
                }
                adb.closeConnection();
            }
        }
        response.sendRedirect("/teachertool.htm");
    }

    /**
     * Add's a TA to the list of course TA's based on teacher's input
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/AddTA", method = RequestMethod.POST)
    public void addTA(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        CourseBean course = (CourseBean)request.getSession().getAttribute("course");
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        String name = request.getParameter("first_name") + " " + request.getParameter("last_name");
        String email = request.getParameter("email");
        String message = tdbc.addTA(name, email, course.getCourseId());

        MessageBean messageBean = (MessageBean)request.getSession().getAttribute("messageBean");
        messageBean.setMessageBean(message);

        if (message.contains("green")) {
            // success, send the approval e-mail
            String courseName = tdbc.getCourseName(course.getCourseId());
            Email.sendAddTAEmail((JavaMailSenderImpl) mailSender, email, courseName);
        }
        tdbc.closeConnection();
        // return to same page
        response.sendRedirect(request.getHeader("referer"));
    }

    /**
     * Removes one or many TA from list of course TA's based on which
     * TA's teacher selected on front end form.
     * @param request
     * @param response
     * @throws IOException
     * @throws SQLException
     */
    @RequestMapping(value = "/RemoveTAs", method = RequestMethod.POST)
    public void removeTAs(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String[] emails = request.getParameterValues("taEmails");
        CourseBean course = (CourseBean)request.getSession().getAttribute("course");
        int courseId = course.getCourseId();

        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        if(emails != null){
            for(String email: emails){
                tdbc.removeTA(email);
            }
        }
        String courseName = tdbc.getCourseName(courseId);
        // Alert the TA's through e-mail that they've been removed
        for (int i = 0; i < emails.length; i++) {
            Email.sendRemoveTAEmail((JavaMailSenderImpl) mailSender, emails[i], courseName);
        }
        tdbc.closeConnection();

        response.sendRedirect(request.getHeader("referer"));
    }

}
