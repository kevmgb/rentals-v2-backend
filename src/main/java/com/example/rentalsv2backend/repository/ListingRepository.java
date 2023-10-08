package com.example.rentalsv2backend.repository;

import com.example.rentalsv2backend.entity.Listing;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends R2dbcRepository<Listing, Integer> {
}
