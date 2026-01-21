package com.projekt.wypozyczalnia.services;

import com.projekt.wypozyczalnia.dao.BookRepository;
import com.projekt.wypozyczalnia.dto.demo.*;
import com.projekt.wypozyczalnia.models.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class PublicDemoService {

    private final BookRepository bookRepository;

    public PublicDemoService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public DemoDataResponseDto getDemoData() {
        log.info("Starting demo-data generation...");
        var books = bookRepository.findAll();
        log.info("Fetched entities: books={}", books.size());

        String timestamp = Instant.now().toString();

        var booksDto = books.stream().map(b -> {
            // Mockowanie zagnieżdżonych danych (specs -> dimensions)
            // W realnym świecie mogłoby to iść z innej tabeli
            return DemoBookDto.builder()
                .id(b.getId())
                .title(b.getTitle())
                .author(b.getAuthor())
                .isbn(b.getIsbn())
                .publishedYear(b.getPublishedYear())
                .genre(b.getGenre())
                .description(b.getDescription())
                .totalCopies(b.getTotalCopies())
                .availableCopies(b.getAvailableCopies())
                .imageUrl(nullToEmpty(b.getImageUrl()))
                .specs(BookSpecsDto.builder()
                        .coverType("Hardcover")
                        .pages(300 + (b.getTitle().length() * 5)) // pseudo-random number
                        .language("PL")
                        .dimensions(BookDimensionsDto.builder()
                                .widthMm(145)
                                .heightMm(205)
                                .thicknessMm(25)
                                .build())
                        .build())
                .build();
        }).toList();
        log.info("Mapped booksDto");

        log.info("Building final response...");
        return DemoDataResponseDto.builder()
                .timestamp(timestamp)
                .books(booksDto)
                .build();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
