package com.rahul.rest.service;

import com.rahul.rest.dao.BookRepository;
import com.rahul.rest.entity.Book;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    //Manual Dummy
//    public static List<Book> books=new ArrayList<>();
//
//    static{
//        books.add(new Book(123,"the art of nothing","ABC"));
//        books.add(new Book(234,"Designong your life","XYZ"));
//        books.add(new Book(345,"Everything will god gress","PTR"));
//    }

    //getting  all books
    public List<Book> getAllBooks(){
        //manual for dummy
        //return books;

        List<Book> ans=(List<Book>) this.bookRepository.findAll();
        return ans;
    }

    //finding specific book by Id
    public Book getBooksById(int id){
        //Manual
//        Book book=null;
//        book=books.stream().filter(e->e.getId()==id).findFirst().get();
//        return book;

        return this.bookRepository.findById(id);
    }

    //adding books in list
    public Book addBooks(Book book){
        //Manual
//        books.add(book);
//        return book;
        book.setId(null);
        return  this.bookRepository.save(book);

    }

   //Java 8   (delete book by id)
//    public List<Book> deleteBook(int id) {
//        //Manual
////        books.removeIf(book -> book.getId() == id);
////        return books;
//
//        this.bookRepository.deleteById(id);
//        return bookRepository.findAll();
//    }

    // delete all books
//    public List<Book> deleteAll() {
//        //Manual
////        books.clear();
////        return books;
//
//        this.bookRepository.deleteAll();
//        return bookRepository.findAll();
//    }

    //update book method
//    public List<Book> updateBookById(Book book,int id){
////
////        //Manual
//////        books=books.stream().map(b->{
//////            if(b.getId()==id){
//////                b.setBookName(book.getBookName());
//////                b.setBookAuthor(book.getBookAuthor());
//////            }
//////            return b;
//////        }).collect(Collectors.toList());
//////        return books;
////
////
//        book.setId(id);
//        List<Book> ans = (List<Book>) this.bookRepository.save(book);
//        return ans;
//       }




}
