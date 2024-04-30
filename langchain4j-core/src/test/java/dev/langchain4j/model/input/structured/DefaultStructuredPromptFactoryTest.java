package dev.langchain4j.model.input.structured;

import dev.langchain4j.model.input.Prompt;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
class DefaultStructuredPromptFactoryTest implements WithAssertions {
    @StructuredPrompt("Hello, my name is {{name}}")
    static class Greeting {
        public final String name;

        public Greeting(String name) {
            this.name = name;
        }
    }

    @StructuredPrompt("Hello, my name is {{broken}}")
    static class BrokenPrompt {
        public final String name;

        public BrokenPrompt(String name) {
            this.name = name;
        }
    }
    @StructuredPrompt("${placeholder}")
    static class PlaceholderPrompt {

        public final String name;

        public PlaceholderPrompt(String name) {
            this.name = name;
        }
    }

    static class SimpleEnvironment {

        private final Map<String,String> map;

        SimpleEnvironment(Map<String, String> map) {
            this.map = map;
        }

        String resolverPlaceholders (String text) {
            for (Map.Entry<String,String> entry: map.entrySet()) {
                text = text.replaceFirst("\\$\\{"+entry.getKey()+"\\}", entry.getValue());
            }
            return text;
        }
    }


    @Test
    public void test() {
        DefaultStructuredPromptFactory factory = new DefaultStructuredPromptFactory();
        Prompt p = factory.toPrompt(new Greeting("Klaus"));
        assertThat(p.text()).isEqualTo("Hello, my name is Klaus");
    }

    @Test
    public void test_bad_input() {
        DefaultStructuredPromptFactory factory = new DefaultStructuredPromptFactory();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> factory.toPrompt(null))
                .withMessage("structuredPrompt cannot be null");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> factory.toPrompt(new Object()))
                .withMessage(
                        "java.lang.Object should be annotated with @StructuredPrompt " +
                                "to be used as a structured prompt");
    }

    @Test
    public void test_brokenPrompt() {
        DefaultStructuredPromptFactory factory = new DefaultStructuredPromptFactory();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> factory.toPrompt(new BrokenPrompt("Klaus")))
                .withMessage("Value for the variable 'broken' is missing");
    }

    @Test
    public void test_placeholder_prompt() {
        Map<String,String> variables = new HashMap<>();
        variables.put("placeholder", "Hello, my name is {{name}}");
        SimpleEnvironment environment = new SimpleEnvironment(variables);
        DefaultStructuredPromptFactory factory = new DefaultStructuredPromptFactory(environment::resolverPlaceholders);
        Prompt prompt = factory.toPrompt(new PlaceholderPrompt("Klaus"));
        assertThat(prompt.text()).isEqualTo("Hello, my name is Klaus");
    }
}