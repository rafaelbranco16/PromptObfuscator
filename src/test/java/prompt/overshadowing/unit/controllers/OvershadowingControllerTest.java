package prompt.overshadowing.unit.controllers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import prompt.overshadowing.controllers.ObfuscationController;
import prompt.overshadowing.dto.ObfuscateRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.services.interfaces.IOvershadowingService;

import java.util.List;
import java.util.UUID;

@QuarkusTest
public class OvershadowingControllerTest {
    @InjectMock
    IOvershadowingService srv;
    @Inject
    ObfuscationController ctrl;
    @Test
    public void validObfuscation() {
        //Arrange
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO("My name is My name", List.of("name"));
        String reqId = UUID.randomUUID().toString();
        ResponseDTO expected = new ResponseDTO(reqId, 200, "My name is {name_1_"+reqId+"}");
        Mockito.when(srv.overshadowPrompt(dto)).thenReturn(expected);
        //Act
        Response r = Response.ok().entity(expected).build();
        ResponseDTO actual = (ResponseDTO) r.getEntity();
        //Assert
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void obfuscateEmptyPrompt() {
        //Arrange
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO("", List.of("name"));
        ResponseDTO expected = new ResponseDTO(UUID.randomUUID().toString(), 200, "");
        Mockito.when(srv.overshadowPrompt(dto)).thenReturn(expected);

        //Act
        Response r = ctrl.obfuscation(dto);
        ResponseDTO actual = (ResponseDTO) r.getEntity();
        //Assert
        Assertions.assertEquals(expected, actual);
    }
}