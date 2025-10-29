package com.InSpace.Api.services.util;

import org.springframework.ai.chat.prompt.Prompt;

import java.util.Map;
import java.util.Objects;

public final class PromptBuilder {

    private final StringBuilder sb = new StringBuilder();
    private final String lineSeparator = "\n";

    public PromptBuilder() {
    }

    
    public PromptBuilder addLine(String text) {
        if (text == null || text.isEmpty()) return this;
        if (sb.length() > 0) sb.append(lineSeparator);
        sb.append(text);
        return this;
    }

    public PromptBuilder addTemplate(String template, Map<String, String> variables) {
        if (template == null || template.isEmpty()) return this;
        String out = template;
        if (variables != null && !variables.isEmpty()) {
            for (Map.Entry<String, String> e : variables.entrySet()) {
                String key = "{" + e.getKey() + "}";
                out = out.replace(key, Objects.toString(e.getValue(), ""));
            }
        }
        return addLine(out);
    }

    public PromptBuilder clear() {
        sb.setLength(0);
        return this;
    }

    public String build() {
        return sb.toString();
    }

    public Prompt buildPrompt() {
        return new Prompt(build());
    }
}
