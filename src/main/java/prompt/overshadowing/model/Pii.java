package prompt.overshadowing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import prompt.overshadowing.exceptions.InvalidPIIException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Getter
public class Pii {
    /**
     * The id of the pii. It's based on the request id
     * ex.:
     * {name_1_reqId}
     */
    @Id
    private String id;
    /**
     * The content
     */
    private String content;
    public Pii() {}

    /**
     * Full constructor
     * @param id the ID of the PII {name_NUMBER_ID}
     * @param content the Content
     */
    private Pii(String id, String content) {
        this.id = id;
        this.content = content;
    }

    /**
     * Creates a new PII
     * @param id the ID
     * @param content the content
     * @return a new PII
     */
    public static Pii create(String id, String content) throws InvalidPIIException {
        if(id == null || id.isEmpty() || id.isBlank()) {
            throw new InvalidPIIException("The id, " + id + ", is invalid.");
        }

        if(content == null || content.isEmpty() || content.isBlank()) {
            throw new InvalidPIIException("The content, " + content + ", is invalid.");
        }
        Pattern pattern = Pattern
                .compile("(?<=\\{)[^{}]*(?=\\})");
        Matcher matcher = pattern.matcher(content);

        if(matcher.find()) {
            throw new InvalidPIIException("This PII has been obfuscated or is a wrong detection from " +
                    "the revision and will not be created.");
        }

        return new Pii(id, content);
    }

    /**
     * The PII as a JSON string
     * @return the PII as JSON string
     */
    @Override
    public String toString() {
        return "\n{" +
                "\n\tid: " + this.id +
                "\n\tcontent: " + this.content +
                "\n}";
    }
}
