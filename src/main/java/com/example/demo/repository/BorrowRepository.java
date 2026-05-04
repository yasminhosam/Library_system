package com.example.demo.repository;

import com.example.demo.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {

    List<Borrow> findByStudent_StudentId(int studentId);

    List<Borrow> findByBook_BookId(int bookId);

    List<Borrow> findByStatus(String status);

    List<Borrow> findByStudent_StudentIdAndStatus(int studentId, String status);
}