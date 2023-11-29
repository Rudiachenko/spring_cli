package com.learning.cli.realdb;

import com.learning.cli.model.BookDocument;
import com.learning.cli.model.Role;
import com.learning.cli.model.User;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.TestSocketUtils.findAvailableTcpPort;

/**
 * The {@code GeneralMongoDBOperationsTest} class is responsible for demonstrating and testing
 * various MongoDB operations such as create, update, and delete in the 'users' and 'books' collections.
 * <p>
 * It manually sets up and tears down a MongoDB instance through code before and after each test method.
 * This includes running a Mongo server manually, initializing the collections, and setting up MongoTemplate
 * for database interactions.
 */
public class GeneralMongoDBOperationsTest {
    private static final String LOCAL_HOST = "localhost";
    private static final String UPDATE_OPERATION = "$set";
    private static final String TEST_DB = "testdb";
    private static final String USERS_COLLECTION = "users";
    private static final String BOOKS_COLLECTION = "books";
    private static final Integer PORT = findAvailableTcpPort();
    private static MongoClient client;
    private static MongoServer server;
    private static MongoCollection<Document> userCollection;
    private static MongoCollection<Document> booksCollection;
    private static MongoTemplate mongoTemplate;

    @BeforeEach
    public void setUp() {
        // Setup Mongo server
        server = new MongoServer(new MemoryBackend());
        server.bind(LOCAL_HOST, PORT);

        // Create MongoClient settings
        final MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(LOCAL_HOST))))
                .build();

        // Create MongoClient and MongoCollections
        client = MongoClients.create(settings);
        userCollection = client.getDatabase(TEST_DB).getCollection(USERS_COLLECTION);
        booksCollection = client.getDatabase(TEST_DB).getCollection(BOOKS_COLLECTION);

        // Initialize MongoTemplate
        mongoTemplate = new MongoTemplate(new SimpleMongoClientDatabaseFactory(client, TEST_DB), getMongoConverter());
    }

    @AfterEach
    public void tearDown() {
        // Drop the collection, close the client and shutdown the server after each test
        userCollection.drop();
        booksCollection.drop();
        client.close();
        server.shutdown();
    }

    @Test
    public void shouldSaveUser() {
        // Create and insert user document
        User user = createUser("testUser", "password");
        Document doc = convertToDocument(user);
        userCollection.insertOne(doc);

        // Verify the document is inserted
        Document docFromDb = userCollection.find(doc).first();
        assertThat(userCollection.countDocuments()).isEqualTo(1);
        assertThat(doc).isEqualTo(docFromDb);
    }

    @Test
    public void shouldUpdateUser() {
        // Insert a user document first
        User user = createUser("testUser", "password");
        Document doc = convertToDocument(user);
        userCollection.insertOne(doc);

        // Update user information
        String newUsername = "newUsername";
        String newPassword = "newPassword";
        user.setUsername(newUsername);
        user.setPassword(newPassword);

        // Update document in database
        Bson filteredById = new Document("_id", user.getId());
        Bson updated = new Document(UPDATE_OPERATION, new Document("username", newUsername)
                .append("password", newPassword));
        userCollection.updateOne(filteredById, updated);

        // Verify the updated document
        Document updatedDocFromDb = userCollection.find(filteredById).first();
        assertThat(updatedDocFromDb.getString("username")).isEqualTo(newUsername);
        assertThat(updatedDocFromDb.getString("password")).isEqualTo(newPassword);
    }

    @Test
    public void shouldDeleteUser() {
        // Insert a user document first
        User user = createUser("testUser", "password");
        Document doc = convertToDocument(user);
        userCollection.insertOne(doc);

        // Delete the document
        Bson filteredById = new Document("_id", user.getId());
        userCollection.deleteOne(filteredById);

        // Verify it's deleted
        assertThat(userCollection.countDocuments()).isEqualTo(0);
    }

    @Test
    public void shouldSaveBook() {
        // Create and insert book document
        BookDocument book = new BookDocument("1", "Test Book", "Test Author", "Test Content");
        Document doc = convertToDocument(book);
        booksCollection.insertOne(doc);

        // Verify the document is inserted
        Document docFromDb = booksCollection.find(doc).first();
        assertThat(booksCollection.countDocuments()).isEqualTo(1);
        assertThat(doc).isEqualTo(docFromDb);
    }

    @Test
    public void shouldUpdateBook() {
        // Insert a book document first
        BookDocument book = new BookDocument("10", "Test Book", "Test Author", "Test Content");
        Document doc = convertToDocument(book);
        booksCollection.insertOne(doc);

        // Update book information
        String newBookName = "New Book Name";
        String newContent = "New Content";
        book.setBookName(newBookName);
        book.setContent(newContent);

        // Update document in database
        Bson filtered = new Document("_id", book.getId());
        Bson updated = new Document("$set", new Document("bookName", newBookName)
                .append("content", newContent));
        booksCollection.updateOne(filtered, updated);

        // Verify the updated document
        Document updatedDocFromDb = booksCollection.find(filtered).first();
        assertThat(updatedDocFromDb.getString("bookName")).isEqualTo(newBookName);
        assertThat(updatedDocFromDb.getString("content")).isEqualTo(newContent);
    }

    @Test
    public void shouldDeleteBook() {
        // Insert a book document first
        BookDocument book = new BookDocument("1", "Test Book", "Test Author", "Test Content");
        Document doc = convertToDocument(book);
        booksCollection.insertOne(doc);

        // Delete the document
        Bson filtered = new Document("_id", book.getId());
        booksCollection.deleteOne(filtered);

        // Verify it's deleted
        assertThat(booksCollection.countDocuments()).isEqualTo(0);
    }

    private MongoConverter getMongoConverter() {
        // Create and return a MongoConverter
        MongoDatabaseFactory mongoDbFactory = new SimpleMongoClientDatabaseFactory(client, TEST_DB);
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setAutoIndexCreation(true);
        mappingContext.afterPropertiesSet();
        return new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), mappingContext);
    }

    private User createUser(String username, String password) {
        // Create and return a User object
        Role role = new Role(Role.RoleName.USER);
        return new User("userId", username, password, Collections.singleton(role), true);
    }

    private Document convertToDocument(User user) {
        // Convert and return User object to Document
        return new Document("_id", user.getId())
                .append("username", user.getUsername())
                .append("password", user.getPassword())
                .append("roles", user.getRoles().toString());
    }

    private Document convertToDocument(BookDocument book) {
        // Convert and return BookDocument object to Document
        return new Document("_id", book.getId())
                .append("bookName", book.getBookName())
                .append("bookAuthor", book.getBookAuthor())
                .append("content", book.getContent());
    }
}
