package com.example.rentalsv2backend.service.impl;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingDetailsModel;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.repository.ListingRepository;
import com.example.rentalsv2backend.repository.UserRepository;
import com.example.rentalsv2backend.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    @Override
    public Flux<Listing> getListings() {
        return listingRepository.findAll();
    }

    @Override
    public Mono<ListingDetailsModel> getListingById(int id) {
        return listingRepository.findById(id)
                .flatMap(listing -> userRepository
                        .findById(Long.valueOf(listing.getUserId()))
                        .flatMap(user -> {
                            ListingDetailsModel listingDetailsModel = new ListingDetailsModel();
                            listingDetailsModel.setName(listing.getName());
                            listingDetailsModel.setBeds(listing.getBeds());
                            listingDetailsModel.setBaths(listing.getBaths());
                            listingDetailsModel.setDescription(listing.getDescription());
                            listingDetailsModel.setUserName(user.getName());
                            listingDetailsModel.setContact(user.getEmail());
                            return Mono.just(listingDetailsModel);
                        }).switchIfEmpty(Mono.just(new ListingDetailsModel()))).switchIfEmpty(Mono.just(new ListingDetailsModel()));
    }

    @Override
    public Mono<Listing> createListing(ListingModel listingModel, int userId) {
        Listing listing = new Listing();
        listing.setName(listingModel.getName());
        listing.setBeds(listingModel.getBeds());
        listing.setBaths(listingModel.getBaths());
        listing.setUserId(userId);
        listing.setDescription(listingModel.getDescription());
        return listingRepository.save(listing);
    }

    @Override
    public Flux<Listing> searchListings(String query) {
        return listingRepository.searchListings(query);
    }
}
