package prompt.overshadowing.services.interfaces;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import langfuse.sdk.model.Batch;
import langfuse.sdk.model.ChatPrompt;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("")
@RegisterRestClient
public interface ILLMLoggingService {
    @POST
    @Path("/api/public/ingestion")
    void sendBatch(@HeaderParam("Authorization") String headerAuthorization, Batch batch);
    @POST
    @Path("/api/public/prompts")
    void sendPrompt(@HeaderParam("Authorization") String headerAuthorization, ChatPrompt prompt);
}
