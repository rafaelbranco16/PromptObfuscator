package prompt.overshadowing.utils;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
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
        if(sentence.contains(toReplace)) {
            Map<Integer, Integer> map = new HashMap<>();
            Pattern pattern = Pattern.compile("\\{\\w{1,}_\\d{1,}_\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}\\}");
            Matcher matcher = pattern.matcher(sentence);
            while(matcher.find()) {
                map.put(sentence.indexOf(matcher.group()), matcher.group().length());
            }
            int index = 0;
            int index2 = 0;
            int index1;
            while (index >= 0) {
                index = sentence.indexOf(toReplace, index+1);
                boolean doChange = true;
                for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
                    index1 = entry.getKey();
                    index2 = entry.getKey() + entry.getValue();
                    if(index1 < index && index2 > index) doChange = false;
                }
                if(doChange && index >= 0) {
                    return sentence.substring(0, index) + replacement +
                            sentence.substring(index+toReplace.length());
                }
            }
        }
        return sentence;
    }
    public static Prompt generatePromptFromTemplate(PromptTemplate template, Map<String, Object> variables) {
        return template.apply(variables);
    }
}
