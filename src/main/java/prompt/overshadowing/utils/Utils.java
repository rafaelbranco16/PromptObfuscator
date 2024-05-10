package prompt.overshadowing.utils;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Code done by ChatGPT 3.5
     * @param sentence the sentence
     * @param toReplace the to replace
     * @param replacement the replacement
     * @return the string obfuscated
     */
    public static String replaceInBetweenBrackets(String sentence, String toReplace, String replacement) {
        String s = sentence;
        while(s.contains(toReplace)) {
            int index = s.indexOf(toReplace);
            Pattern pattern = Pattern.compile("\\{\\w{1,}_\\d{1,}_\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}\\}");
            Matcher matcher = pattern.matcher(s);

            if (matcher.find()) {
                String s1 = matcher.group();
                int index1 = s.indexOf(s1);
                int index2 = index1 + s1.length();
                if(index > index1 && index < index2) s = s.substring(index2);
                else {
                    String s2 = s.replace(toReplace, replacement);
                    return sentence.replace(s, s2);
                }
            }else{
                String s2 = s.replace(toReplace, replacement);
                return sentence.replace(s, s2);
            }
        }
        return sentence;
    }
}
