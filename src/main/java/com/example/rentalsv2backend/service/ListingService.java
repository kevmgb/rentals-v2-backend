package com.example.rentalsv2backend.service;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingDetailsModel;
import com.example.rentalsv2backend.model.ListingModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ListingService {
    Flux<Listing> getListings();

    Mono<ListingDetailsModel> getListingById(int id);

    Mono<Listing> createListing(ListingModel listingModel, int userId);

    Flux<Listing> searchListings(String query);
}
