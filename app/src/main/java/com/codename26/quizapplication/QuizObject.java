package com.codename26.quizapplication;


/**
 * Created by Dell on 07.09.2017.
 */

public class QuizObject {
    private int id;
    private String title;
    private int numberOfSearchQueries;
    private int imageId;

    public QuizObject(int id, String title, int numberOfSearchQueries, int imageId) {
        this.id = id;
        this.title = title;
        this.numberOfSearchQueries = numberOfSearchQueries;
        this.imageId = imageId;
    }

    public QuizObject() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getNumberOfSearchQueries() {
        return numberOfSearchQueries;
    }

    public void setNumberOfSearchQueries(int numberOfSearchQueries) {
        this.numberOfSearchQueries = numberOfSearchQueries;
    }
}
