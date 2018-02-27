package com.mastermycourse.controller;

import com.mastermycourse.beans.QuizBean;
import com.mastermycourse.database.StudentTeacherDBCommands;
import com.mastermycourse.database.TeacherDBCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Zach on 5/23/17.
 */
public class MessageBoardController {
    @Autowired
    ApplicationContext ctx;


}
