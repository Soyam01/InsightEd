package com.app.Insighted.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    private Long sectionId;
    private String comment;
    private String highlightedText;
}
