package com.app.Insighted.repository;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.AssignmentStatus;
import com.app.Insighted.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepo extends JpaRepository<Assignment, Long> {
    List<Assignment> findByStudentOrderBySubmittedAtDesc(User student);
    List<Assignment> findByTeacherOrderBySubmittedAtDesc(User teacher);
    List<Assignment> findByTeacherAndStatusInOrderBySubmittedAtAsc(User teacher, List<AssignmentStatus> statuses);
    List<Assignment> findByStatus(AssignmentStatus status);
}
