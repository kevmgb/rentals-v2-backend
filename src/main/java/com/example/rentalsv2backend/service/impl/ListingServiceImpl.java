package com.example.rentalsv2backend.service.impl;

import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.model.ListingDetailsModel;
import com.example.rentalsv2backend.model.ListingModel;
import com.example.rentalsv2backend.model.UserModel;
import com.example.rentalsv2backend.repository.ListingRepository;
import com.example.rentalsv2backend.repository.UserRepository;
import com.example.rentalsv2backend.service.ListingService;
import com.example.rentalsv2backend.utils.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Mono<Page<Listing>> getListings(int page, int size) {
        int offset = (page - 1) * size; // Calculate the offset based on page and size

        return listingRepository.findAll()
                .skip(offset) // Skip the first 'offset' elements
                .take(size)   // Take 'size' elements after skipping
                .collectList() // Collect the paginated list
                .flatMap(list -> listingRepository.count() // Get the total count of items
                        .map(totalElements -> {
                            int totalPages = (int) Math.ceil((double) totalElements / size);
                            return new Page<>(list, totalElements, totalPages, page, size);
                        })
                );
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

    @Override
    public Mono<Page<Listing>> getUserListings(int page, int size, int userId) {
        int offset = (page - 1) * size; // Calculate the offset based on page and size

        return listingRepository.findByUserId(userId)
                .skip(offset) // Skip the first 'offset' elements
                .take(size)   // Take 'size' elements after skipping
                .collectList() // Collect the paginated list
                .flatMap(list -> listingRepository.findByUserId(userId).count() // Get the total count of items
                        .map(totalElements -> {
                            int totalPages = (int) Math.ceil((double) totalElements / size);
                            return new Page<>(list, totalElements, totalPages, page, size);
                        })
                );
    }

    @Override
    public Mono<ResponseEntity<String>> deleteListing(int userId, int listingId) {
        return listingRepository.findById(listingId)
                .flatMap(listing -> {
                    if (listing.getUserId() != userId) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorised to perform this action"));
                    }
                    return listingRepository
                            .deleteById(listingId)
                                    .then(Mono.just(ResponseEntity.ok("Listing deleted successfully")));
                }).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found")));
    }

    @Override
    public Mono<ResponseEntity<UserModel>> getUserProfile(int userId) {
        return userRepository.findById((long) userId)
                .flatMap(user -> Mono.just(ResponseEntity.ok(new UserModel(user.getName(), user.getEmail()))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)));
    }

    @Override
    public Mono<ResponseEntity<UserModel>> updateUserProfile(int userId, UserModel user) {
        return userRepository.findById((long) userId)
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    return userRepository.save(existingUser)
                            .map(savedUser -> ResponseEntity.ok(new UserModel(savedUser.getName(), savedUser.getEmail())));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)));
    }

}
