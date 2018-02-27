package com.mastermycourse.beans;

import java.io.Serializable;

/**
 * Author: Zach Lerman.
 *
 * This class holds information for sending messages to the user. This is
 * done by using the properties of the class to display a Materialize toast on the
 * user's browser.
 */
public class MessageBean implements Serializable {
    private String message = "";
    private String color;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Parse the data in format "message,color" to set the message bean
     * @param data
     */
    public void setMessageBean(String data) {
        String[] properties = data.split(",");
        message = properties[0];
        color = properties[1];
    }
}
