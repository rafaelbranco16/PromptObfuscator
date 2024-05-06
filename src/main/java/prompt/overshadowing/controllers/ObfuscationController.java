package prompt.overshadowing.controllers;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ObfuscateRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.services.interfaces.IDeobfuscateService;
import prompt.overshadowing.services.interfaces.IOvershadowingService;

@Path("/overshadowing")
public class ObfuscationController {
    /**
     *  The service that obfuscates the prompt
     */
    @Inject
    IOvershadowingService obfuscateService;
    @Inject
    IDeobfuscateService deobfuscateService;
    @POST
    @Path("/obfuscation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obfuscation(@Valid ObfuscateRequestDTO request) {
        ResponseDTO dto = this.obfuscateService.overshadowPrompt(request);
        return Response.ok().entity(dto).build();
    }
    @POST
    @Path("/deobfuscation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deobfuscation(@Valid DesovershadowRequestDTO request) {
        ResponseDTO dto = this.deobfuscateService.deobfuscate(request);
        return Response.status(dto.getCode()).entity(dto).build();
    }
}