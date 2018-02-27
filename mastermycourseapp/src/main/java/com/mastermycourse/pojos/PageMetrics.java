package com.mastermycourse.pojos;


/**
 * Author: Rahul Verma.
 */
public class PageMetrics {

    private String title;
    private int seconds;
    private int id;
    private double minutes;

    public PageMetrics(){
    }

    public PageMetrics(String title, int seconds, int id){
        this.setTitle(title);
        this.seconds = seconds;
        this.setId(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void computeMinutes(){
        setMinutes(((double)seconds)/60);
    }

    public double getMinutes() {
        return minutes;
    }

    public void setMinutes(double minutes) {
        this.minutes = minutes;
    }

}
