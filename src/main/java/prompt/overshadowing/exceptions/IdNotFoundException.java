package prompt.overshadowing.exceptions;

import jakarta.ws.rs.NotFoundException;

public class IdNotFoundException extends NotFoundException {
    /**
     * Should be thrown when the ID is not found
     * @param message the message
     */
    public IdNotFoundException(String message) {super(message);}
}
