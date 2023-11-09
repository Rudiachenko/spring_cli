package com.learning.cli.repository;

import com.learning.cli.model.BookDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DataMongoTest
public class BookDocumentRepositoryTest {
    @Autowired
    private BookDocumentRepository bookDocumentRepository;

    @BeforeEach
    public void setup() {
        BookDocument bookDocument = new BookDocument("1", "Sample Book", "John Doe", "Lorem ipsum");
        bookDocumentRepository.save(bookDocument);
    }

    @AfterEach
    void tearDown() {
        bookDocumentRepository.deleteAll();
    }

    @Test
    void shouldGetById() {
        BookDocument bookDocument = new BookDocument("1", "Harry Potter", "J. K. Rowling", "content");
        bookDocumentRepository.save(bookDocument);
        BookDocument book = bookDocumentRepository.findById("1").get();
        assertNotNull(book);

        assertEquals(book.getId(), "1");
    }

    @Test
    void shouldUpdateBookDocument() {
        Optional<BookDocument> bookDocumentOptional = bookDocumentRepository.findById("1");
        Assertions.assertTrue(bookDocumentOptional.isPresent());
        BookDocument bookDocument = bookDocumentOptional.get();

        bookDocument.setBookName("Updated Book");
        bookDocumentRepository.save(bookDocument);

        Optional<BookDocument> updatedBookDocumentOptional = bookDocumentRepository.findById("1");
        Assertions.assertTrue(updatedBookDocumentOptional.isPresent());
        BookDocument updatedBookDocument = updatedBookDocumentOptional.get();
        Assertions.assertEquals("Updated Book", updatedBookDocument.getBookName());
    }

    @Test
    void shouldDeleteBookDocument() {
        bookDocumentRepository.deleteById("1");
        Optional<BookDocument> bookDocumentOptional = bookDocumentRepository.findById("1");
        Assertions.assertFalse(bookDocumentOptional.isPresent());
    }

    @Test
    void shouldCreateBookDocument() {
        BookDocument newBookDocument = new BookDocument("2", "New Book", "John Doe", "Dolor sit amet\"");

        bookDocumentRepository.save(newBookDocument);

        Optional<BookDocument> createdBookDocumentOptional = bookDocumentRepository.findById("2");
        Assertions.assertTrue(createdBookDocumentOptional.isPresent());
        BookDocument createdBookDocument = createdBookDocumentOptional.get();
        Assertions.assertEquals("New Book", createdBookDocument.getBookName());
    }
}
