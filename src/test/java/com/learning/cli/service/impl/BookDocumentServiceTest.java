package com.learning.cli.service.impl;

import com.learning.cli.model.BookDocument;
import com.learning.cli.repository.BookDocumentRepository;
import com.learning.cli.service.Impl.BookDocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookDocumentServiceTest {
    @Captor
    private ArgumentCaptor<BookDocument> bookCaptor;
    @Mock
    private BookDocumentRepository bookRepository;
    @InjectMocks
    private BookDocumentServiceImpl bookService;

    @Test
    void shouldCreateBook() {
        Long bookId = 1L;
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.save(book)).thenReturn(book);
        BookDocument createdBook = bookService.create(book);

        assertEquals(book.getBookId(), createdBook.getBookId());
        assertEquals(book.getBookName(), createdBook.getBookName());
        assertEquals(book.getBookAuthor(), createdBook.getBookAuthor());
        assertEquals(book.getContent(), createdBook.getContent());

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void shouldGetBookById() {
        Long bookId = 1L;
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookDocument result = bookService.getById(bookId);

        assertNotNull(result);
        assertEquals(book, result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void shouldThrowExceptionWhenGettingBook() {
        Long bookId = 1L;
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> bookService.updateBook(bookId, book));
        verify(bookRepository, times(1)).findById(book.getBookId());
    }

    @Test
    void shouldGetAllBooks() {
        Long bookId1 = 1L;
        BookDocument book1 = getBookDocument(bookId1);

        Long bookId2 = 2L;
        BookDocument book2 = getBookDocument(bookId2);

        List<BookDocument> books = Arrays.asList(book1, book2);

        when(bookRepository.findAll()).thenReturn(books);

        Iterable<BookDocument> result = bookService.getAll();

        assertEquals(books, result);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateBook() {
        Long bookId = 1L;
        BookDocument existingBook = getBookDocument(bookId);

        BookDocument updatedBook = getBookDocument(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookDocument.class))).thenReturn(updatedBook);

        BookDocument result = bookService.updateBook(bookId, updatedBook);

        assertEquals(updatedBook, result);

        verify(bookRepository, times(1)).save(bookCaptor.capture());

        BookDocument capturedBook = bookCaptor.getValue();
        assertEquals(bookId, capturedBook.getBookId());
        assertEquals(updatedBook.getBookName(), capturedBook.getBookName());
        assertEquals(updatedBook.getBookAuthor(), capturedBook.getBookAuthor());
        assertEquals(updatedBook.getContent(), capturedBook.getContent());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingBook() {
        Long bookId = 1L;
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> bookService.updateBook(bookId, book));

        verify(bookRepository, times(1)).findById(book.getBookId());
        verify(bookRepository, times(0)).save(book);
    }

    @Test
    void shouldDeleteBook() {
        Long bookId = 1L;

        bookService.delete(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    private BookDocument getBookDocument(Long bookId) {
        BookDocument book = new BookDocument();
        book.setBookId(bookId);
        book.setBookName("Book " + bookId);
        book.setBookAuthor("Author " + bookId);
        book.setContent("Content " + bookId);
        return book;
    }
}
