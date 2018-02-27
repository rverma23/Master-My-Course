package com.mastermycourse.beans;

import com.mastermycourse.database.TeacherDBCommands;
import com.mastermycourse.pojos.Message;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Zach on 5/23/17.
 * Helper methods and properties to manage the messenger board of a course
 */
public class MessageBoardBean implements Serializable, ApplicationContextAware {
    private ApplicationContext ctx;

    public ArrayList<Message> getMessages(int courseId) throws SQLException {
        TeacherDBCommands tdbc = new TeacherDBCommands(ctx);
        ArrayList<Message> messages = tdbc.getMessages(courseId);
        return messages;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
