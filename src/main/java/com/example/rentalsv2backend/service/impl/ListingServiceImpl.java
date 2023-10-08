package com.example.rentalsv2backend.service.impl;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.repository.ListingRepository;
import com.example.rentalsv2backend.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;

    @Override
    public Flux<Listing> getListings() {
        return listingRepository.findAll();
    }
}
