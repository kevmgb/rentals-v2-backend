package com.example.rentalsv2backend.repository;

import com.example.rentalsv2backend.entity.User;
import com.example.rentalsv2backend.entity.UserVerificationToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserSignupTokenRepository extends ReactiveCrudRepository<UserVerificationToken, Long> {
}
