package prompt.overshadowing.unit.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import prompt.overshadowing.exceptions.InvalidPIIException;
import prompt.overshadowing.model.Pii;
import prompt.overshadowing.model.Prompt;

import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class PromptTest {
    @Test
    public void createValidPrompt() {
        // Arrange
        String expected = "prompt";

        // Act
        Prompt actual = Prompt.create(expected);

        // Assert
        Assertions.assertDoesNotThrow(() -> Prompt.create(expected));
        Assertions.assertEquals(expected, actual.getPrompt());
        Assertions.assertNotNull(actual.getPiis());
    }

    @Test
    public void createWithInvalidString() {
        // Arrange
        String expected = "";

        // Act
        Prompt actual = Prompt.create(null);

        // Assert
        Assertions.assertDoesNotThrow(() -> Prompt.create(null));
        Assertions.assertEquals(expected, actual.getPrompt());
    }
    @Test
    public void addValidPii() throws InvalidPIIException {
        // Arrange
        Pii pii = Pii.create("{phone_1_a93d5452-1d81-41db-a432-312da0606b95}", "content");
        Prompt actual = Prompt.create("prompt");
        int expectedSize = 1;

        // Act
        Assertions.assertDoesNotThrow(() -> actual.addPiiToList(pii));

        // Assert
        Assertions.assertEquals(expectedSize, actual.getPiis().size());
        Assertions.assertEquals(pii, actual.getPiis().get(0));
    }
    @Test
    public void addInvalidPii() {
        // Arrange
        Prompt actual = Prompt.create("prompt");

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> actual.addPiiToList(null));

        // Assert
        Assertions.assertEquals(actual.getPiis().size(), 0);
    }
    @Test
    public void findPiiStringsOnPrompt() {
        // Arrange
        String validString = "{phone_1_a93d5452-1d81-41db-a432-312da0606b95}";
        String expectedString = "phone_1_a93d5452-1d81-41db-a432-312da0606b95";
        Prompt prompt = Prompt.create("prompt with valid string " + validString);
        int expectedSize = 1;
        // Act
        List<String> validStrings = prompt.findPiiStrings();

        // Assert
        Assertions.assertEquals(expectedSize, prompt.findPiiStrings().size());
        Assertions.assertEquals(expectedString, validStrings.get(0));
    }
     @Test
    public void addListWithValidPiis() throws InvalidPIIException {
        // Arrange
         Pii pii1 = Pii.create("{phone_1_a93d5452-1d81-41db-a432-312da0606b95}", "content");
         Pii pii2 = Pii.create("{phone_2_a93d5452-1d81-41db-a432-312da0606b95}", "content");
         List<Pii> validPiis = List.of(pii1, pii2);
         int expectedSize = 2;
         Prompt prompt = Prompt.create("prompt with valid list " + validPiis);

         // Act + Assert
         Assertions.assertDoesNotThrow(() -> prompt.addPiisToList(validPiis));
         Assertions.assertEquals(expectedSize, prompt.getPiis().size());
     }
    @Test
    public void addListWithInvalidPiis() {
        // Arrange
        Prompt prompt = Prompt.create("prompt");
        List<Pii> invalidPiis = new ArrayList<>();
        invalidPiis.add(null);
        int expectedSize = 0;
        // Act
        prompt.addPiisToList(invalidPiis);
        // Assert
        Assertions.assertEquals(expectedSize, prompt.getPiis().size());
     }
}
