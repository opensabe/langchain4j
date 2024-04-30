package dev.langchain4j.model.input;

@FunctionalInterface
public interface PromptTemplateCustomizer {

    String customer (String template);

    default PromptTemplateCustomizer andThen (PromptTemplateCustomizer after) {
        return template -> after.customer(customer(template));
    }
}
