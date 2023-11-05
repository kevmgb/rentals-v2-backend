package com.example.rentalsv2backend.auth;

import com.example.rentalsv2backend.auth.configs.Tokenizer;
import com.example.rentalsv2backend.auth.exception.AnonymousException;
import com.example.rentalsv2backend.auth.models.LoginRequest;
import com.example.rentalsv2backend.auth.models.RegisterRequest;
import com.example.rentalsv2backend.entity.User;
import com.example.rentalsv2backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Log4j2
@RestController
@RequestMapping("api/v1")
public class LoginSignup {

    private final Tokenizer tokenizer;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public LoginSignup(Tokenizer tokenizer, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenizer = tokenizer;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("login")
    public Mono<ResponseEntity<LoginRequest>> login(@RequestBody LoginRequest request) {
        // start with find requested email in DB
        return userRepository.findByEmail(request.getEmail())

                // match password
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))

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
                    return userRepository.save(entity);
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
}
