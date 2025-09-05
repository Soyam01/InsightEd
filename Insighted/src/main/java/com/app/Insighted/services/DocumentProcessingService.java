package com.app.Insighted.services;

import com.app.Insighted.model.Assignment;
import com.app.Insighted.model.DocumentSection;
import com.app.Insighted.repository.AssignmentRepo;
import jakarta.transaction.Transactional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentProcessingService {

    @Autowired
    private AssignmentRepo assignmentRepo;

    public Assignment processUploadedDocument(MultipartFile file, Assignment assignment) throws IOException {
        // Save file locally
        String filePath = saveFile(file);
        assignment.setFilePath(filePath);
        assignment.setOriginalFileName(file.getOriginalFilename());
        assignment.setFileSize(file.getSize());
        assignment.setMimeType(file.getContentType());

        // Extract text from PDF
        List<String> paragraphs = extractTextFromPDF(filePath);

        // Create document sections
        List<DocumentSection> sections = new ArrayList<>();
        for (int i = 0; i < paragraphs.size(); i++) {
            DocumentSection section = new DocumentSection();
            section.setSectionNumber(i + 1);
            section.setContent(paragraphs.get(i));
            section.setAssignment(assignment);
            sections.add(section);
        }

        assignment.setSections(sections);
        return assignmentRepo.save(assignment);
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/assignments/" + LocalDate.now().getYear() + "/"
                + LocalDate.now().getMonthValue() + "/";
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

//    private List<String> extractTextFromPDF(String filePath) throws IOException {
//        try (PDDocument document = PDDocument.load(new File(filePath))) {
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//
//            // Split into paragraphs and clean up
//            return Arrays.stream(text.split("\n\s*\n"))
//                    .map(String::trim)
//                    .filter(s -> !s.isEmpty() && s.length() > 20) // Filter out very short segments
//                    .collect(Collectors.toList());
//        }
//    }
private List<String> extractTextFromPDF(String filePath) throws IOException {
    try (PDDocument document = PDDocument.load(new File(filePath))) {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        // Normalize line breaks
        text = text.replaceAll("\r\n", "\n").replaceAll("\r", "\n");

        // First try splitting by double line breaks (true paragraphs)
        String[] rawParagraphs = text.split("\\n\\s*\\n");

        List<String> paragraphs = new ArrayList<>();
        for (String para : rawParagraphs) {
            para = para.trim();
            if (!para.isEmpty()) {
                // If paragraph is still very long (likely all lines joined), split further by single newlines
                if (para.length() > 500 && para.contains("\n")) {
                    paragraphs.addAll(Arrays.stream(para.split("\\n"))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty() && s.length() > 20)
                            .collect(Collectors.toList()));
                } else {
                    paragraphs.add(para);
                }
            }
        }
        return paragraphs;
    }
}

}
