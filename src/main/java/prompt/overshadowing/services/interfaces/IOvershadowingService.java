package prompt.overshadowing.services.interfaces;

import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ObfuscateRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.exceptions.LLMRequestException;

import java.util.List;

public interface IOvershadowingService {
    ResponseDTO overshadowPrompt(ObfuscateRequestDTO dto);
    String getLlmResponse(String p, List<String> keywords) throws LLMRequestException;
    ResponseDTO deobfuscate(DesovershadowRequestDTO dto);
}
