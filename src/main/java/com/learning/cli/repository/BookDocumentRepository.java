package com.learning.cli.repository;

import com.learning.cli.model.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookDocumentRepository extends MongoRepository<BookDocument, Long> {
}
