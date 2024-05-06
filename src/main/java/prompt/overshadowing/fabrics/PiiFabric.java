package prompt.overshadowing.fabrics;

import prompt.overshadowing.exceptions.InvalidPIIException;
import prompt.overshadowing.model.Pii;

public class PiiFabric {
    public static Pii create(String piiId, String content) throws InvalidPIIException {
        return Pii.create(piiId, content);
    }
}
