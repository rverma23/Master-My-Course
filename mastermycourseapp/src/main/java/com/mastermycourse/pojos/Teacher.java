package com.mastermycourse.pojos;

/**
 * Author: James DeCarlo.
 *
 * Plain old Java Object used when retrieving Teachers from database.
 */
public class Teacher extends User {
    private String school;
    private String description;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
