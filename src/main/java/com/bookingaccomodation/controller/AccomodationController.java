package com.bookingaccomodation.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/accomodation")
public class AccomodationController {

    @GetMapping("/{id}")
    public String getMessage(@PathVariable String id) {
        return "Hello from Docker!" + id;
    }
}
