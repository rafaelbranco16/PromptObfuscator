package prompt.overshadowing.exceptions;

import java.net.ConnectException;

public class LLMRequestException extends ConnectException {
    public LLMRequestException(String message) {super(message);}
}
