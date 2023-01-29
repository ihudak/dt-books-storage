package com.dynatrace.storage.controller;

import com.dynatrace.storage.exception.InsufficientResourcesException;
import com.dynatrace.storage.exception.ResourceNotFoundException;
import com.dynatrace.storage.model.Book;
import com.dynatrace.storage.model.Storage;
import com.dynatrace.storage.repository.BookRepository;
import com.dynatrace.storage.repository.StorageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
public class StorageController {
    @Value("${added.workload.cpu}")
    private long cpuPressure;
    @Value("${added.workload.ram}")
    private int memPressureMb;

    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private BookRepository bookRepository;
    Logger logger = LoggerFactory.getLogger(StorageController.class);

    // get all storage items
    @GetMapping("/storage")
    public List<Storage> getAllStorageItems() {
        return storageRepository.findAll();
    }

    // get a storage item by id
    @GetMapping("/storage/{id}")
    public Storage getStorageItemById(@PathVariable Long id) {
        Optional<Storage> storage = storageRepository.findById(id);
        if (storage.isEmpty()) {
            throw new ResourceNotFoundException("Book is not found in the storage");
        }
        return storage.get();
    }

    // get all users who have the book in their cart
    @GetMapping("/storage/findByISBN")
    public Storage getCartsByISBN(@RequestParam String isbn) {
        this.verifyBook(isbn);
        return storageRepository.findByIsbn(isbn);
    }

    // create a book in storage
    @PostMapping("/storage")
    public Storage createStorage(@RequestBody Storage storage) {
        return ingestBook(storage);
    }

    // add book to storage
    @PostMapping("/storage/ingest-book")
    public Storage addBooksToStorage(@RequestBody Storage storage) {
        return ingestBook(storage);
    }

    private Storage ingestBook(Storage storage) {
        this.verifyBook(storage.getIsbn());
        simulateHardWork();

        Storage storageDb = storageRepository.findByIsbn(storage.getIsbn());
        if (null == storageDb) {
            return storageRepository.save(storage);
        }
        storageDb.setQuantity(storageDb.getQuantity() + storage.getQuantity());
        return storageRepository.save(storageDb);
    }

    @PostMapping("/storage/sell-book")
    public Storage sellBooksFromStorage(@RequestBody Storage storage) {
        this.verifyBook(storage.getIsbn());
        simulateHardWork();

        Book book = bookRepository.getBookByISBN(storage.getIsbn());
        Storage storageDb = storageRepository.findByIsbn(storage.getIsbn());
        if (null == book || storageDb.getQuantity() < storage.getQuantity()) {
            throw new InsufficientResourcesException("Insufficient books in the storage");
        }
        storageDb.setQuantity(storageDb.getQuantity() - storage.getQuantity());
        return storageRepository.save(storageDb);
    }

    // update a storage item
    @PutMapping("/storage/{id}")
    public Storage updateStorageById(@PathVariable Long id, @RequestBody Storage storage) {
        this.verifyBook(storage.getIsbn());
        simulateHardWork();

        Optional<Storage> storageDb = storageRepository.findById(id);
        if (storageDb.isEmpty() || storageDb.get().getId() != storage.getId()) {
            throw new ResourceNotFoundException("Book is not found, ISBN: " + storage.getIsbn());
        }
        return storageRepository.save(storage);
    }

    // delete an item from storage
    @DeleteMapping("/storage/{id}")
    public void deleteStorageItemById(@PathVariable Long id) {
        storageRepository.deleteById(id);
    }

    // delete all books
    @DeleteMapping("/storage/delete-all")
    public void deleteAllBooks() {
        storageRepository.deleteAll();
    }

    private void verifyBook(String isbn) {
        Book book = bookRepository.getBookByISBN(isbn);
        if (null == book) {
            throw new ResourceNotFoundException("Book not found by isbn " + isbn);
        }
        Book[] books = bookRepository.getAllBooks();
        logger.debug(books.toString());
    }

    private void simulateHardWork() {
        int arraySize = (int)((long)this.memPressureMb * 1024L * 1024L / 8L);
        if (arraySize < 0) {
            arraySize = Integer.MAX_VALUE;
        }
        long[] longs = new long[arraySize];
        int j = 0;
        for(long i = 0; i < this.cpuPressure; i++, j++) {
            j++;
            if (j >= arraySize) {
                j = 0;
            }
            try {
                if (longs[j] > Integer.MAX_VALUE) {
                    longs[j] = (long) Integer.MIN_VALUE;
                }
            } catch (Exception ignored) {};
        }
    }
}
