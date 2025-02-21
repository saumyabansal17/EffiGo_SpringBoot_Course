package com.effigo.ems.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.effigo.ems.model.FinancialDocument;
import com.effigo.ems.model.Users;
import com.effigo.ems.repository.FinancialDocRepository;
import com.effigo.ems.repository.UsersRepository;
import com.effigo.ems.security.JwtUtil;
import com.effigo.ems.service.S3Service;

@RestController
public class DocumentController {

    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private FinancialDocRepository documentRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload/{uid}")
    public ResponseEntity<String> uploadDocument(@PathVariable UUID uid,
                                                 @RequestParam("file") MultipartFile file) throws IOException {
    	
    	if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
    	String email=usersRepository.findEmailByUID(uid);
        String fileKey = s3Service.uploadFile(email, file);
        
        Users user = usersRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FinancialDocument document = new FinancialDocument();
        document.setUser(user);
        document.setDoc_url(fileKey);
        document.setDoc_name(file.getOriginalFilename());

        documentRepository.save(document);

        return ResponseEntity.ok("File uploaded successfully!");
    }
    
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewDocument(@PathVariable UUID id) {
        try {
            UUID docId = documentRepository.findDocIDById(id);

            if (docId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No document found for the given ID.");
            }

            FinancialDocument document = documentRepository.findById(docId)
                    .orElse(null);

            if (document == null || document.getDoc_url() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Document not found or does not have a valid URL.");
            }

            URL viewUrl = s3Service.generateDownloadUrl(document.getDoc_url());
            return ResponseEntity.ok(viewUrl.toString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the document.");
        }
    }
    
    @GetMapping("/documents/{userId}")
    public ResponseEntity<?> getUserDocuments(@PathVariable UUID userId) {
        try {
            List<FinancialDocument> documents = documentRepository.findDocumentsByUserId(userId);

            if (documents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No documents found for the given user.");
            }

            List<Map<String, String>> documentList = documents.stream().map(doc -> {
                Map<String, String> docInfo = new HashMap<>();
                docInfo.put("id", doc.getDoc_id().toString());
                docInfo.put("name", doc.getDoc_name());
                docInfo.put("url", s3Service.generateDownloadUrl(doc.getDoc_url()).toString());
                return docInfo;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(documentList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the documents.");
        }
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<String> downloadDocument(@PathVariable UUID id) {
    	UUID docId=documentRepository.findDocIDById(id);
    	if (docId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No document found for the given ID.");
        }

        FinancialDocument document = documentRepository.findById(docId)
                .orElse(null);

        if (document == null || document.getDoc_url() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Document not found or does not have a valid URL.");
        }
        URL downloadUrl = s3Service.generateDownloadUrl(document.getDoc_url());

        return ResponseEntity.ok(downloadUrl.toString()); 
    }
}
