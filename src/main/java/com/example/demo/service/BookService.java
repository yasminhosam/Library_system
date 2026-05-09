package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;


    public Book addBook(Book book) {

        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {

        return bookRepository.findAll();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public Book updateBook(int id, Book bookDetails) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found to update"));

        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setCategory(bookDetails.getCategory());
        existingBook.setTotalCopies(bookDetails.getTotalCopies());
        existingBook.setAvailableCopies(bookDetails.getAvailableCopies());

        return bookRepository.save(existingBook);
    }


    public String deleteBook(int id) {
        if (!bookRepository.existsById(id)) {
            return "Book not found";
        }
        bookRepository.deleteById(id);
        return "Deleted Successfully";
    }

    public List<Book> getAvailableBooksOnly() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }
}