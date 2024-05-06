package prompt.overshadowing.utils;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {
    public static String getProperty(String key) {
        Properties prop = new Properties();
        try {
            prop.load(Utils.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return prop.getProperty(key);
    }
    public static String generatePromptTemplateAsString(Object variable, String variableDefinition,
                                                        PromptTemplate template) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(variableDefinition, variable);
        Prompt prompt = template.apply(variables);
        return prompt.text();
    }
}
