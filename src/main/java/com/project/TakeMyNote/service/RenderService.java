package com.project.TakeMyNote.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.springframework.stereotype.Service;

@Service
public class RenderService {
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public String renderAndSanitize(String markdown) {
        if (markdown == null) markdown = "";
        com.vladsch.flexmark.util.ast.Node doc = parser.parse(markdown);
        String html = renderer.render(doc);

        // sanitize with Jsoup (allow basic formatting + links)
        String safe = org.jsoup.Jsoup.clean(html, org.jsoup.safety.Safelist.basicWithImages());
        return safe;
    }
}

