package com.app.Insighted.repository;

import com.app.Insighted.model.Feedback;
import com.app.Insighted.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    List<Feedback> findByDocumentSectionAssignmentIdOrderByCreatedAt(Long assignmentId);
    List<Feedback> findByTeacherOrderByCreatedAtDesc(User teacher);
}
