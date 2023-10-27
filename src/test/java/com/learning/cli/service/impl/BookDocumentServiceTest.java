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
        String bookId = "1";
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.save(book)).thenReturn(book);
        BookDocument createdBook = bookService.create(book);

        assertEquals(book.getId(), createdBook.getId());
        assertEquals(book.getBookName(), createdBook.getBookName());
        assertEquals(book.getBookAuthor(), createdBook.getBookAuthor());
        assertEquals(book.getContent(), createdBook.getContent());

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void shouldGetBookById() {
        String bookId = "1";
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookDocument result = bookService.getById(bookId);

        assertNotNull(result);
        assertEquals(book, result);
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void shouldThrowExceptionWhenGettingBook() {
        String bookId = "1";
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> bookService.updateBook(bookId, book));
        verify(bookRepository, times(1)).findById(book.getId());
    }

    @Test
    void shouldGetAllBooks() {
        String bookId1 = "1";

        BookDocument book1 = getBookDocument(bookId1);

        String bookId2 = "2";
        BookDocument book2 = getBookDocument(bookId2);

        List<BookDocument> books = Arrays.asList(book1, book2);

        when(bookRepository.findAll()).thenReturn(books);

        Iterable<BookDocument> result = bookService.getAll();

        assertEquals(books, result);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldUpdateBook() {
        String bookId = "1";
        BookDocument existingBook = getBookDocument(bookId);

        BookDocument updatedBook = getBookDocument(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(BookDocument.class))).thenReturn(updatedBook);

        BookDocument result = bookService.updateBook(bookId, updatedBook);

        assertEquals(updatedBook, result);

        verify(bookRepository, times(1)).save(bookCaptor.capture());

        BookDocument capturedBook = bookCaptor.getValue();
        assertEquals(bookId, capturedBook.getId());
        assertEquals(updatedBook.getBookName(), capturedBook.getBookName());
        assertEquals(updatedBook.getBookAuthor(), capturedBook.getBookAuthor());
        assertEquals(updatedBook.getContent(), capturedBook.getContent());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingBook() {
        String bookId = "1";
        BookDocument book = getBookDocument(bookId);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> bookService.updateBook(bookId, book));

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(0)).save(book);
    }

    @Test
    void shouldDeleteBook() {
        String bookId = "1";

        bookService.delete(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    private BookDocument getBookDocument(String bookId) {
        BookDocument book = new BookDocument();
        book.setId(bookId);
        book.setBookName("Book " + bookId);
        book.setBookAuthor("Author " + bookId);
        book.setContent("Content " + bookId);
        return book;
    }
}
