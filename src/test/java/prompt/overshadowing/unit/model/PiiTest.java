package prompt.overshadowing.unit.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import prompt.overshadowing.exceptions.InvalidPIIException;
import prompt.overshadowing.model.Pii;

@QuarkusTest
public class PiiTest {
    @Test
    public void createValidPii() throws InvalidPIIException {
        Pii pii = Pii.create("{phone_1_a93d5452-1d81-41db-a432-312da0606b95}", "content");
        Assertions.assertNotNull(pii);
    }
    @Test
    public void createPiiWithNullId() {
        Assertions.assertThrows(InvalidPIIException.class, () -> Pii.create(null, "content"),
                "The id, " + null + ", is invalid.");
    }
    @Test
    public void createPiiWithEmptyId() {
        Assertions.assertThrows(InvalidPIIException.class, () -> Pii.create("", "content"),
                "The id, , is invalid.");
    }
    @Test
    public void createWithInvalidIdFormat() {
        String id = "{id_X";
        Assertions.assertThrows(InvalidPIIException.class, () -> Pii.create("{id_X", "content"),
                "The id," + id + ", is invalid.");
    }

    @Test
    public void createWithNullContent() {
        Assertions.assertThrows(InvalidPIIException.class, () -> Pii.create("id", null),
                "The content, " + null + ", is invalid.");
    }
    @Test
    public void createWithEmptyContent() {
        Assertions.assertThrows(InvalidPIIException.class, () -> Pii.create("id", ""),
                "The content, , is invalid.");
    }
    @Test
    public void createWithInvalidContentFormat() {
        String content = "{phone_1_a93d5452-1d81-41db-a432-312da0606b95}";
        Assertions.assertThrows(InvalidPIIException.class, () ->
                        Pii.create("id", content),
                "The content,"+ content + ", is invalid.");
    }
}
