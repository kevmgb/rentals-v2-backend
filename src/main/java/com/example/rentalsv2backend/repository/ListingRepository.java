package com.example.rentalsv2backend.repository;

import com.example.rentalsv2backend.entity.Listing;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ListingRepository extends R2dbcRepository<Listing, Integer> {

    @Query("SELECT id, name, beds, baths FROM listings WHERE MATCH (name) AGAINST (:query IN NATURAL LANGUAGE MODE)")
    Flux<Listing> searchListings(String query);

    Flux<Listing> findByUserId(int userId);
}
