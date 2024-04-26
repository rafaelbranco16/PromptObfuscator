package prompt.overshadowing.services.interfaces;

import dev.langchain4j.data.document.Document;

public interface IFileToPromptService {
    String convertToString(String filePath);
    Document convertToDocument(String filePath);
}
