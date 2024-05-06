package prompt.overshadowing.services.interfaces;

import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;

public interface IDeobfuscateService {
    ResponseDTO deobfuscate(DesovershadowRequestDTO dto);
}
