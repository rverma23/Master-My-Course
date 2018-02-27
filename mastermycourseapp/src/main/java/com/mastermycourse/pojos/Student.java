package com.mastermycourse.pojos;

/**
 * Author: Jose Rodriguez
 *
 * Plain old Java Object used when retrieving students from database.
 */
public class Student {
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}
