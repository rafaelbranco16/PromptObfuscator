package prompt.overshadowing.unit.services;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.model.Prompt;
import prompt.overshadowing.services.interfaces.ILlmModelService;
import prompt.overshadowing.services.interfaces.IPIIRevisionService;

import java.util.List;

@QuarkusTest
public class PiiRevisionService {
    @Inject
    IPIIRevisionService piiService;
    @InjectMock
    ILlmModelService llmModelService;
    @Test
    public void testNeedsHigherRevisionWithValidPromptReturnsTrue() throws LLMRequestException {
        // Arrange
        Prompt prompt = Prompt.create("This is a valid prompt");
        List<String> keywords = List.of("");
        Mockito.when(this.llmModelService.generate(Mockito.anyString(), Mockito.anyString())).thenReturn("true");
        // Act
        boolean needsHigherRevision = this.piiService.needsHigherRevision(prompt, keywords);
        // Assert
        Assertions.assertTrue(needsHigherRevision);
    }
    @Test
    public void testNeedsHigherRevisionWithValidPromptReturnsFalse() throws LLMRequestException {
        // Arrange
        Prompt prompt = Prompt.create("This is a valid prompt");
        List<String> keywords = List.of("");
        Mockito.when(this.llmModelService.generate(Mockito.anyString(), Mockito.anyString())).thenReturn("false");
        // Act
        boolean needsHigherRevision = this.piiService.needsHigherRevision(prompt, keywords);
        // Assert
        Assertions.assertFalse(needsHigherRevision);
    }
    @Test
    public void testNeedsHigherRevisionWithInvalidPromptReturnsFalse() throws LLMRequestException {
        // Arrange + Act + Assert
        List<String> keywords = List.of("");
        boolean needsHigherRevision = this.piiService.needsHigherRevision(null, keywords);
        Assertions.assertFalse(needsHigherRevision);
    }
    @Test
    public void testNeedsHigherRevisionNoConnectionToLLMThrowsException() throws LLMRequestException {
        // Arrange
        Prompt prompt = Prompt.create("This is a valid prompt");
        List<String> keywords = List.of("");
        Mockito.when(this.llmModelService.generate(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(LLMRequestException.class);
        // Act + Assert
        Assertions.assertThrows(LLMRequestException.class , () -> this.piiService.needsHigherRevision(prompt, keywords));
    }
}
