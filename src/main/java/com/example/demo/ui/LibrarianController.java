package com.example.demo.ui;

import com.example.demo.entity.Librarian;
import com.example.demo.service.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarians")
public class LibrarianController {

    @Autowired
    private LibrarianService librarianService;

    // INSERT
    @PostMapping
    public ResponseEntity<Librarian> addLibrarian(@RequestBody Librarian librarian) {
        return ResponseEntity.ok(librarianService.addLibrarian(librarian));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Librarian>> getAllLibrarians() {
        return ResponseEntity.ok(librarianService.getAllLibrarians());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Librarian> getLibrarianById(@PathVariable int id) {
        return ResponseEntity.ok(librarianService.getLibrarianById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Librarian> updateLibrarian(@PathVariable int id, @RequestBody Librarian librarian) {
        return ResponseEntity.ok(librarianService.updateLibrarian(id, librarian));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLibrarian(@PathVariable int id) {
        return ResponseEntity.ok(librarianService.deleteLibrarian(id));
    }
}
