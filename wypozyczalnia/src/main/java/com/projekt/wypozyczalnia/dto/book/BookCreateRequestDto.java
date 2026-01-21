package com.projekt.wypozyczalnia.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "BookCreateRequest")
public class BookCreateRequestDto {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 255)
    private String author;

    @NotBlank
    @Size(max = 20)
    private String isbn;

    private Integer publishedYear;

    private String genre;

    private String description;

    @NotNull
    @Min(1)
    private Integer totalCopies;

    @NotNull
    @Min(0)
    private Integer availableCopies;

    @Size(max = 1024)
    private String imageUrl;
}
