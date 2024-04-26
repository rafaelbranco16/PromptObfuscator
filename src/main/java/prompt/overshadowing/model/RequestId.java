package prompt.overshadowing.model;

import jakarta.validation.Valid;
import lombok.Getter;
import prompt.overshadowing.exceptions.InvalidIdException;
import java.util.UUID;

public class RequestId {
    /**
     * The id
     */
    @Getter
    private String id;

    /**
     * Empty constructor
     */
    public RequestId() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Constructor with a valid id
     * @param id the id
     */
    private RequestId(@Valid String id) {
        this.id = id;
    }

    /**
     * Creates a new RequestId. This has the validation to verify if it's a valid UUID
     * @param id the id
     * @return the new RequestId
     * @throws InvalidIdException if the Id is invalid
     */
    public static RequestId create(String id) throws InvalidIdException {
        try {
            return new RequestId(UUID.fromString(id).toString());
        }catch (IllegalArgumentException e) {
            throw new InvalidIdException("This RequestId is invalid");
        }
    }
    public static RequestId create() {
        return new RequestId(UUID.randomUUID().toString());
    }
}
