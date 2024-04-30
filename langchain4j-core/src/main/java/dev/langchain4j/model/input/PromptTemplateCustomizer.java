package dev.langchain4j.model.input;

@FunctionalInterface
public interface PromptTemplateCustomizer {

    String customer (String template);
}
