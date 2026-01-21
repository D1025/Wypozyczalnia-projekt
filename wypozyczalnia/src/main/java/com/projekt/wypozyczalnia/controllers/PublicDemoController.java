package com.projekt.wypozyczalnia.controllers;

import com.projekt.wypozyczalnia.dto.demo.DemoDataResponseDto;
import com.projekt.wypozyczalnia.services.PublicDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicDemoController {

    private final PublicDemoService publicDemoService;

    public PublicDemoController(PublicDemoService publicDemoService) {
        this.publicDemoService = publicDemoService;
    }

    /**
     * Public (no-auth) endpoint returning 10 kinds of data.
     * Uses REAL DB data (books/members/loans) but keeps the same JSON structure.
     * Records include image links via imageUrl fields (may be empty if not provided yet).
     */
    @GetMapping(value = "/demo-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DemoDataResponseDto> demoData() {
        try {
            DemoDataResponseDto dto = publicDemoService.getDemoData();
            // jawne logowanie, żeby upewnić się, że serwis zwrócił obiekt
            log.info("Demo data retrieved successfully: timestamp={}, books={}", dto.getTimestamp(), dto.getBooks().size());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error generating demo data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
