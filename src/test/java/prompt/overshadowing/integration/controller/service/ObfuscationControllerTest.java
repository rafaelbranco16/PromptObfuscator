package prompt.overshadowing.integration.controller.service;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import prompt.overshadowing.constants.Constants;
import prompt.overshadowing.controllers.ObfuscationController;
import prompt.overshadowing.dto.ObfuscateRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.services.interfaces.ILlmModelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuarkusTest
public class ObfuscationControllerTest {
    @InjectMock
    ILlmModelService model;
    @Inject
    ObfuscationController ctrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void overshadowingTestWithValidRequest() throws LLMRequestException {
        //Arrange
        List<String> keywords = List.of("name");
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(
                "My name is Example Name.",
                keywords
        );
        String sysMessage = generatePromptTemplate(dto.getKeywords());
        Mockito.when(model.generate(sysMessage, dto.getPrompt()))
                .thenReturn("[{\"pii\":\"Example Name\",\"type\":\"name\"}]");
        String expected = "My name is {name_1_";
        // Act
        ResponseDTO response = (ResponseDTO) this.ctrl.obfuscation(dto).getEntity();
        // Assert
        Assertions.assertTrue(response.getPrompt().contains("{name_1_"));
    }
    @Test
    public void overshadowingWithEmptyPrompt() throws LLMRequestException {
        //Arrange
        List<String> keywords = List.of("name");
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(
                "",
                keywords
        );
        String sysMessage = generatePromptTemplate(dto.getKeywords());
        Mockito.when(model.generate(sysMessage, dto.getPrompt()))
                .thenReturn("[]");
        String expected = "text cannot be null or blank";
        // Act
        ResponseDTO response = (ResponseDTO) this.ctrl.obfuscation(dto).getEntity();
        // Assert
        Assertions.assertEquals(expected, response.getPrompt());
    }
    @Test
    public void overshadowingWithNoKeywords() throws LLMRequestException {
        //Arrange
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(
                "My name is Example Name",
                List.of()
        );
        String sysMessage = generatePromptTemplate(dto.getKeywords());
        Mockito.when(model.generate(sysMessage, dto.getPrompt()))
                .thenReturn("[]");
        String expected = "My name is Example Name";
        // Act
        ResponseDTO response = (ResponseDTO) this.ctrl.obfuscation(dto).getEntity();
        // Assert
        Assertions.assertEquals(expected, response.getPrompt());
    }

    @Test
    public void overshadowingButThrowsAtGenerateResponseFromLlm() throws LLMRequestException {
        //Arrange
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(
                "My name is Example Name",
                List.of()
        );
        String sysMessage = generatePromptTemplate(dto.getKeywords());
        Mockito.when(model.generate(sysMessage, dto.getPrompt()))
                .thenThrow(IllegalArgumentException.class);
        String expected = "We couldnt reach the LM. Try again in 5 minutes.";
        // Act
        ResponseDTO response = (ResponseDTO) this.ctrl.obfuscation(dto).getEntity();
        // Assert
        Assertions.assertEquals(expected, response.getPrompt());
    }
    @Test
    public void overshadowingReturnsNullFromLlm() throws LLMRequestException {
        //Arrange
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(
                "My name is Example Name",
                List.of()
        );
        String sysMessage = generatePromptTemplate(dto.getKeywords());
        Mockito.when(model.generate(sysMessage, dto.getPrompt()))
                .thenReturn(null);
        String expected = "Invalid response from Llm.";
        // Act
        ResponseDTO response = (ResponseDTO) this.ctrl.obfuscation(dto).getEntity();
        // Assert
        Assertions.assertEquals(expected, response.getPrompt());
    }

    private String generatePromptTemplate(List<String> keywords) {
        PromptTemplate template = Constants.promptTemplate;
        Map<String, Object> variables = new HashMap<>();
        variables.put("keywords", keywords);
        Prompt prompt = template.apply(variables);
        return prompt.text();
    }
}
