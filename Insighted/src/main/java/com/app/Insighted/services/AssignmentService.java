package com.app.Insighted.services;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.AssignmentStatus;
import com.app.Insighted.model.User;
import com.app.Insighted.repository.AssignmentRepo;
import com.app.Insighted.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AssignmentService {

    @Autowired
    private AssignmentRepo assignmentRepository;

    @Autowired
    private UserRepo userRepository;

    public List<Assignment> findAssignmentsByStudent(Long userId) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return assignmentRepository.findByStudentOrderBySubmittedAtDesc(student);
    }

    public List<Assignment> findAssignmentsByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        return assignmentRepository.findByTeacherOrderBySubmittedAtDesc(teacher);
    }

    public List<Assignment> findPendingAssignmentsForTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return assignmentRepository.findByTeacherAndStatusInOrderBySubmittedAtAsc(
                teacher, Arrays.asList(AssignmentStatus.SUBMITTED, AssignmentStatus.IN_REVIEW));
    }

    public Assignment assignToTeacher(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));


        assignment.setTeacher(teacher);
        assignment.setStatus(AssignmentStatus.IN_REVIEW);

        return assignmentRepository.save(assignment);
    }
}
