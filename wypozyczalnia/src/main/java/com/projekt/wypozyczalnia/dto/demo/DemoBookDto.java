package com.projekt.wypozyczalnia.dto.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "DemoBook")
public class DemoBookDto {
    // 10 pól prostych (zgodnie z bazą danych)
    UUID id;
    String title;
    String author;
    String isbn;
    Integer publishedYear;
    String genre;
    String description;
    Integer totalCopies;
    Integer availableCopies;
    String imageUrl;

    // Dane zagnieżdżone (poziom 1 i 2)
    BookSpecsDto specs;
}

