package com.project.TakeMyNote.model;
import lombok.Data;

import java.time.Instant;

@Data
public class NoteMetaData {
    private String id;
    private String title;
    private String filename;      // e.g., "f3c1b8c2-....md"
    private String originalFilename; // optional: name from upload
    private Instant createdAt;
    private Instant updatedAt;

    public NoteMetaData() {
    }

    public NoteMetaData(String id, String title, String filename, String originalFilename, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

