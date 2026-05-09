package com.example.demo.ui;

import com.example.demo.entity.Borrow;
import com.example.demo.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;


    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(
            @RequestParam int studentId,
            @RequestParam int bookId,
            @RequestParam int librarianId) {
        return ResponseEntity.ok(borrowService.borrowBook(studentId, bookId, librarianId));
    }


    @PutMapping("/return/{borrowId}")
    public ResponseEntity<String> returnBook(@PathVariable int borrowId) {
        return ResponseEntity.ok(borrowService.returnBook(borrowId));
    }


    @GetMapping
    public ResponseEntity<List<Borrow>> getAllBorrows() {
        return ResponseEntity.ok(borrowService.getAllBorrows());
    }
}
