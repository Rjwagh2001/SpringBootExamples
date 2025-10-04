package com.rahul.rest.controller;

import com.rahul.rest.entity.Book;
import com.rahul.rest.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {


    @Autowired
    BookService bookService;

    //getting books handeler
    @GetMapping("/books")
    public List<Book> getAllBooks(){
        return this.bookService.getAllBooks();
    }

    //getting book by id handler
    @GetMapping("/books/{id}")
    public Book getBooksById(@PathVariable("id") int ids){
        return this.bookService.getBooksById(ids);
    }

    // adding book handler
    @PostMapping("/books")
    public Book addBook(@RequestBody Book book){

        return  this.bookService.addBooks(book);

    }

    //delete specific books by Id handler
    @DeleteMapping("/books/{id}")
    public List<Book> deleteBookById(@PathVariable("id") int id){
       List<Book> ans=this.bookService.deleteBook(id);
        return ans;

    }

    //delete all book handler
    @DeleteMapping("/books")
    public List<Book> deleteAll(){
        List<Book> ans=bookService.deleteAll();
         return ans;
    }

    //update book handler
    @PutMapping("/books/{bookId}")
    public List<Book> updateBook(@RequestBody Book book, @PathVariable("bookId") int id){
      return this.bookService.updateBookById(book,id);
        //return null;
    }







}
