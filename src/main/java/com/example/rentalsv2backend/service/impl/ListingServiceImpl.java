package com.example.rentalsv2backend.service.impl;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.repository.ListingRepository;
import com.example.rentalsv2backend.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;

    @Override
    public Flux<Listing> getListings() {
        return listingRepository.findAll();
    }

    @Override
    public Mono<Listing> getListingById(int id) {
        return listingRepository.findById(id);
    }

    @Override
    public Mono<Listing> createListing(ListingModel listingModel) {
        Listing listing = new Listing();
        listing.setName(listingModel.getName());
        listing.setBeds(listingModel.getBeds());
        listing.setBaths(listingModel.getBaths());
        return listingRepository.save(listing);
    }

    @Override
    public Flux<Listing> searchListings(String query) {
        return listingRepository.searchListings(query);
    }
}
