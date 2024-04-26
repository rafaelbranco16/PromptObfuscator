package prompt.overshadowing.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import prompt.overshadowing.services.interfaces.IFileToPromptService;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

public class TxtToPromptService implements IFileToPromptService {
    public String convertToString(String filePath) {
        Path documentFile = toPath(filePath);
        Document document = loadDocument(documentFile, new TextDocumentParser());
        return document.text();
    }

    public Document convertToDocument(String filePath) {
        Path documentFile = toPath(filePath);
        return loadDocument(documentFile, new TextDocumentParser());
    }

    private Path toPath(String fileName) {
        try {
            URL fileUrl = this.getClass().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
