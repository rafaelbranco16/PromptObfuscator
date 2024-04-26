package prompt.overshadowing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

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
    public static Pii create(String id, String content) {
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
