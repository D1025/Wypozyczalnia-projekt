package com.projekt.wypozyczalnia.dto.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "DemoItem")
public class DemoItemDto {

    String type;
    String title;

    DemoMemberRefDto member;
    DemoBookRefDto book;

    Boolean active;
    String imageUrl;
}

