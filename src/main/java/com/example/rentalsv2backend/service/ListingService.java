package com.example.rentalsv2backend.service;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingDetailsModel;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.utils.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ListingService {
    Mono<Page<Listing>> getListings(int page, int size);

    Mono<ListingDetailsModel> getListingById(int id);

    Mono<Listing> createListing(ListingModel listingModel, int userId);

    Flux<Listing> searchListings(String query);
}
