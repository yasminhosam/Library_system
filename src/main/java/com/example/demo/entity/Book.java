package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private List<Borrow> borrowRecords;

    public List<Borrow> getBorrowRecords() {
        return borrowRecords;
    }

    public void setBorrowRecords(List<Borrow> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }
    private String title;
    private String author;
    private String category;

    @Column(name = "total_copies")
    private int totalCopies;

    @Column(name = "available_copies")
    private int availableCopies;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}