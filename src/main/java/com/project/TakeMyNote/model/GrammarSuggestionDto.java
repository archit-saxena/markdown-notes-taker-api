package com.project.TakeMyNote.model;

import java.util.List;

public class GrammarSuggestionDto {
    private int from;
    private int to;
    private String message;
    private List<String> replacements;

    public GrammarSuggestionDto() {
    }

    public GrammarSuggestionDto(int from, int to, String message, List<String> replacements) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.replacements = replacements;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<String> replacements) {
        this.replacements = replacements;
    }
}

