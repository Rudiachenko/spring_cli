package com.learning.cli.service.Impl;

import com.learning.cli.model.BookDocument;
import com.learning.cli.repository.BookDocumentRepository;
import com.learning.cli.service.BookDocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@Service
public class BookDocumentServiceImpl implements BookDocumentService {
    private final BookDocumentRepository bookDocumentRepository;

    @Autowired
    public BookDocumentServiceImpl(BookDocumentRepository bookDocumentRepository) {
        this.bookDocumentRepository = bookDocumentRepository;
    }

    @Override
    public BookDocument create(BookDocument bookDocument) {
        log.info("Creating book: " + bookDocument.getBookName());
        return bookDocumentRepository.save(bookDocument);
    }

    @Override
    public BookDocument getById(String bookId) {
        log.info("Getting book with id: " + bookId);
        Optional<BookDocument> bookDocument = bookDocumentRepository.findById(bookId);
        if (bookDocument.isPresent()) {
            log.info("book document was got successfully: " + bookDocument.get());
            return bookDocument.get();
        } else {
            throw new NoSuchElementException("Can't get book document with id: " + bookId);
        }
    }

    @Override
    public List<BookDocument> getAll() {
        log.info("Getting all books");
        List<BookDocument> bookDocuments = bookDocumentRepository.findAll();
        if (bookDocuments.isEmpty()) {
            log.info("No book documents are presented. Returning empty list");
            return Collections.emptyList();
        } else {
            log.info("Book documents were got successfully: " + bookDocuments);
            return bookDocuments;
        }
    }

    @Override
    public BookDocument updateBook(String id, BookDocument updatedBook) {
        log.info("Updating book with ID: " + id);
        BookDocument book = bookDocumentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID: " + id));

        book.setBookName(updatedBook.getBookName());
        book.setBookAuthor(updatedBook.getBookAuthor());
        book.setContent(updatedBook.getContent());
        return bookDocumentRepository.save(book);
    }

    @Override
    public void delete(String bookId) {
        log.info("Deleting book with id " + bookId);
        bookDocumentRepository.deleteById(bookId);
        log.info("Book with id " + bookId + " was successfully deleted");
    }
}
