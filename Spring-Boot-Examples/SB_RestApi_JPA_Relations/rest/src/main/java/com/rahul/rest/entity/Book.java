package com.rahul.rest.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String bookName;

    //Book Author is another object
    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Author bookAuthor;

    public Book() {
    }

    public Book(String bookName, Author bookAuthor) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Author getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(Author bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
}
