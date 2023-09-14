package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BibliographicCollection implements Serializable {
    private String knowledgeArea;
    private List<Book> books;

    public BibliographicCollection(String knowledgeArea) {
        this.knowledgeArea = knowledgeArea;
        this.books = new ArrayList<>();
    }

    public String getKnowledgeArea() {
        return knowledgeArea;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
    }
}
