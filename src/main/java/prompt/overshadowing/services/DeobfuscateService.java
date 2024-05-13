package prompt.overshadowing.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.fabrics.RequestFabric;
import prompt.overshadowing.model.Pii;
import prompt.overshadowing.model.Prompt;
import prompt.overshadowing.model.Request;
import prompt.overshadowing.repositories.PiiRepository;
import prompt.overshadowing.services.interfaces.IDeobfuscateService;

import java.util.List;

@ApplicationScoped
public class DeobfuscateService implements IDeobfuscateService {
    @Inject
    PiiRepository piiRepo;
    /**
     * Deobfuscate a prompt
     * @param dto the dto with the request
     * @return the deobfuscated prompt
     */
    public ResponseDTO deobfuscate(DesovershadowRequestDTO dto) {
        Request req = RequestFabric.create(dto.getPrompt());
        List<String> piis = req.getPrompt().findPiiStrings();
        Prompt prompt =req.getPrompt();
        for(String s : piis) {
            Pii pii = this.piiRepo.findById("{" + s + "}");
            if (pii != null) {
                try {
                    prompt.addPiiToList(pii);
                    prompt.deobfuscatePrompt(pii, "{" + s + "}");
                }catch (Exception e) {
                    return new ResponseDTO(req.getId().getId(), 400, e.getMessage());
                }
            }else{
                return new ResponseDTO(req.getId().getId(), 404, "This parameter " + s + " does not " +
                        "exist.");
            }
        }
        return new ResponseDTO(req.getId().getId(), 200, prompt.getPrompt());
    }
}
