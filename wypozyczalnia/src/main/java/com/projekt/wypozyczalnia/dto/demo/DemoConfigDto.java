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
@JacksonXmlRootElement(localName = "DemoConfig")
public class DemoConfigDto {
    String apiVersion;

    @Singular("contentType")
    List<String> contentTypes;

    @Singular("auth")
    List<String> auth;

    String note;
    boolean hasAnyImage;
}

