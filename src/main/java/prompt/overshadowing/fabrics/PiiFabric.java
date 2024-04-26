package prompt.overshadowing.fabrics;

import prompt.overshadowing.model.Pii;

public class PiiFabric {
    public static Pii create(String piiId, String content) {
        return Pii.create(piiId, content);
    }
}
