package com.artur.engineer.controllers;

import com.artur.engineer.engine.managers.CourseGroupManager;
import com.artur.engineer.engine.managers.CourseManager;
import com.artur.engineer.engine.readers.CourseGroupReader;
import com.artur.engineer.engine.readers.CoursesReader;
import com.artur.engineer.engine.readers.UserReader;
import com.artur.engineer.engine.views.*;
import com.artur.engineer.entities.Course;
import com.artur.engineer.entities.CourseGroup;
import com.artur.engineer.entities.User;
import com.artur.engineer.payload.ApiResponse;
import com.artur.engineer.payload.PagedResponse;
import com.artur.engineer.payload.course.CourseConfigurationResponse;
import com.artur.engineer.payload.course.CourseCreate;
import com.artur.engineer.payload.course.StudentsIds;
import com.artur.engineer.payload.groups.GroupCreatePayload;
import com.artur.engineer.payload.user.UserIdPayload;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Artur Pilch <artur.pilch12@gmail.com>
 */
@RestController
@CrossOrigin
@RequestMapping(path = "/api/groups")
public class CourseGroupController {

    @Autowired
    private CourseGroupReader reader;

    @Autowired
    private CourseGroupManager manager;


    @PostMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @JsonView({CourseView.class})
    public CourseGroup create(@Valid @RequestBody GroupCreatePayload payload) {
        return manager.create(payload);
    }

    @PatchMapping(path = "/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @JsonView({CourseView.class})
    public CourseGroup edit(
            @PathVariable(value = "groupId") Long id,
            @Valid @RequestBody GroupCreatePayload payload
    ) {
        return manager.edit(id, payload);
    }

    @DeleteMapping(path = "/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @JsonView({CourseView.class})
    public ApiResponse delete(
            @PathVariable(value = "groupId") Long id
    ) {
        manager.remove(id);

        return new ApiResponse(true, "Grupa została usunięta");
    }

    @PostMapping(path = "/{groupId}/students")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    @JsonView({CourseView.class})
    public CourseGroup delete(
            @PathVariable(value = "groupId") Long id,
            @Valid @RequestBody StudentsIds payload
    ) {
        return manager.setStudents(id, payload);
    }
}
