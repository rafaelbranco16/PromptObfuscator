package prompt.overshadowing.endtoend;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ObfuscateRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.exceptions.InvalidSplitException;
import prompt.overshadowing.services.PDFToPromptService;
import prompt.overshadowing.services.TxtToPromptService;
import prompt.overshadowing.services.interfaces.IFileToPromptService;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;
@Disabled
@QuarkusTest
public class ObfuscationControllerTest {
    /*@Test
    public void testOvershadowingEndpoint() {
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO("prompt",
                List.of("name"));
        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200);
    }

    @Test
    public void testOvershadowingEndpointWhenLLMIsOff() {
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO("prompt",
                List.of("name"));
        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200);
    }

    @Test
    public void testOvershadowingWithPiiInBody() {
        //Arrange
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO("My name is Rafael Branco",
                List.of("name"));
        //Act
        Response response = given()
                .contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200)
                .extract().response();

        String prompt = response.jsonPath().getString("prompt");
        String id = response.jsonPath().getString("id");
        //Assert
        assertThat(prompt, matchesRegex("My name is \\{Name_1_" + id + "}"));
    }

    @Test
    public void testOvershadowingWithoutPiiInBody() {
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO("No PII here.",
                List.of("name"));
        ResponseDTO expectedResponse = new ResponseDTO("", 400, "No PII here.");

        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200)
                .body("prompt", equalTo(expectedResponse.getPrompt()));
    }

    @Test
    public void testOvershadowingWithInvalidJsonBody() {
        given().contentType("application/json")
                .body("{}")
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(400);
    }

    @Test
    public void circuitBreakerTest() {
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO("prompt",
                List.of("name"));
        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200);
        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200);
        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200);
        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200);
        given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/overshadow")
                .then()
                .statusCode(200);
    }

    @Test
    public void testWithDocument() {
        IFileToPromptService srv = new TxtToPromptService();
        String promptText = srv.convertToString("/piiAsText.txt");
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(promptText,
                List.of("name"));
        Response response = given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/obfuscation")
                .then()
                .statusCode(200)
                .extract()
                .response();
        String prompt = response.jsonPath().getString("prompt");
        System.out.println(prompt);
        DesovershadowRequestDTO dto2 = new DesovershadowRequestDTO(prompt);
        response = given().contentType("application/json")
                .body(dto2)
                .when()
                .post("/overshadowing/deobfuscation")
                .then()
                .statusCode(200)
                .extract()
                .response();
        System.out.println(response.jsonPath().getString("prompt"));
    }

    @Test
    public void testWithPDFDocument() {
        IFileToPromptService srv = new PDFToPromptService();
        String promptText = srv.convertToString("/Currículo.pdf");
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(promptText,
                List.of("name"));
        Response response = given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/obfuscation")
                .then()
                .statusCode(200)
                .extract()
                .response();
        String prompt = response.jsonPath().getString("prompt");
        System.out.println(prompt);
        DesovershadowRequestDTO dto2 = new DesovershadowRequestDTO(prompt);
        response = given().contentType("application/json")
                .body(dto2)
                .when()
                .post("/overshadowing/deobfuscation")
                .then()
                .statusCode(200)
                .extract()
                .response();
        System.out.println(response.jsonPath().getString("prompt"));
    }
    @Test
    public void testWithPDFDocumentAsDocument() throws InvalidSplitException {
        int timeoutInMillis = 600000000;
        // Configure RestAssured to use the specified read timeout
            RestAssured.config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
            .setParam("http.connection.timeout", timeoutInMillis)
            .setParam("http.socket.timeout", timeoutInMillis));

        IFileToPromptService srv = new PDFToPromptService();
        String promptText = srv.convertToString("/Currículo2.pdf");
        System.out.println(promptText);
        ObfuscateRequestDTO dto = new ObfuscateRequestDTO(promptText,
                List.of("name"));
        Response response = given().contentType("application/json")
                .body(dto)
                .when()
                .post("/overshadowing/obfuscation")
                .then()
                .statusCode(200)
                .extract()
                .response();
        String prompt = response.jsonPath().getString("prompt");
        System.out.println(prompt);
        DesovershadowRequestDTO dto2 = new DesovershadowRequestDTO(prompt);
        response = given().contentType("application/json")
                .body(dto2)
                .when()
                .post("/overshadowing/deobfuscation")
                .then()
                .statusCode(200)
                .extract()
                .response();
        System.out.println(response.jsonPath().getString("prompt"));
        Assertions.assertEquals(promptText, response.jsonPath().getString("prompt"));
    }*/
}