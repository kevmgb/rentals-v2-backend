package com.example.rentalsv2backend.controller;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RentalsController {
    private final ListingService listingService;

    @GetMapping("/listings")
    private Flux<Listing> getListings() {
        return listingService.getListings();
    }
}
