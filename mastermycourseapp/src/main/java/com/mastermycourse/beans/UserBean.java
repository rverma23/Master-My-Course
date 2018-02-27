package com.mastermycourse.beans;

import com.mastermycourse.database.StudentDBCommands;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authors: Zach Lerman and James DeCarlo.
 *
 * This bean is used by the TA to check if they are a TA or not for the given course.
 * Implements Serializable and ApplicationContextAware.
 */
public class UserBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;
    private int userId;
    private String email;
    private Logger log = Logger.getLogger(UserBean.class.getName());

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if a user with given email is a teaching assistant
     * @return
     */
    public int isTA() {
        StudentDBCommands sdbc = new StudentDBCommands(ctx);
        int isTA = 0;
        try {
            isTA = sdbc.isTA(email);
        } catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return isTA;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
