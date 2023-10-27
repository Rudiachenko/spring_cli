package com.learning.cli.service;

import com.learning.cli.model.BookDocument;

import java.util.List;

public interface BookDocumentService {
    BookDocument create(BookDocument bookDocument);

    BookDocument getById(String id);

    List<BookDocument> getAll();

    BookDocument updateBook(String id, BookDocument updatedBook);

    void delete(String bookId);
}
