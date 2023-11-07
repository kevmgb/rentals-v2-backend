package com.example.rentalsv2backend.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingDetailsModel;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

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
    private Mono<ListingDetailsModel> getListingById(@PathVariable("id") int id) {
        return listingService.getListingById(id);
    }

    @PostMapping("listing")
    private Mono<Listing> createListing(@RequestHeader Map<String, String> headers, @RequestBody ListingModel listingModel) {
        String token = headers.get("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        int userId = Integer.parseInt(jwt.getClaim("userId").asString());

        System.out.println(token);
        System.out.println(userId);

        return listingService.createListing(listingModel, userId);
    }

    @GetMapping("listings/search")
    private Flux<Listing> searchListings(@RequestParam("query") String query) {
        return listingService.searchListings(query);
    }
}
