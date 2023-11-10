package com.example.rentalsv2backend.service;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingDetailsModel;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.model.UserModel;
import com.example.rentalsv2backend.utils.Page;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ListingService {
    Mono<Page<Listing>> getListings(int page, int size);

    Mono<ListingDetailsModel> getListingById(int id);

    Mono<Listing> createListing(ListingModel listingModel, int userId);

    Flux<Listing> searchListings(String query);

    Mono<Page<Listing>> getUserListings(int page, int size, int userId);

    Mono<ResponseEntity<String>> deleteListing(int userId, int listingId);

    Mono<ResponseEntity<UserModel>> getUserProfile(int userId);
}
