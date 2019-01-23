package com.melodious.application.melodiousalpha.Models;

public class User {
    private String name, location, about;

    public User(){

    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getAbout() {
        return about;
    }
}
