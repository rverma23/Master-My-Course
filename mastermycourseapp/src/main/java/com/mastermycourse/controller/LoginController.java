package com.mastermycourse.controller;

import com.mastermycourse.beans.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Map;

/**
 * Authors: James DeCarlo and Zach Lerman.
 *
 * This class contains the login controls.  It contains methods
 * that manage login and logout.
 */
@Controller
public class LoginController {

    @Autowired
    ServletContext context;

    /**
     * This method manages login of a user.
     * Certain attributes of the user upon login are picked up and set as session attributes.
     * The user is then redirected to the homepage.
     * @param req contains user name, user id, user email, and user image attributes.
     * @param request contains the state attribute and this param is used to set the correct session attributes for the user
     * @param response is used to redirect to user to register.htm
     * @throws IOException
     */
    @RequestMapping(value="/Login", method= RequestMethod.POST)
    public void login(@RequestParam Map<String, String> req, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(req.get("state") == null || !req.get("state").equals(request.getSession().getAttribute("state"))){
            response.getWriter().print("State: " + req.get("state"));
            response.getWriter().print("  State: " +request.getAttribute("state"));
            response.sendRedirect("/");
            return;
        }

        request.getSession().setAttribute("userName", req.get("userName"));
        request.getSession().setAttribute("userId", req.get("userId"));
        request.getSession().setAttribute("userEmail", req.get("userEmail"));
        request.getSession().setAttribute("userImage", req.get("userImage"));
        response.getWriter().print("userEmail: " + req.get("userEmail"));

        // setup a UserBean
        UserBean userBean = (UserBean)request.getSession().getAttribute("userBean");
        userBean.setEmail(req.get("userEmail"));

        response.sendRedirect("register.htm");
    }

    /**
     * This method manages logging a user out.
     * The user is redirected to the homepage upon logout.
     * @param req contains the session that we delete upon logging out
     * @param resp is used to redirect the user back to the home page.
     * @throws IOException
     */
    @RequestMapping(value = "/Logout")
    public void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // you can also make an authenticated request to logout, but here we choose to
        // simply delete the session variables for simplicity
        HttpSession session =  req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // rebuild session
        req.getSession();
        resp.sendRedirect("/");
    }
}