package com.match.making.controller;

import com.match.making.entity.payload.UserPayload;
import com.match.making.facade.UserFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserFacade facade;

    public UserController(final UserFacade facade) {
        this.facade = facade;
    }

    @PostMapping("/users")
    public ResponseEntity<Void> registerUserInMatchPool(@RequestBody final UserPayload user) {
        facade.registerUser(user);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/users/generate/{count}")
    public ResponseEntity<Void> registerUserInMatchPool(@PathVariable final int count) {
        facade.registerGeneratedUsers(count);

        return ResponseEntity
                .noContent()
                .build();
    }
}
