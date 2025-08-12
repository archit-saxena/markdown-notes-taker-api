package com.project.TakeMyNote.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {

    @Value("${notes.storage.path:./data/notes}")
    private String storagePath;

    @Bean
    public Path notesRoot() throws IOException {
        Path root = Paths.get(storagePath).toAbsolutePath().normalize();
        Files.createDirectories(root); // creates folder if missing
        return root;
    }
}
