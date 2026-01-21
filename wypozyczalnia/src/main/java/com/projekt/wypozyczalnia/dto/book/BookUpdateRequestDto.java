package com.projekt.wypozyczalnia.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "BookUpdateRequest")
public class BookUpdateRequestDto {

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String author;

    @Size(max = 20)
    private String isbn;

    private Integer publishedYear;

    private String genre;

    private String description;

    @Min(1)
    private Integer totalCopies;

    @Min(0)
    private Integer availableCopies;

    @Size(max = 1024)
    private String imageUrl;
}
