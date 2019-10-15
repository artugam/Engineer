package com.artur.engineer.controllers;

import com.artur.engineer.engine.exceptions.ApiException;
import com.artur.engineer.engine.managers.UserManager;
import com.artur.engineer.engine.readers.UserReader;
import com.artur.engineer.engine.views.PagedView;
import com.artur.engineer.engine.views.UserView;
import com.artur.engineer.entities.User;
import com.artur.engineer.payload.PagedResponse;
import com.artur.engineer.payload.user.UserCreate;
import com.artur.engineer.payload.user.UserCreateWithPassword;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * @author Artur Pilch <artur.pilch12@gmail.com>
 */
@RestController
@CrossOrigin
@RequestMapping(path = "/api/users")
public class UserController {

    @Autowired
    private UserReader userReader;

    @Autowired
    private UserManager userManager;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @JsonView({PagedView.class})
    public PagedResponse<User> getAll(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer records,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false, defaultValue = "") String search
    ) {
        return userReader.getAllUsers(page, records, sortField, sortDirection, search);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    @JsonView({UserView.class})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User createUser(@Valid @RequestBody UserCreateWithPassword userCreateRequest) throws ApiException {
        return userManager.create(userCreateRequest);
    }

    @PatchMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @JsonView({UserView.class})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User editUser(@PathVariable(value = "userId") Long id, @Valid @RequestBody UserCreate userCreateRequest) throws ApiException {
        return userManager.edit(id, userCreateRequest);
    }

    @DeleteMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @JsonView({UserView.class})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean deleteUser(@PathVariable(value = "userId") Long id) {
        userManager.remove(id);
        return true;
    }

    @GetMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @JsonView({UserView.class})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User getUser(@PathVariable(value = "userId") Long id) {
        return userReader.get(id);
    }

    @PostMapping(path = "/{userId}/block")
    @ResponseStatus(HttpStatus.OK)
    @JsonView({UserView.class})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User blockUser(@PathVariable(value = "userId") Long id) {
        return userManager.block(id);
    }

    @PostMapping(path = "/{userId}/unblock")
    @ResponseStatus(HttpStatus.OK)
    @JsonView({UserView.class})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User unblockUser(@PathVariable(value = "userId") Long id) {
        return userManager.unblock(id);
    }


}