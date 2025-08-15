package com.project.TakeMyNote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.TakeMyNote.model.NoteMetaData;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NoteStorageService {

    private final Path root;
    private final ObjectMapper mapper = new ObjectMapper()
            .findAndRegisterModules(); // to handle Instant

    public NoteStorageService(Path notesRoot) {
        this.root = notesRoot;
    }

    private Path mdPath(String id) { return root.resolve(id + ".md"); }
    private Path metaPath(String id) { return root.resolve(id + ".json"); }

    public NoteMetaData saveMarkdown(String title, String markdown, String originalFilename) throws IOException {
        String id = UUID.randomUUID().toString();
        Path tmp = Files.createTempFile(root, "note-", ".tmp");
        try {
            // write content to temp then move (atomic on same filesystem)
            Files.writeString(tmp, markdown == null ? "" : markdown, StandardOpenOption.TRUNCATE_EXISTING);
            Path destMd = mdPath(id);
            Files.move(tmp, destMd, StandardCopyOption.ATOMIC_MOVE);

            NoteMetaData meta = new NoteMetaData();
            meta.setId(id);
            meta.setTitle(title == null ? "" : title);
            meta.setFilename(destMd.getFileName().toString());
            meta.setOriginalFilename(originalFilename);
            Instant now = Instant.now();
            meta.setCreatedAt(now);
            meta.setUpdatedAt(now);

            // write metadata'
            Files.writeString(metaPath(id), mapper.writeValueAsString(meta), StandardOpenOption.CREATE_NEW);
            return meta;
        } finally {
            // cleanup temp if it still exists
            try { Files.deleteIfExists(tmp); } catch (IOException ignored) {}
        }
    }

    public Optional<String> readRaw(String id) throws IOException {
        Path p = mdPath(id);
        if (!Files.exists(p)) return Optional.empty();
        return Optional.of(Files.readString(p));
    }

    public Optional<NoteMetaData> readMetadata(String id) throws IOException {
        Path p = metaPath(id);
        if (!Files.exists(p)) return Optional.empty();
        return Optional.of(mapper.readValue(Files.readString(p), NoteMetaData.class));
    }

    public List<NoteMetaData> listNotes() throws IOException {
        try (Stream<Path> s = Files.list(root)) {
            return s.filter(p -> p.toString().endsWith(".json"))
                    .map(p -> {
                        try {
                            return mapper.readValue(Files.readString(p), NoteMetaData.class);
                        } catch (IOException e) { return null; }
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(NoteMetaData::getUpdatedAt).reversed())
                    .collect(Collectors.toList());
        }
    }

    public NoteMetaData updateMarkdown(String id, String title, String markdown) throws IOException {
        Path mdFile = mdPath(id);
        Path metaFile = metaPath(id);

        if (!Files.exists(mdFile) || !Files.exists(metaFile)) {
            throw new FileNotFoundException("Note not found: " + id);
        }

        // overwrite markdown
        Files.writeString(mdFile, markdown == null ? "" : markdown, StandardOpenOption.TRUNCATE_EXISTING);

        // update metadata
        NoteMetaData meta = mapper.readValue(Files.readString(metaFile), NoteMetaData.class);
        if (title != null) meta.setTitle(title);
        meta.setUpdatedAt(Instant.now());

        Files.writeString(metaFile, mapper.writeValueAsString(meta), StandardOpenOption.TRUNCATE_EXISTING);

        return meta;
    }

    public boolean deleteNote(String id) throws IOException {
        Path mdFile = mdPath(id);
        Path metaFile = metaPath(id);

        if (!Files.exists(mdFile) && !Files.exists(metaFile)) {
            return false;
        }

        Files.deleteIfExists(mdFile);
        Files.deleteIfExists(metaFile);

        return true;
    }
}

