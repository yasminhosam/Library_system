package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.entity.Borrow;
import com.example.demo.entity.Librarian;
import com.example.demo.entity.Student;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.BorrowRepository;
import com.example.demo.repository.LibrarianRepository;
import com.example.demo.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowService {
    @Autowired
    private BorrowRepository borrowRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private LibrarianRepository librarianRepository;

    @Transactional
    public String borrowBook(int studentId,int bookId,int librarianId){
        Student student=studentRepository.findById(studentId)
                .orElseThrow(()->new RuntimeException("Student not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found "));
        Librarian librarian = librarianRepository.findById(librarianId)
                .orElseThrow(() -> new RuntimeException("Librarian not found"));

        if(book.getAvailableCopies() <=0){
            return "Sorry,there is no available copy of this book";
        }
        List<Borrow> currentBorrows = borrowRepository.findByStudent_StudentIdAndStatus(studentId, "BORROWED");
        if (currentBorrows.size() >= student.getMaxBorrowLimit()) {
            return "Sorry, you reached your max limit ";
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Borrow borrow = new Borrow();
        borrow.setStudent(student);
        borrow.setBook(book);
        borrow.setLibrarian(librarian);
        Date today = new Date();
        borrow.setBorrowDate(today);

        long fourteenDaysInMillis = 14L * 24 * 60 * 60 * 1000;
        borrow.setDueDate(new Date(today.getTime() + fourteenDaysInMillis));

        borrow.setStatus("BORROWED");
        borrowRepository.save(borrow);
        return  "Borrowed Successfully";
    }

    public List<Borrow> searchTransactions(String keyword) {
        return borrowRepository.findAll().stream()
                .filter(b ->
                        (b.getStudent() != null && (b.getStudent().getFirstname() + " " + b.getStudent().getLastname()).toLowerCase().contains(keyword.toLowerCase())) ||
                                (b.getBook() != null && b.getBook().getTitle().toLowerCase().contains(keyword.toLowerCase())) ||
                                (b.getLibrarian() != null && (b.getLibrarian().getFirstname() + " " + b.getLibrarian().getLastname()).toLowerCase().contains(keyword.toLowerCase()))
                )
                .collect(Collectors.toList());
    }

    public List<Borrow> getBorrowsByStatus(String status) {
        return borrowRepository.findByStatus(status);
    }

    public List<Borrow> getOverdueLoans() {
        return borrowRepository.findByStatusAndDueDateBefore("BORROWED", new java.util.Date());
    }

    @Transactional
    public String returnBook(int borrowId) {

        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new RuntimeException("Borrow operation not found"));


        if ("RETURNED".equals(borrow.getStatus())) {
            return "This book is already returned";
        }


        borrow.setReturnDate(new Date());
        borrow.setStatus("RETURNED");


        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);


        bookRepository.save(book);
        borrowRepository.save(borrow);

        return "Book Returned Successfully";
    }


    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }
}
