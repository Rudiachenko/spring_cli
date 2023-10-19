package com.learning.cli;

import com.learning.cli.model.BookDocument;
import com.learning.cli.repository.BookDocumentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
public class BookDocumentRepositoryTest {
    @Autowired
    private BookDocumentRepository bookDocumentRepository;

    @BeforeEach
    public void setup() {
        BookDocument bookDocument = new BookDocument(1L, "Sample Book", "John Doe", "Lorem ipsum");
        bookDocumentRepository.save(bookDocument);
    }

    @Test
    void shouldGetAllBooks() {
        BookDocument bookDocument = new BookDocument(2L, "Harry Potter", "J. K. Rowling", "content");
        bookDocumentRepository.save(bookDocument);
        List<BookDocument> allBooks = bookDocumentRepository.findAll();
        assertEquals(allBooks.size(), 2);
    }

    @Test
    void shouldGetById() {
        BookDocument bookDocument = new BookDocument(1L, "Harry Potter", "J. K. Rowling", "content");
        bookDocumentRepository.save(bookDocument);
        BookDocument book = bookDocumentRepository.findById(1L).get();
        assertNotNull(book);

        assertEquals(book.getBookId(), 1L);
    }

    @Test
    void shouldUpdateBookDocument() {
        Optional<BookDocument> bookDocumentOptional = bookDocumentRepository.findById(1L);
        Assertions.assertTrue(bookDocumentOptional.isPresent());
        BookDocument bookDocument = bookDocumentOptional.get();

        bookDocument.setBookName("Updated Book");
        bookDocumentRepository.save(bookDocument);

        Optional<BookDocument> updatedBookDocumentOptional = bookDocumentRepository.findById(1L);
        Assertions.assertTrue(updatedBookDocumentOptional.isPresent());
        BookDocument updatedBookDocument = updatedBookDocumentOptional.get();
        Assertions.assertEquals("Updated Book", updatedBookDocument.getBookName());
    }

    @Test
    void shouldDeleteBookDocument() {
        bookDocumentRepository.deleteById(1L);
        Optional<BookDocument> bookDocumentOptional = bookDocumentRepository.findById(1L);
        Assertions.assertFalse(bookDocumentOptional.isPresent());
    }

    @Test
    void shouldCreateBookDocument() {
        BookDocument newBookDocument = new BookDocument(2L, "New Book", "John Doe", "Dolor sit amet\"");

        bookDocumentRepository.save(newBookDocument);

        Optional<BookDocument> createdBookDocumentOptional = bookDocumentRepository.findById(2L);
        Assertions.assertTrue(createdBookDocumentOptional.isPresent());
        BookDocument createdBookDocument = createdBookDocumentOptional.get();
        Assertions.assertEquals("New Book", createdBookDocument.getBookName());
    }
}
