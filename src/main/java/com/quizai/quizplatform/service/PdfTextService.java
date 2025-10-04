package com.quizai.quizplatform.service;

import com.quizai.quizplatform.core.util.TextCleaner;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class PdfTextService {

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    public String extractCleanCapped(String storedRelativePath, int maxTokens) {

        Path path = Path.of(uploadsDir).resolve(storedRelativePath);

        try (PDDocument doc = PDDocument.load(new File(path.toString()))) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String raw = stripper.getText(doc);
            String clean = TextCleaner.clean(raw);

            return TextCleaner.capByTokens(clean, maxTokens);

        } catch (Exception e) {

            throw new RuntimeException("Failed to read PDF: " + e.getMessage(), e);

        }
    }
}
