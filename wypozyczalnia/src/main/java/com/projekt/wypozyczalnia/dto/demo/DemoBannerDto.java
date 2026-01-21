package com.projekt.wypozyczalnia.dto.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "DemoBanner")
public class DemoBannerDto {
    String title;
    String imageUrl;
}

