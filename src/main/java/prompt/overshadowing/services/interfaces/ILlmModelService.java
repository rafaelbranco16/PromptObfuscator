package prompt.overshadowing.services.interfaces;

import dev.langchain4j.model.chat.ChatLanguageModel;
import prompt.overshadowing.exceptions.LLMRequestException;

public interface ILlmModelService {
    ChatLanguageModel buildModel();
    String generate(String sysMessage, String userMessage) throws LLMRequestException;
}
