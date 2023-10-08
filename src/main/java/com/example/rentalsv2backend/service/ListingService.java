package com.example.rentalsv2backend.service;

import com.example.rentalsv2backend.entity.Listing;
import reactor.core.publisher.Flux;

public interface ListingService {
    Flux<Listing> getListings();
}
