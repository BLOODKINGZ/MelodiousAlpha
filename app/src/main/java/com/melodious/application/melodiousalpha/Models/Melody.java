package com.melodious.application.melodiousalpha.Models;

public class Melody {
    private String name, duration, author;
    private int likes;

    public Melody() {
    }

    public Melody(String duration, String author, String name, int likes) {
        this.name = name;
        this.duration = duration;
        this.author = author;
        this.likes = likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getAuthor() {
        return author;
    }

    public int getLikes() {
        return likes;
    }
}
