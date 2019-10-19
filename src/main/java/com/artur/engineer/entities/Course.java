package com.artur.engineer.entities;

import com.artur.engineer.engine.views.CourseView;
import com.artur.engineer.engine.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "form", "degree", "startDate"})
})
public class Course extends BaseEntity {

    public static final String FORM_STATIC = "Stacjonarne";
    public static final String FORM_UNSTATIC = "Niestacjonarne";

    public static final String ALLOWED_FORMS[] = {
            FORM_STATIC,
            FORM_UNSTATIC
    };

    public static final String DEGREE_I = "I Stopnia";
    public static final String DEGREE_II = "II Stopnia";
    public static final String DEGREE_III = "III Stopnia";

    public static final String ALLOWED_DEGREES[] = {
            DEGREE_I,
            DEGREE_II,
            DEGREE_III
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(CourseView.class)
    private Long id;

    @JsonView(CourseView.class)
    private String name;

    @JsonView(CourseView.class)
    private String form;

    @JsonView(CourseView.class)
    private String degree;

    @JsonView(CourseView.class)
    private int semesters;

    @JsonView(CourseView.class)
    private Date startDate = new Date();

    @JsonView(CourseView.class)
    private int currentSemester;

    @ManyToMany(mappedBy = "courses")
    private Collection<User> users = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Collection<Subject> subjects;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getSemesters() {
        return semesters;
    }

    public void setSemesters(int semesters) {
        this.semesters = semesters;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(int currentSemester) {
        this.currentSemester = currentSemester;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public Collection<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Collection<Subject> subjects) {
        this.subjects = subjects;
    }
}
