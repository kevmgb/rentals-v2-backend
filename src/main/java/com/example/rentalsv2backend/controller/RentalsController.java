package com.example.rentalsv2backend.controller;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RentalsController {
    private final ListingService listingService;

    @GetMapping("/listings")
    private Flux<Listing> getListings() {
        return listingService.getListings();
    }

    @GetMapping("/listing/{id}")
    private Mono<Listing> getListingById(@PathVariable("id") int id) {
        return listingService.getListingById(id);
    }

    @PostMapping("listing")
    private Mono<Listing> createListing(@RequestBody ListingModel listingModel) {
        return listingService.createListing(listingModel);
    }

    @GetMapping("listings/search")
    private Flux<Listing> searchListings(@RequestParam("query") String query) {
        return listingService.searchListings(query);
    }
}
