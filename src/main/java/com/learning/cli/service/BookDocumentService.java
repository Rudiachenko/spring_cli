package com.learning.cli.service;

import com.learning.cli.model.BookDocument;

import java.util.List;

public interface BookDocumentService {
    BookDocument create(BookDocument bookDocument);

    BookDocument getById(Long id);

    List<BookDocument> getAll();

    BookDocument updateBook(Long id, BookDocument updatedBook);

    void delete(Long bookId);
}
