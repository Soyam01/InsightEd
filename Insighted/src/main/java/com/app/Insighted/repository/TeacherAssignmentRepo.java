package com.app.Insighted.repository;

import com.app.Insighted.model.TeacherAssignment;
import com.app.Insighted.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeacherAssignmentRepo extends JpaRepository<TeacherAssignment, Long> {

    List<TeacherAssignment> findTeacherAssignmentByTeacher(User user);

    @Query("SELECT COUNT(ta) FROM TeacherAssignment ta WHERE ta.teacher = :teacher AND ta.dueDate BETWEEN :startOfWeek AND :endOfWeek")
    Long countAssignmentsDueThisWeek(@Param("teacher") User teacher,
                                     @Param("startOfWeek") LocalDateTime startOfWeek,
                                     @Param("endOfWeek") LocalDateTime endOfWeek);
}
