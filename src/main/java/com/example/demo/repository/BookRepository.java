package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {


    List<Book> findByCategory(String category);


    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAvailableCopiesGreaterThan(int availableCopies);
}