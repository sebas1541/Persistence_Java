package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Library implements Serializable {
    private Map<String, Book> books;
    private Map<String, BibliographicCollection> collections;

    public Library() {
        books = new HashMap<>();
        collections = new HashMap<>();
    }

    public void insertBook(String ID, String title, String author, String knowledgeArea, boolean state) {
        Book book = new Book(ID, title, author, knowledgeArea, state);
        books.put(ID, book);
    }

    public void editBook(String ID, String title, String author, String knowledgeArea, boolean state) {
        if (books.containsKey(ID)) {
            Book book = books.get(ID);
            book.setTitle(title);
            book.setAuthor(author);
            book.setKnowledgeArea(knowledgeArea);
            book.setState(state);
        }
    }

    public void eraseBook(String ID) {
        books.remove(ID);
    }

    public Book searchBook(String ID) {
        return books.get(ID);
    }

    public void createBibliographicCollection(String knowledgeArea) {
        collections.put(knowledgeArea, new BibliographicCollection(knowledgeArea));
    }

    public void assignBookToCollection(String bookID, String collectionKnowledgeArea) {
        if (books.containsKey(bookID) && collections.containsKey(collectionKnowledgeArea)) {
            Book book = books.get(bookID);
            BibliographicCollection collection = collections.get(collectionKnowledgeArea);
            collection.addBook(book);
        }
    }

    public Map<String, BibliographicCollection> getCollections() {
        return collections;
    }

    public Map<String, Book> getBooks() {
        return books;
    }
}