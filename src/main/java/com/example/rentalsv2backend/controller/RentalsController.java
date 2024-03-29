package com.example.rentalsv2backend.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingDetailsModel;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.model.UserModel;
import com.example.rentalsv2backend.service.ListingService;
import com.example.rentalsv2backend.utils.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RentalsController {
    private final ListingService listingService;

    @GetMapping("/listings")
    private Mono<Page<Listing>> getListings(@RequestParam int page, @RequestParam int size) {
        return listingService.getListings(page, size);
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

    @GetMapping("user/listings")
    private Mono<Page<Listing>> getUserListings(@RequestHeader Map<String, String> headers, @RequestParam int page, @RequestParam int size) {
        String token = headers.get("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        int userId = Integer.parseInt(jwt.getClaim("userId").asString());
        return listingService.getUserListings(page, size, userId);
    }

    @DeleteMapping("listing/delete/{id}")
    private Mono<ResponseEntity<String>> deleteListing(@RequestHeader Map<String, String> headers, @PathVariable("id") int listingId) {
        String token = headers.get("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        int userId = Integer.parseInt(jwt.getClaim("userId").asString());
        return listingService.deleteListing(userId, listingId);
    }

    @GetMapping("user")
    private Mono<ResponseEntity<UserModel>>getUserProfile(@RequestHeader Map<String, String> headers) {
        String token = headers.get("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        int userId = Integer.parseInt(jwt.getClaim("userId").asString());
        return listingService.getUserProfile(userId);
    }

    @PutMapping("user")
    private Mono<ResponseEntity<UserModel>>updateUserProfile(@RequestHeader Map<String, String> headers, @RequestBody UserModel user) {
        String token = headers.get("Authorization").substring(7);
        DecodedJWT jwt = JWT.decode(token);
        int userId = Integer.parseInt(jwt.getClaim("userId").asString());
        return listingService.updateUserProfile(userId, user);
    }
 }
