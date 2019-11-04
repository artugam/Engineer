package com.artur.engineer.repositories;

import com.artur.engineer.entities.Course;
import com.artur.engineer.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {

    Optional<Course> findById(Long id);

    Page<Course> findByNameContainingOrFormContainingOrDegreeContaining(
            Pageable pageable,
            String nameSearch,
            String form,
            String degree
    );
//    Page<Course> findByNameContainingOrFormContainingOrDegreeContainingOrSemestersContainingOrStartDateContaining(
//            Pageable pageable,
//            String nameSearch,
//            String form,
//            String degree,
//            String semesters,
//            String startDate
//    );

//    @Query("select article from Article article left join fetch article.topics where article.id =:id")
//    Article findStudentsByCourseId(@Param("id") Long id);

    void deleteById(Long id);

    @Query("DELETE FROM Course o WHERE o.id = :id AND o.users IN :students")
    void deleteStudentsFromCourse(@Param("id") Long id, @Param("students") List<User> students);

    Collection<Course> findAllByIdIn(Collection<Long> ids);
}
