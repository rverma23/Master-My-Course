package com.mastermycourse.pojos;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zach on 5/23/17.
 * Represents a message used on the message board
 */
public class Message {
    private String summary; // summary description about the post
    private String message; // what the student/teacher made
    private Date date;  // when the post was made.
    private String userName; // who posted this message.
    private ArrayList<Message> responses; // set of response messages to the main message

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Message> getResponses() {
        return responses;
    }

    public void setResponses(ArrayList<Message> responses) {
        this.responses = responses;
    }
}
