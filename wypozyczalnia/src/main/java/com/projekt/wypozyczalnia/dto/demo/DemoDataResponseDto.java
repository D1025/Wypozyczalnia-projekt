package com.projekt.wypozyczalnia.dto.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "DemoData")
public class DemoDataResponseDto {

    String timestamp;

    @Singular("book")
    List<DemoBookDto> books;
}

