package com.example.demo.service;

import com.example.demo.entity.Librarian;
import com.example.demo.repository.LibrarianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibrarianService {

    @Autowired
    private LibrarianRepository librarianRepository;

    public Librarian addLibrarian(Librarian librarian)
    {
        return librarianRepository.save(librarian);
    }

    public List<Librarian> getAllLibrarians() {
        return librarianRepository.findAll(Sort.by(Sort.Direction.ASC, "firstname"));
    }
    public Librarian getLibrarianById(int id) {
        return librarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Librarian not found"));
    }

    public Librarian updateLibrarian(int id, Librarian details) {
        Librarian existing = librarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Librarian not found"));

        existing.setFirstname(details.getFirstname());
        existing.setLastname(details.getLastname());
        existing.setEmail(details.getEmail());
        existing.setShift(details.getShift());

        return librarianRepository.save(existing);
    }

    public String deleteLibrarian(int id) {
        if (!librarianRepository.existsById(id)) {
            return "Librarian not found!";
        }
        librarianRepository.deleteById(id);
        return "Deleted Successfully";
    }

    public List<Librarian> getLibrariansByShift(String shift)
    {
        return librarianRepository.findByShift(shift);
    }
}