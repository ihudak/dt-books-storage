package com.dynatrace.storage.repository;

import com.dynatrace.storage.exception.ResourceNotFoundException;
import com.dynatrace.storage.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class BookRepository {
    @Value("${http.service.books}")
    private String bookBaseURL;
    private RestTemplate restTemplate;
    private Logger logger = LoggerFactory.getLogger(BookRepository.class);

    public BookRepository() {
        restTemplate = new RestTemplate();
    }


    public Book getBookByISBN(String isbn) {
        String urlBuilder = bookBaseURL +
                "/find" +
                "?isbn=" +
                isbn;
        logger.info("Getting book " + isbn);
        Book book = restTemplate.getForObject(urlBuilder, Book.class);
        if (null == book) {
            ResourceNotFoundException ex = new ResourceNotFoundException("Book not found by isbn: " + isbn);
            logger.error(ex.getMessage());
            throw ex;
        }
        return book;
    }

    public Book[] getAllBooks() {
        return restTemplate.getForObject(bookBaseURL, Book[].class);
    }
}
