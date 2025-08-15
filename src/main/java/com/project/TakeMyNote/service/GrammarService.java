package com.project.TakeMyNote.service;

import com.project.TakeMyNote.model.GrammarSuggestionDto;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GrammarService {

    private final JLanguageTool langTool;

    public GrammarService() throws IOException {
        Language lang = Languages.getLanguageForShortCode("en-US");
        this.langTool = new JLanguageTool(lang);
    }

    public List<GrammarSuggestionDto> check(String text) throws IOException {
        if (text == null) text = "";
        List<RuleMatch> matches = langTool.check(text);
        List<GrammarSuggestionDto> result = new ArrayList<>();
        for (RuleMatch m : matches) {
            GrammarSuggestionDto dto = new GrammarSuggestionDto(
                    m.getFromPos(),
                    m.getToPos(),
                    m.getMessage(),
                    m.getSuggestedReplacements()
            );
            result.add(dto);
        }
        return result;
    }
}

