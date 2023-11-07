package com.example.rentalsv2backend.auth;

import com.example.rentalsv2backend.auth.configs.Tokenizer;
import com.example.rentalsv2backend.auth.exception.AnonymousException;
import com.example.rentalsv2backend.auth.models.LoginRequest;
import com.example.rentalsv2backend.auth.models.RegisterRequest;
import com.example.rentalsv2backend.entity.Listing;
import com.example.rentalsv2backend.entity.User;
import com.example.rentalsv2backend.entity.UserVerificationToken;
import com.example.rentalsv2backend.repository.UserRepository;
import com.example.rentalsv2backend.repository.UserSignupTokenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Log4j2
@RestController
@RequestMapping("api/v1")
public class LoginSignup {

    private final Tokenizer tokenizer;

    private final UserRepository userRepository;
    private final UserSignupTokenRepository userSignupTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginSignup(Tokenizer tokenizer, UserRepository userRepository, UserSignupTokenRepository userSignupTokenRepository, PasswordEncoder passwordEncoder) {
        this.tokenizer = tokenizer;
        this.userRepository = userRepository;
        this.userSignupTokenRepository = userSignupTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public Mono<ResponseEntity<LoginRequest>> login(@RequestBody LoginRequest request) {
        // start with find requested email in DB
        return userRepository.findByEmail(request.getEmail())

                // match password
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .filter(user -> user.getActive().equals(Boolean.TRUE))

                // transform to user id
                .map(User::getId)

                // map as desired spec and generate token (JWT)
                .map(userId -> {
                    LoginRequest response = new LoginRequest();
                    response.setToken(tokenizer.tokenize(Long.toString(userId)));
                    return ResponseEntity.ok(response);
                })

                // fail to log in? mark as unauthorized.
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("register")
    public Mono<ResponseEntity<Void>> register(@RequestBody RegisterRequest request) {
        // find this email in DB
        return userRepository.findByEmail(request.getEmail())

                // default as empty User of doesn't exist in DB
                .defaultIfEmpty(new User())

                // check User object before register user
                .flatMap((Function<User, Mono<User>>) user -> {
                    if (user.getId() != null) {
                        // can't register using requested email because the email is already exists in DB.
                        // so, we return empty Mono to be handled in next operation
                        return Mono.empty();
                    }

                    // ready to create new user from requested information
                    // generate new password
                    final String password = Long.toString(System.currentTimeMillis());


                    log.info(request.getEmail() + " / " + password);

                    // draft new entity
                    User entity = new User();
                    entity.setEmail(request.getEmail());
                    entity.setName(request.getName());
                    entity.setPassword(passwordEncoder.encode(request.getPassword()));

                    // save entity, any error will be handled in onErrorResume
                    Mono<User> userMono = userRepository.save(entity);

                    return userMono.flatMap(savedUser -> {
                        UserVerificationToken userVerificationToken = new UserVerificationToken();
                        userVerificationToken.setUserId(savedUser.getId());
                        userVerificationToken.setToken(String.valueOf(UUID.randomUUID()));
                        userVerificationToken.setExpiry(LocalDateTime.now().plusDays(1));
                        return userSignupTokenRepository.save(userVerificationToken)
                                .thenReturn(savedUser);
                    });
                })

                // in case that we can save User, return HTTP 201
                .map(new Function<User, ResponseEntity<Void>>() {
                    @Override
                    public ResponseEntity<Void> apply(User user) {
                        return ResponseEntity.status(HttpStatus.CREATED).build();
                    }
                })

                // in case that we got empty Mono from previous operation, which mean requested email is already exists in DB.
                // so, throw an exception for duplicated email
                .switchIfEmpty(Mono.error(AnonymousException.registerDuplicatedEmail()))

                // handle any other exception like SQLException and Bad SQL Grammar Exception
                .onErrorResume(Mono::error);
    }

    @GetMapping("/token/verify/{token}")
    private Mono<ResponseEntity<String>> verifyToken(@PathVariable("token") String token) {
        return userSignupTokenRepository.findByToken(token)
                .flatMap(signupToken -> {
                    // Verify that token has not expired
                    if (LocalDateTime.now().isAfter(signupToken.getExpiry())) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification token has expired"));
                    }

                    // Check if user exists
                    return userRepository.findById(signupToken.getUserId())
                            .flatMap(user -> {
                                // Mark user as active
                                if (user.getActive().equals(Boolean.TRUE)) {
                                    return Mono.just(ResponseEntity.ok("User is already activated"));
                                }
                                user.setActive(Boolean.TRUE);
                                return userRepository.save(user)
                                        .thenReturn(ResponseEntity.ok("User has been successfully verified"));
                            }).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Verification token not found")));
    }
}
