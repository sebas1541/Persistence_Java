package models;

import java.io.Serializable;

public class Book implements Serializable {
    private String ID;
    private String title;
    private String author;
    private String knowledgeArea;
    private boolean state;

    public Book() {
    }

    public Book(String ID, String title, String author, String knowledgeArea, boolean state) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.knowledgeArea = knowledgeArea;
        this.state = state;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKnowledgeArea() {
        return knowledgeArea;
    }

    public void setKnowledgeArea(String knowledgeArea) {
        this.knowledgeArea = knowledgeArea;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Book ID: " + ID +
                "\nTitle: " + title +
                "\nAuthor: " + author +
                "\nKnowledge Area: " + knowledgeArea +
                "\nState: " + state;
    }
}