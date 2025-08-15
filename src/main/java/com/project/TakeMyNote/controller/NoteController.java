package com.project.TakeMyNote.controller;

import com.project.TakeMyNote.model.NoteMetaData;
import com.project.TakeMyNote.service.GrammarService;
import com.project.TakeMyNote.service.NoteStorageService;
import com.project.TakeMyNote.service.RenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteStorageService storage;
    private final RenderService renderService;
    private final GrammarService grammarService;

    public NoteController(NoteStorageService storage, RenderService renderService, GrammarService grammarService) {
        this.storage = storage;
        this.renderService = renderService;
        this.grammarService = grammarService;
    }

    // 1) Grammar check (text)
    @PostMapping(value = "/grammar", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> checkGrammarText(@RequestBody String markdown) throws IOException {
        if(markdown == null || markdown.isBlank()){
            return ResponseEntity.badRequest().body(Map.of("error", "Markdown text cannot be empty"));
        }
        var suggestions = grammarService.check(markdown);
        return ResponseEntity.ok(suggestions);
    }

    // 1b) Grammar check - uploaded .md file
    @PostMapping(value = "/grammar-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> checkGrammarFile(@RequestParam("file") MultipartFile file) throws IOException {
        if(file.isEmpty() || !file.getOriginalFilename().endsWith(".md") ) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload a valid .md file"));
        }
        String md = new String(file.getBytes(), StandardCharsets.UTF_8);
        var suggestions = grammarService.check(md);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/{id}/grammar")
    public ResponseEntity<?> grammarCheckNoteById(@PathVariable String id) throws IOException {
        // read the file from disk
        Optional<String> raw = storage.readRaw(id);
        if (raw.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Note not found"));
        }

        // run grammar check
        var suggestions = grammarService.check(raw.get());
        return ResponseEntity.ok(suggestions);
    }

    // 2) Save note (body JSON)
    public static class SaveNoteRequest {
        public String title;
        public String markdown;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveNote(@RequestBody SaveNoteRequest req) throws IOException {
        try {
            NoteMetaData saved = storage.saveMarkdown(req.title, req.markdown, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", saved.getId()));
        }
        catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Note not found"));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2b) Upload an .md file
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".md")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload a valid .md file"));
        }
        String md = new String(file.getBytes(), StandardCharsets.UTF_8);
        String original = file.getOriginalFilename();
        String title = original != null ? original : "";
        try {
            NoteMetaData saved = storage.saveMarkdown(title, md, original);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", saved.getId()));
        }
        catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Note not found"));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3) List notes
    @GetMapping
    public ResponseEntity<?> list() throws IOException {
        List<NoteMetaData> list = storage.listNotes();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    // 4) Rendered HTML
    @GetMapping(value = "/{id}/render", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> render(@PathVariable String id) throws IOException {
        Optional<String> raw = storage.readRaw(id);
        if (raw.isEmpty()) return ResponseEntity.notFound().build();
        String html = renderService.renderAndSanitize(raw.get());
        return ResponseEntity.ok(html);
    }

    // Raw markdown
    @GetMapping(value = "/{id}/raw", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> raw(@PathVariable String id) throws IOException {
        return storage.readRaw(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable String id, @RequestBody SaveNoteRequest req) throws IOException {
        try {
            NoteMetaData updated = storage.updateMarkdown(id, req.title, req.markdown);
            return ResponseEntity.ok(updated);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable String id) throws IOException {
        boolean deleted = storage.deleteNote(id);
        if (deleted) {
            return ResponseEntity.ok("Note Deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

