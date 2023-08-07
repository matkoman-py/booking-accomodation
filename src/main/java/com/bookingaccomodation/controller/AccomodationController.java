package com.bookingaccomodation.controller;


import com.bookingaccomodation.model.dto.AccomodationDTO;
import com.bookingaccomodation.service.AccomodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/accomodation")
@RequiredArgsConstructor
public class AccomodationController {

    private final AccomodationService accomodationService;

    @PostMapping
    public ResponseEntity<AccomodationDTO> create(@RequestBody AccomodationDTO accomodationDTO) {
        return ResponseEntity.ok(accomodationService.create(accomodationDTO));
    }

    @GetMapping
    public ResponseEntity<List<AccomodationDTO>> getAll() {
        return ResponseEntity.ok(accomodationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccomodationDTO> getOne(@PathVariable String id) {
        return ResponseEntity.ok(accomodationService.getOne(id));
    }
}
