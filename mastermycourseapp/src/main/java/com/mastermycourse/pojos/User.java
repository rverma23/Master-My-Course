package com.mastermycourse.pojos;

/**
 * Author: James DeCarlo.
 *
 * Plain old Java Object used when retrieving Users from database.
 */
public class User {
    private int id;
    private String email;
    private String name;
    private int status;
    private String imageUrl;

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
