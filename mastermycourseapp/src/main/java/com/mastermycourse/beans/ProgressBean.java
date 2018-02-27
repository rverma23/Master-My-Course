package com.mastermycourse.beans;

import java.io.Serializable;

/**
 * Author: James DeCarlo.
 *
 * Application Scoped Bean that is named by progress concatenated with the userId. Used to get the progress of
 * pdf file conversion for asynchronous file conversion progress. You must set the thread of the process running.
 *
 *
 */
public class ProgressBean implements Serializable {

    private float percentage = 0;
    private String message = "Initializing";
    private Integer courseId;
    private Thread thread;

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
