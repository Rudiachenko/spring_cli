package com.learning.cli.controller;

import com.learning.cli.model.BookDocument;
import com.learning.cli.service.BookDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/books")
public class BookDocumentController {
    private final BookDocumentService bookService;

    @Autowired
    public BookDocumentController(BookDocumentService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public BookDocument createBook(@RequestBody BookDocument book) {
        return bookService.create(book);
    }

    @GetMapping("/all")
    public List<BookDocument> getAllBooks() {
        return bookService.getAll();
    }

    @GetMapping("/get/{id}")
    public BookDocument getBookById(@PathVariable String id) {
        return bookService.getById(id);
    }

    @PutMapping("/update/{id}")
    public BookDocument updateBook(@PathVariable String id, @RequestBody BookDocument updatedBook) {
        return bookService.updateBook(id, updatedBook);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.delete(id);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
