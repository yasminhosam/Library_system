package com.example.demo.repository;

import com.example.demo.entity.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibrarianRepository extends JpaRepository<Librarian, Integer> {
    Optional<Librarian> findByEmail(String email);

    List<Librarian> findByShift(String shift);
}