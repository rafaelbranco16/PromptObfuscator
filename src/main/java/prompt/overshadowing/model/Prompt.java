package prompt.overshadowing.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Prompt {
    /**
     * The prompt
     */
    private String prompt;
    /**
     * The list of the piis inside a prompt. Empty by default.
     */
    private final List<Pii> piis;

    /**
     * Prompt constructor
     * @param prompt the prompt
     */
    private Prompt(String prompt) {
        this.prompt = prompt;
        this.piis = new ArrayList<>();
    }

    /**
     * Creates a new prompt with validations
     * @param prompt the prompt
     * @return the new prompt
     */
    public static Prompt create(String prompt) {
        if(prompt == null)
            return new Prompt("");
        return new Prompt(prompt);
    }

    /**
     * Replace a string on the prompt with another
     * @param toReplace the string to replace
     * @param s the string
     */
    public void replaceStringOnPrompt(String toReplace, String s) {
        this.prompt = this.prompt.replace(s, toReplace);
    }

    /**
     * Add the PII to the prompt list
     * @param pii the PII to be added
     */
    public void addPiiToList(Pii pii) {
        this.piis.add(pii);
    }

    public List<String> findPiiStrings() {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(this.prompt);

        while (matcher.find()) {
            matches.add(matcher.group(1));
        }

        return matches;
    }

    public void addPiisToList(List<Pii> pii) {
        this.piis.addAll(pii);
    }
}
