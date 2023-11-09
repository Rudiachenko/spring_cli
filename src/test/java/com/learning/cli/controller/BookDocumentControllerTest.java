package com.learning.cli.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.cli.config.TestSecurityConfig;
import com.learning.cli.model.BookDocument;
import com.learning.cli.service.BookDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookDocumentController.class)
@Import({TestSecurityConfig.class})
public class BookDocumentControllerTest {
    private final String BASE_PATH = "/api/books";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @MockBean
    private BookDocumentService bookService;

    private BookDocument book;

    @BeforeEach
    public void setUp() {
        book = new BookDocument("123", "Book name", "Author", "Content");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldGetAllBooks() throws Exception {
        List<BookDocument> bookList = List.of(
                book
        );
        when(bookService.getAll()).thenReturn(bookList);
        mockMvc.perform(get(BASE_PATH + "/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookList.size()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldGetBookById() throws Exception {
        when(bookService.getById(book.getId())).thenReturn(book);

        mockMvc.perform(get(BASE_PATH + "/get/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.content").value(book.getContent()))
                .andExpect(jsonPath("$.bookAuthor").value(book.getBookAuthor()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateBook() throws Exception {
        when(bookService.updateBook(anyString(), any(BookDocument.class))).thenReturn(book);

        mockMvc.perform(put(BASE_PATH + "/update/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                        .header("Authorization", "Bearer " + generateAdminToken()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).delete(book.getId());

        mockMvc.perform(delete(BASE_PATH + "/delete/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book))
                        .header("Authorization", "Bearer " + generateAdminToken()))
                .andExpect(status().isOk());
    }

    private String generateAdminToken() {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(60, ChronoUnit.MINUTES))
                .subject("username")
                .claim("scope", "ADMIN")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
