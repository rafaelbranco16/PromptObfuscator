package prompt.overshadowing.unit.services;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.exceptions.InvalidPIIException;
import prompt.overshadowing.model.Pii;
import prompt.overshadowing.repositories.PiiRepository;
import prompt.overshadowing.services.DeobfuscateService;
import prompt.overshadowing.services.interfaces.IDeobfuscateService;

import java.util.UUID;

@QuarkusTest
public class DeobfuscateServiceTest {
    @Inject
    DeobfuscateService service;
    @InjectMock
    PiiRepository repo;
    @Test
    public void testDeobfuscateWithValidRequest() throws InvalidPIIException {
        // Arrange
        String id = UUID.randomUUID().toString();
        String fullId = "{name_1_" + id + "}";
        DesovershadowRequestDTO dto = new DesovershadowRequestDTO("My name is {name_1_" + id + "}");
        Pii pii = Pii.create(fullId, "Rafael");
        Mockito.when(repo.findById(Mockito.anyString())).thenReturn(pii);
        String expectedPrompt = "My name is Rafael";
        int expectedCode = 200;
        // Act
        ResponseDTO response = this.service.deobfuscate(dto);
        // Assert
        Assertions.assertEquals(expectedCode, response.getCode());
        Assertions.assertEquals(expectedPrompt, response.getPrompt());
    }
    @Test
    public void testDeobfuscateWithInvalidPromptRequest(){
        // Arrange + Act + Assert
        Assertions.assertThrows(NullPointerException.class,
                () -> new DesovershadowRequestDTO(null));
    }
    @Test
    public void testDeobfuscateWithEmptyPromptReturnsEmptyPrompt() {
        // Arrange
        DesovershadowRequestDTO dto = new DesovershadowRequestDTO("");

        Mockito.when(repo.findById(Mockito.anyString())).thenReturn(null);
        String expectedPrompt = "";
        int expectedCode = 200;
        // Act
        ResponseDTO response = this.service.deobfuscate(dto);
        // Assert
        Assertions.assertEquals(expectedCode, response.getCode());
        Assertions.assertEquals(expectedPrompt, response.getPrompt());
    }

    @Test
    public void testDeobfuscateWithPromptWithNonExistIdReturnsInvalidPrompt() {
        // Arrange
        String nonExistId = "{name}";
        DesovershadowRequestDTO dto = new DesovershadowRequestDTO("My name is " + nonExistId +"}");

        Mockito.when(repo.findById(Mockito.anyString())).thenReturn(null);
        String expectedPrompt = "This parameter " + nonExistId + " does not exist.";
        int expectedCode = 404;
        // Act
        ResponseDTO response = this.service.deobfuscate(dto);
        // Assert
        Assertions.assertEquals(expectedCode, response.getCode());
        Assertions.assertEquals(expectedPrompt, response.getPrompt());
    }
}
