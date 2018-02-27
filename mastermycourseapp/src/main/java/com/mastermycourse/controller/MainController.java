package com.mastermycourse.controller;

import com.mastermycourse.database.StudentDBCommands;
import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;

/**
 * Authors: James DeCarlo, Zach Lerman, Rahul Verma and Jose Rodriguez.
 */
@Controller
public class MainController {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    JavaMailSender mailSender;

    /**
     * This method handles the event when a user enter the site.  If the session email is not null we present them the course page.
     * If session email is null we goto the homepage.
     * @param request contains the session email that we need to test to see if it is null.
     * @param response is used to redirect the user.
     * @return String containing 'index' to redirect valid email to their user homepage. Or the string contains 'register' to
     * redirect to user to the website homepage.
     * @throws IOException
     */
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String enter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getSession().getAttribute("userEmail") == null){
            // Create a state token to prevent request forgery.
            // Store it in the session for later validation.
            String state = new BigInteger(130, new SecureRandom()).toString(32);
            request.getSession().setAttribute("state", state);
            return "index";
        } else {
            response.sendRedirect("/register.htm");
            return "register";
        }
    }

    /**
     * This method manages verifying the login of users. Users are redirected to a page according to their status.
     * @param request contains the email of the user
     * @param response is used to redirect the user based on their status. A status of 2 and not approved results
     *                 in going to the pending page. A status of 2 and approved brings you to the course creation page.
     *                 A status of 3 brings you to the administration page.
     * @return
     * @throws Exception
     */
    @RequestMapping(value ="/register.htm", method = RequestMethod.GET)
    public String verifyLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = (String)request.getSession().getAttribute("userEmail");
        if(email == null) {
            response.sendRedirect("/");
            return "index";
        } else {
            StudentDBCommands sdbc = new StudentDBCommands(ctx);
            try {
                User user = sdbc.getUserByEmail(email);
                if(user != null){
                    if(user.getStatus() == 2){
                        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
                        if(!tdbc.isAccountApproved(email)){
                            tdbc.closeConnection();
                            response.sendRedirect("/pending.htm");
                            return "pending";
                        }
                        tdbc.closeConnection();
                    }
                    request.getSession().setAttribute("status", user.getStatus());
                } else {
                    sdbc.closeConnection();
                    return "/register";
                }

                if(user.getStatus() == 2){
                    sdbc.closeConnection();
                    response.sendRedirect("/courseCreation.htm");
                    return "/courseCreation";
                } else if (user.getStatus() == 3){
                    sdbc.closeConnection();
                    response.sendRedirect("/administration.htm");
                    return "/admin";
                } else {
                    // student
                    sdbc.closeConnection();
                    response.sendRedirect("/course.htm");
                    return "/course";
                }

            } catch (SQLException e) {
                throw new Exception(e.getMessage());
            } catch (Exception e){
                throw new Exception(e.getMessage());
            }
        }
    }


    @RequestMapping(value = "paymentError.htm")
    public String getPaymentError(){
        return "paymentError";
    }

    @RequestMapping(value = "paymentResponse")
    public String getPaymentResponse(){
        return "paymentResponse";
    }

    @RequestMapping(value ="/chat.htm", method = RequestMethod.GET)
    public String chat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getSession().getAttribute("userEmail") == null){
            response.sendRedirect("/");
            return "index";
        } else {
            return "chat";
        }
    }

    @RequestMapping(value ="/courseCreation.htm", method = RequestMethod.GET)
    public String courseCreation(HttpServletRequest request){

        if(request.getSession().getAttribute("userEmail") == null || request.getSession().getAttribute("status") == null || (int)request.getSession().getAttribute("status") < 2){
            return "/index";
        } else {
            return "courseCreation";
        }
    }

    @RequestMapping(value ="/quizSuccess.htm", method = RequestMethod.GET)
    public String quizSuccess(HttpServletRequest request){
        return "quizSuccess";
    }

    @RequestMapping(value = "/administration.htm")
    public String administration(HttpServletRequest request, HttpServletResponse response) {
        if(request.getSession().getAttribute("status") != null && (int)request.getSession().getAttribute("status") == 3){
            return "/admin";
        } else {
            try {
                response.sendRedirect("/");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "/index";
        }
    }

    @RequestMapping(value = "/course.htm")
    public String course(HttpServletRequest request, HttpServletResponse response){
        if(request.getSession().getAttribute("status") != null && (int)request.getSession().getAttribute("status") >= 1){
            return "/course";
        }
        return "/index";
    }

    @RequestMapping(value = "/newCourse.htm")
    public String newCourse(HttpServletRequest request){
        if(request.getSession().getAttribute("status") != null && (int)request.getSession().getAttribute("status") >= 2){
            return "/newCourseForm";
        }
        return "/index";
    }

    @RequestMapping(value = "/quiz.htm")
    public String takeQuiz(HttpServletRequest request){
        if(request.getSession().getAttribute("status") != null && (int)request.getSession().getAttribute("status") == 1){
            return "/quiz";
        }
        return "/index";
    }

    @RequestMapping(value = "/coursePreview.htm")
    public String coursePreview(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        int isTA = sdbc.isTA((String)request.getSession().getAttribute("email"));

        if(request.getSession().getAttribute("status") != null && ((int)request.getSession().getAttribute("status") >= 2 || isTA == 1)) {
            return "/coursePreview";
        }
        response.sendRedirect("/");
        return "/index";
    }

    @RequestMapping(value = "/quizCreation.htm")
    public String quizCreation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getSession().getAttribute("status") != null && (int)request.getSession().getAttribute("status") >= 2){
            return "/quizCreation";
        }
        response.sendRedirect("/");
        return "/index";
    }

    @RequestMapping(value = "/messageBoard.htm")
    public String messageBoard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getSession().getAttribute("status") != null){
            return "/messageBoard";
        }
        return "/index";
    }

    @RequestMapping(value = "/joinCourse.htm")
    public String joinCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getSession().getAttribute("status") != null){
            return "/joinCourse";
        }
        response.sendRedirect("/");
        return "/index";
    }

    /**
     * Redirects users according to their status. If the status is 2 or 1 we are brought to the the teacher metrics page.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value ="/teachertool.htm", method = RequestMethod.GET)
    public String teachertool(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String email = (String)request.getSession().getAttribute("userEmail");

        if (email == null) {
            response.sendRedirect("/");
        } else {
            try {
                User user = sdbc.getUserByEmail(email);

                if (user.getStatus() == 3){
                    response.sendRedirect("/administration.htm");//TODO:set to what redirect for admin
                    return "/administration";
                } else if(user.getStatus() == 1){
                    int isTA = sdbc.isTA(email);
                    // allow access only if student is a TA of that course
                    if (isTA == 1) {
                        return "/teachertool";
                    } else {
                        return "/course.htm";
                    }
                }

            } catch (SQLException e) {
                throw new Exception(e.getMessage());
            } catch (Exception e){
                throw new Exception(e.getMessage());
            }
            return "/teachertool";
        }
        return "/index";
    }

    /**
     * Handles redirecting users to the student metrics page.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value ="/studenttool.htm", method = RequestMethod.GET)
    public String studenttool(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        String email = (String)request.getSession().getAttribute("userEmail");

        if (email == null) {
            response.sendRedirect("/");
        } else {
            try {
                User user = sdbc.getUserByEmail(email);

                if (user.getStatus() == 3){
                    response.sendRedirect("/administration.htm");//TODO:set to what redirect for admin
                    return "/administration";
                } else if(user.getStatus() == 2){
                    response.sendRedirect("/course.htm");//TODO: set to what redirect student
                    return "/course";
                }

            } catch (SQLException e) {
                throw new Exception(e.getMessage());
            } catch (Exception e){
                throw new Exception(e.getMessage());
            }
            return "/studenttool";
        }
        System.out.println("EMAIL4: " + email);
        return "/index";
    }


    @RequestMapping(value ="/gradeQuiz.htm", method = RequestMethod.GET)
    public String gradeQuiz(HttpServletRequest request) {
        if (request.getSession().getAttribute("userEmail") == null) {
            return "/";
        } else {
            return "/gradeQuiz";
        }
    }

    @RequestMapping(value = "/privateChat.htm")
    public String privateChat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getSession().getAttribute("status") != null){
            return "/privateChat";
        }
        response.sendRedirect("/");
        return "/index";

    }

    @RequestMapping(value = "/courseDeletedMessage.htm")
    public String deleteCourseMessage(){
        return "/courseDeletedMessage";
    }

    @RequestMapping(value = "/pending.htm")
    public String accountPending(){
        return "/pending";
    }

    @RequestMapping(value = "/courseDisabled.htm")
    public String courseDisabled(){
        return "/courseDisabled";
    }

    @RequestMapping(value = "/courseDisabledStudent.htm")
    public String courseDisabledStudent(){
        return "/courseDisabledStudent";
    }

    @RequestMapping(value = "/teacherFileShare.htm")
    public String teacherFileShare(HttpServletRequest request){
        if(request.getSession().getAttribute("status") != null && (int)request.getSession().getAttribute("status") >= 2){
            return "/teacherFileShare";
        } else {
            return "/index";
        }
    }
}

