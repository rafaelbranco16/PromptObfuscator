package prompt.overshadowing.unit.services;

import dev.langchain4j.data.segment.TextSegment;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ObfuscateRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.exceptions.InvalidPIIException;
import prompt.overshadowing.exceptions.LLMRequestException;
import prompt.overshadowing.exceptions.OvershadowingIllegalArgumentException;
import prompt.overshadowing.exceptions.OvershadowingJsonParseException;
import prompt.overshadowing.model.Pii;
import prompt.overshadowing.model.Prompt;
import prompt.overshadowing.repositories.PiiRepository;
import prompt.overshadowing.services.OvershadowingService;
import prompt.overshadowing.services.interfaces.IDeobfuscateService;
import prompt.overshadowing.services.interfaces.ILlmModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@QuarkusTest
public class OvershadowingServiceTest {
    private final String llmResponseTemplate = "[{\"pii\": \"O meu nome\", \"type\": \"Name\"},{\"pii\": \"21\", \"type\":\"Age\"}]";
    private final String promptTemplate = "My name is O meu nome. Im 21 years.";
    private final String reqIdTemplate = UUID.randomUUID().toString();
    @Inject
    OvershadowingService service;
    @InjectMock
    PiiRepository repo;
    @InjectMock
    ILlmModelService llmService;
    @Inject
    IDeobfuscateService deobfuscateService;

    @Test
    public void overshadowWithValidPrompt() throws OvershadowingIllegalArgumentException,
            OvershadowingJsonParseException, LLMRequestException {
        // Arrange
        String reqId = "id1";
        String expected = "My name is {Name_1_"+reqId+"}. Im {Age_1_"+reqId+"} years.";
        String sysMessage = "test message.";
        String usrMessage = "My name is My name. Im 16 years.";
        Mockito
                .when(llmService.generate(sysMessage, usrMessage))
                .thenReturn("My name is {Name_1_"+reqId+"}. Im {Age_1_"+reqId+"} years.");
        Pii mockPii = new Pii(); // create a mock Pii object
        Mockito.doNothing().when(repo).persist(mockPii);
        // Act
        Prompt actual = service.overshadow(llmResponseTemplate, promptTemplate, reqId);
        // Assert
        Assertions.assertEquals(expected, actual.getPrompt());
    }
    @Test
    public void overshadowingWithNullPrompt() {
        // Arrange
        String expected = "argument \"content\" is null";
        // Act + Assert
        Assertions.assertThrows(OvershadowingIllegalArgumentException.class,
                () -> service.overshadow(null, null, reqIdTemplate), expected);
    }
    @Test
    public void overshadowingWithInvalidJson() {
        // Arrange
        String invalidJson = "[{\"pii: \"O meu nome\", \"type\": \"Name\"},{\"pii\": \"21\", \"type\":\"Age\"}]";
        // Act + Assert
        Assertions.assertThrows(OvershadowingJsonParseException.class,
                () -> service.overshadow(invalidJson, promptTemplate, reqIdTemplate));
    }

    /**
     * This test will most likely not work and that's because it depends on the fact that the LM will work based on the
     * prompt. But it's being tested in case of it happens, LLM can hallucinate
     */
    @Test
    public void overshadowingWithPromptWithNoPiiInTheSentence() throws OvershadowingIllegalArgumentException,
            OvershadowingJsonParseException {
        // Arrange
        String prompt = "No pii in here!";
        Pii mockPii = new Pii(); // create a mock Pii object
        Mockito.doNothing().when(repo).persist(mockPii);
        // Act
        Prompt actual = service.overshadow(llmResponseTemplate, prompt, reqIdTemplate);
        // Assert
        Assertions.assertDoesNotThrow(() -> service.overshadow(llmResponseTemplate, prompt, reqIdTemplate));
        Assertions.assertEquals(actual.getPrompt(), prompt);
    }

    /**
     * The same can happen, even if there is pii in the sentence. It can happen to not find any pii even if it exists
     */
    @Test
    public void overshadowingWithPromptWithPiiNotFoundInTheSentence() throws OvershadowingIllegalArgumentException,
            OvershadowingJsonParseException {
        // Arrange
        String llmResponse = "[]";
        Pii mockPii = new Pii(); // create a mock Pii object
        Mockito.doNothing().when(repo).persist(mockPii);
        // Act
        Prompt actual = service.overshadow(llmResponse, promptTemplate, reqIdTemplate);
        // Assert
        Assertions.assertDoesNotThrow(() -> service.overshadow(llmResponse, promptTemplate, reqIdTemplate));
        Assertions.assertEquals(actual.getPrompt(), promptTemplate);
    }

    /**
     * The scenario for this test is the following:
     * We have 2 times the same person. The objective is for the program recognize it has that person and
     * replace with the same on every single appearance.
     */
    @Test
    public void overshadowingWithRepeatedPII() throws OvershadowingIllegalArgumentException,
            OvershadowingJsonParseException {
        String promptWithRepeatedPii = "My name is O meu nome. Im 21 years. I work with Segunda Pessoa. Me, O meu nome " +
                "have no problem in working with Segunda Pessoa";
        String llmResponse = "[" +
                                "{\"pii\":\"O meu nome\", \"type\":\"Name\"},"+
                                "{\"pii\":\"21\", \"type\":\"Age\", \"5after\":\" years\"},"+
                                "{\"pii\":\"Segunda Pessoa\", \"type\":\"Name\"}," +
                                "{\"pii\":\"O meu nome\", \"type\":\"Name\"}," +
                                "{\"pii\":\"Segunda Pessoa\", \"type\":\"Name\"}" +
                            "]";
        String expected = "My name is {Name_1_"+reqIdTemplate+"}. Im {Age_1_"+reqIdTemplate+"} years. " +
                "I work with {Name_2_"+reqIdTemplate+"}. Me, {Name_3_"+reqIdTemplate+"} " +
                "have no problem in working with {Name_4_"+reqIdTemplate+"}";
        Pii mockPii = new Pii(); // create a mock Pii object
        Mockito.doNothing().when(repo).persist(mockPii);
        // Act
        Prompt actual = service.overshadow(llmResponse, promptWithRepeatedPii, reqIdTemplate);
        // Assert
        Assertions.assertDoesNotThrow(() -> service.overshadow(llmResponse, promptWithRepeatedPii, reqIdTemplate));
        Assertions.assertEquals(expected, actual.getPrompt());
    }

    @Test
    public void overshadowingWith5AfterWithRightCharsAtWrongPositions() throws OvershadowingIllegalArgumentException,
            OvershadowingJsonParseException {
        String promptWithRepeatedPii = "My name is O meu nome. Im 21 years. I work with Segunda Pessoa. Me, O meu nome " +
                "have no problem in working with Segunda Pessoa";
        String llmResponse = "[" +
                "{\"pii\":\"O meu nome\", \"type\":\"Name\"},"+
                "{\"pii\":\"21\", \"type\":\"Age\", \"5after\":\"years\"},"+
                "{\"pii\":\"Segunda Pessoa\", \"type\":\"Name\"}," +
                "{\"pii\":\"O meu nome\", \"type\":\"Name\"}," +
                "{\"pii\":\"Segunda Pessoa\", \"type\":\"Name\"}" +
                "]";
        String expected = "My name is {Name_1_"+reqIdTemplate+"}. Im {Age_1_"+reqIdTemplate+"} years. " +
                "I work with {Name_2_"+reqIdTemplate+"}. Me, {Name_3_"+reqIdTemplate+"} " +
                "have no problem in working with {Name_4_"+reqIdTemplate+"}";
        Pii mockPii = new Pii(); // create a mock Pii object
        Mockito.doNothing().when(repo).persist(mockPii);
        // Act
        Prompt actual = service.overshadow(llmResponse, promptWithRepeatedPii, reqIdTemplate);
        // Assert
        Assertions.assertDoesNotThrow(() -> service.overshadow(llmResponse, promptWithRepeatedPii, reqIdTemplate));
        Assertions.assertEquals(expected, actual.getPrompt());
    }
    @Test
    public void overshadowingWith5AfterWithWrong5After() throws OvershadowingIllegalArgumentException,
            OvershadowingJsonParseException {
        String promptWithRepeatedPii = "My name is O meu nome. Im 21 years. I work with Segunda Pessoa. Me, O meu nome " +
                "have no problem in working with Segunda Pessoa";
        String llmResponse = "[" +
                "{\"pii\":\"O meu nome\", \"type\":\"Name\"},"+
                "{\"pii\":\"21\", \"type\":\"Age\", \"5after\":\"yeas\"},"+
                "{\"pii\":\"Segunda Pessoa\", \"type\":\"Name\"}," +
                "{\"pii\":\"O meu nome\", \"type\":\"Name\"}," +
                "{\"pii\":\"Segunda Pessoa\", \"type\":\"Name\"}" +
                "]";
        String expected = "My name is {Name_1_"+reqIdTemplate+"}. Im {Age_1_"+reqIdTemplate+"} years. " +
                "I work with {Name_2_"+reqIdTemplate+"}. Me, {Name_3_"+reqIdTemplate+"} " +
                "have no problem in working with {Name_4_"+reqIdTemplate+"}";
        Pii mockPii = new Pii(); // create a mock Pii object
        Mockito.doNothing().when(repo).persist(mockPii);
        // Act
        Prompt actual = service.overshadow(llmResponse, promptWithRepeatedPii, reqIdTemplate);
        // Assert
        Assertions.assertDoesNotThrow(() -> service.overshadow(llmResponse, promptWithRepeatedPii, reqIdTemplate));
        Assertions.assertEquals(expected, actual.getPrompt());
    }
    @Test
    public void validDeobfuscation() throws InvalidPIIException {
        //Arrange
        String reqId = UUID.randomUUID().toString();
        DesovershadowRequestDTO dto = new DesovershadowRequestDTO("My name is {name_1_"+reqId+"}");
        Mockito.when(repo.findById("{name_1_"+reqId+"}"))
                .thenReturn(Pii.create("{name_1_"+reqId+"}", "My name"));
        ResponseDTO expected = new ResponseDTO(reqId, 200, "My name is My name");

        //Act
        ResponseDTO actual = this.deobfuscateService.deobfuscate(dto);
        //Assert
        Assertions.assertEquals(expected.getCode(), actual.getCode());
        Assertions.assertEquals(expected.getPrompt(), actual.getPrompt());
    }
    @Test
    public void notExistingParameterDeobfuscation() {
        //Arrange
        String reqId = UUID.randomUUID().toString();
        DesovershadowRequestDTO dto = new DesovershadowRequestDTO("My name is {name_1_"+reqId+"}");
        Mockito.when(repo.findById("{name_1_"+reqId+"}"))
                .thenReturn(null);
        ResponseDTO expected = new ResponseDTO(reqId, 404, "This parameter " + "name_1_"+reqId+
                " does not exist.");

        //Act
        ResponseDTO actual = this.deobfuscateService.deobfuscate(dto);
        //Assert
        Assertions.assertEquals(expected.getCode(), actual.getCode());
        Assertions.assertEquals(expected.getPrompt(), actual.getPrompt());
    }
    @Test
    public void emptyPromptDeobfuscation() {
        //Arrange
        String reqId = UUID.randomUUID().toString();
        DesovershadowRequestDTO dto = new DesovershadowRequestDTO("");

        ResponseDTO expected = new ResponseDTO(reqId, 200, "");

        //Act
        ResponseDTO actual = this.deobfuscateService.deobfuscate(dto);
        //Assert
        Assertions.assertEquals(expected.getCode(), actual.getCode());
        Assertions.assertEquals(expected.getPrompt(), actual.getPrompt());
    }
    /*@Test
    public void obfuscate() throws LLMRequestException {
        String prompt = "My name is Rafael";
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(prompt, List.of("name"));
        Mockito.when(this.llmService.generate(Mockito.anyString(), Mockito.anyString()))
                .thenReturn("[{\"pii\":\"Rafael\",\"type\":\"name\"}]");
        Pii mockPii = new Pii(); // create a mock Pii object
        Mockito.doNothing().when(repo).persist(mockPii);
        String expectedString = "My name is {name_1_";
        // Act
        ResponseDTO response = this.service.obfuscate(dto);
        String actual = response.getPrompt();

        //Assert
        Assertions.assertTrue(actual.contains(expectedString));
    }*/
}
