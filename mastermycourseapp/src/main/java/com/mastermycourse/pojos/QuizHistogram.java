package com.mastermycourse.pojos;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Rahul Verma
 *
 * Plain old Java Object used when retrieving quizzes statistics from database.
 */
public class QuizHistogram {
    private String title;
    private int id;
    private int[] histogram;
    private ArrayList<Double> allGrades;

    public QuizHistogram(){
        this.histogram= new int[11];
        this.setAllGrades(new ArrayList<>());
        Arrays.fill(histogram,0);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getHistogram() {
        return histogram;
    }

    public void setHistogram(int[] histogram) {
        this.histogram = histogram;
    }

    public ArrayList<Double> getAllGrades() {
        return allGrades;
    }

    public void setAllGrades(ArrayList<Double> allGrades) {
        this.allGrades = allGrades;
    }
}
