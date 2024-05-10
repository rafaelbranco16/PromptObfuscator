package prompt.overshadowing.model;

import lombok.Getter;
import prompt.overshadowing.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public int replaceStringOnPrompt(String s, String toReplace, String fiveAfter, int lastPosIndex) {
        // The idea of this method is something like this:
        // {name_X}, Anthony, is som, 0
        // The total string represents the original string from the prompt
        // Example: Anthony is som
        // The replacement is the total string, but with the name obfuscated
        // Example: {name_X} is som
        // After that, it is replaced on the original prompt
        // It uses between 3 and 5 characters of context to replace on the prompt.
        // That is specially important for cases like age that uses characters that
        // are frequently found on any kind of text
        // If the PII + context is not found in the prompt, it is replaced for safety
        // The lastPosIndex represents where on the text we're replacing. It's like reading a book.

        String total = toReplace + fiveAfter;
        String replacement = total.replace(toReplace, s);
        String prompt = this.prompt.substring(lastPosIndex);
        if(this.prompt.contains(total)) {
            String promptReplace = prompt.replace(total, replacement);
            this.prompt = this.prompt.replace(prompt, promptReplace);
            int pos = this.prompt.lastIndexOf(s);
            return pos + s.length();
        }else{
            String promptReplace = prompt.replace(toReplace, s);
            this.prompt = this.prompt.replace(prompt, promptReplace);
            int pos = this.prompt.lastIndexOf(s);
            return pos + s.length();
        }
    }

    /**
     * Add the PII to the prompt list
     * @param pii the PII to be added
     */
    public void addPiiToList(Pii pii) throws IllegalArgumentException {
        if(pii == null)
            throw new IllegalArgumentException("The pii added to the prompt cannot be null");
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
        if (pii != null) {
            pii.stream()
                    .filter(Objects::nonNull)
                    .forEach(this.piis::add);
        }    }
}
