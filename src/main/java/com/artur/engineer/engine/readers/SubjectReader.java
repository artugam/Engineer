package com.artur.engineer.engine.readers;

import com.artur.engineer.entities.*;
import com.artur.engineer.payload.PagedResponse;
import com.artur.engineer.payload.subject.SubjectConfigurationOptions;
import com.artur.engineer.payload.subjectSchedule.FullScheduleResponse;
import com.artur.engineer.payload.subjectSchedule.FullScheduleResponseRow;
import com.artur.engineer.payload.subjectSchedule.grades.FullGradeResponseRow;
import com.artur.engineer.payload.subjectSchedule.grades.FullGradesResponse;
import com.artur.engineer.repositories.GradeRepository;
import com.artur.engineer.repositories.SubjectRepository;
import com.artur.engineer.repositories.SubjectScheduleRepository;
import com.artur.engineer.repositories.UserRepository;
import com.artur.engineer.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotFoundException;
import java.util.*;


/**
 * @author Artur Pilch <artur.pilch12@gmail.com>
 */
@Component("SubjectReader")
public class SubjectReader {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository repository;

    @Autowired
    private CourseGroupReader groupReader;

    @Autowired
    private SubjectReader subjectReader;

    @Autowired
    private SubjectScheduleRepository subjectScheduleRepository;

    @Autowired
    private GradeRepository gradeRepository;

    public Subject get(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Subject not found with id : " + id)
        );
    }

    public PagedResponse<Subject> getSubjects(Long courseGroupId, int page, int size, String sortField, String direction, String search) {

        Sort.Direction chooseDirection = Sort.Direction.ASC;
        if (direction.equals("DESC")) {
            chooseDirection = Sort.Direction.DESC;
        }

        Page<Subject> query = subjectRepository.findAllByCourseGroupId(
                courseGroupId,
                search,
                PageRequest.of(page - 1, size, Sort.by(chooseDirection, sortField))
        );

        return new PagedResponse<>(query);
    }

    public Collection<Subject> getSubjects(Long courseGroupId) {
        return subjectRepository.findAllByGroup(groupReader.get(courseGroupId));
    }

    public SubjectConfigurationOptions getConfiguration() {
        SubjectConfigurationOptions config = new SubjectConfigurationOptions();

        Collection<User> users = userRepository.findAllByRoleName(Role.ROLE_TEACHER);

        config.setTeachers(users);
        config.setTypes(Subject.ALLOWED_SUBJECT_TYPES);

        return config;
    }

    public PagedResponse<SubjectSchedule> getSubjectSchedule(Long subjectId, int page, int size, String sortField, String direction, String search) {
        Sort.Direction chooseDirection = Sort.Direction.ASC;
        if (direction.equals("DESC")) {
            chooseDirection = Sort.Direction.DESC;
        }

        Page<SubjectSchedule> query = subjectScheduleRepository.findAllBySubjectId(
                subjectId,
                PageRequest.of(page - 1, size, Sort.by(chooseDirection, sortField))
        );


        return new PagedResponse<>(query);
    }

    public FullScheduleResponse getSubjectFullSchedule(Long subjectId) {

        Subject subject = subjectReader.get(subjectId);

        Collection<SubjectSchedule> schedules = subjectScheduleRepository.findAllBySubject(subject, Sort.by(Sort.Direction.ASC, "start"));

        FullScheduleResponse response = new FullScheduleResponse();
        response.schedules = schedules;

        Collection<SubjectSchedule> schedulesToSave = new ArrayList<>();

        Collection<User> users = userRepository.findAllByCourseGroupId(subject.getGroup().getId(), Sort.by(Sort.Direction.ASC, "lastName"));

        for (User user : users) {
            FullScheduleResponseRow row = new FullScheduleResponseRow();
            row.user = user;

            for (SubjectSchedule schedule : schedules) {
                SubjectPresence userPresence = schedule.getUserPresence(user);
                if (userPresence == null) {
                    userPresence = new SubjectPresence();
                    userPresence.setUser(user);
                    schedule.addPresence(userPresence);
                    schedulesToSave.add(schedule);
                }
                row.presences.add(userPresence);
            }
            response.rows.add(row);
        }

        subjectScheduleRepository.saveAll(schedulesToSave);


        return response;
    }

    public FullGradesResponse getSubjectFullGrades(Long subjectId) {

        Subject subject = subjectReader.get(subjectId);

        List<String> descriptions = gradeRepository.findDistinctDescriptionBySubject(subject, Sort.by(Sort.Direction.ASC, "createdAt"));

        FullGradesResponse response = new FullGradesResponse();
        response.sections = descriptions;

        Collection<Grade> gradesToSave = new ArrayList<>();

        Collection<User> users = userRepository.findAllByCourseGroupId(subject.getGroup().getId(), Sort.by(Sort.Direction.ASC, "lastName"));

        for (User user : users) {
            FullGradeResponseRow row = new FullGradeResponseRow();
            row.user = user;

            for (String description : descriptions) {

                Grade grade = user.getGrade(subject, description);
                if (grade == null) {
                    grade = new Grade();
                    grade.setUser(user);
                    grade.setSubject(subject);

                    gradesToSave.add(grade);
                }
                row.grades.add(grade);
            }
            response.rows.add(row);
        }

        gradeRepository.saveAll(gradesToSave);


        return response;
    }
}
