package com.app.Insighted.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assignmentTitle;
    private String course;
    private String assignmentType;
    private int totalPoints;
    private LocalDateTime dueDate;
    private String assignmentDescription;

    @ManyToOne
    @JoinColumn(name= "teacher_id")
    private User teacher;

}

