package prompt.overshadowing.services.interfaces;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import prompt.overshadowing.model.langfuse.Batch;
import prompt.overshadowing.model.langfuse.ChatPrompt;

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
