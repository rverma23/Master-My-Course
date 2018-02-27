package com.mastermycourse.pojos;

import java.util.ArrayList;

/**
 * Author: Rahul Verma.
 *
 * This class is used to compute chapter metrics.
 */

public class ChapterMetrics {

    private String chapterTitle;
    private ArrayList<PageMetrics> pm;
    private double totalTimeSpent;

    public ChapterMetrics(){
        setPm(new ArrayList<>());
    }

    /**
     * Gets the chapter title.
     * @return String chapter title.
     */
    public String getChapterTitle() {
        return chapterTitle;
    }

    /**
     * Sets the chapter title.
     * @param chapterTitle is the chapter title to be set.
     */
    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    /**
     * Gets the array list of page metrics.
     * @return an array list of page metrics.
     */
    public ArrayList<PageMetrics> getPm() {
        return pm;
    }

    /**
     * Sets the array list of page metrics.
     * @param pm is the array list of page metrics to be set.
     */
    public void setPm(ArrayList<PageMetrics> pm) {
        this.pm = pm;
    }

    /**
     * computes the total time gathered from each page.
     */
    public void computeTotalTime(){
        for(PageMetrics p: pm){
            p.computeMinutes();
            setTotalTimeSpent(getTotalTimeSpent() + p.getMinutes());
        }
    }

    public double getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(double totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }
}
