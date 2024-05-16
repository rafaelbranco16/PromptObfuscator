package prompt.overshadowing.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.HuggingFaceTokenizer;
import dev.langchain4j.model.input.PromptTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import prompt.overshadowing.constants.Constants;
import prompt.overshadowing.dto.DesovershadowRequestDTO;
import prompt.overshadowing.dto.ObfuscateRequestDTO;
import prompt.overshadowing.dto.ResponseDTO;
import prompt.overshadowing.exceptions.*;
import prompt.overshadowing.fabrics.PiiFabric;
import prompt.overshadowing.fabrics.RequestFabric;
import prompt.overshadowing.model.ObfuscationRequest;
import prompt.overshadowing.model.Pii;
import prompt.overshadowing.model.Prompt;
import prompt.overshadowing.model.Request;
import prompt.overshadowing.repositories.PiiRepository;
import prompt.overshadowing.services.interfaces.ILlmModelService;
import prompt.overshadowing.services.interfaces.IOvershadowingService;
import prompt.overshadowing.services.interfaces.IPIIRevisionService;
import prompt.overshadowing.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class OvershadowingService implements IOvershadowingService {
    @Inject
    PiiRepository piiRepo;
    @Inject
    ILlmModelService modelService;
    @Inject
    IPIIRevisionService piiRevisionService;
    /**
     * Empty constructor
     */
    public OvershadowingService() {}

    /**
     * Overshadow the prompt
     * @param dto the content of the prompt
     * @return the overshadowed prompt
     */
    public ResponseDTO overshadowPrompt(ObfuscateRequestDTO dto) {
        ObfuscationRequest req = RequestFabric.create(dto.getPrompt(), dto.getKeywords());
        List<Prompt> prompts = new ArrayList<>();
        String overshadowed;
        try {
            this.modelService.changeModel(this.modelService.buildBaseModel());
            List<TextSegment> segments = this.splitPrompt(req.getPrompt().getPrompt());
            for(TextSegment segment : segments) {
                String llmMessage = this.getLlmResponse(segment.text(), req.getKeywords());
                ObfuscationRequest r = RequestFabric.create(segment.text(), req.getKeywords());

                if (llmMessage == null)
                    return new ResponseDTO(r.getId().getId(), 400, "Invalid response from Llm.");
                Prompt p = overshadow(llmMessage, r.getPrompt().getPrompt(), r.getId().getId());

                reviewPrompt(p, req.getKeywords(), prompts, segment);
            }
            overshadowed = connectPrompt(prompts);
        }catch (OvershadowingIllegalArgumentException
                | OvershadowingJsonParseException
                | LLMRequestException
                | InvalidSplitException e) {
            return new ResponseDTO(req.getId().getId(), 400, e.getMessage());
        }catch (CircuitBreakerOpenException e) {
            return new ResponseDTO(req.getId().getId(), 400,
                    "We couldnt reach the LM. Try again in 5 minutes.");
        }
        return new ResponseDTO(req.getId().getId(), 200, overshadowed);
    }

    /**
     * Asks the LLM the prompt
     *
     * @return the content from LLM Response
     */
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 5000)
    public String getLlmResponse(String p, List<String> keywords) throws LLMRequestException {
        try {
            String prompt = generatePromptTemplate(keywords, Constants.promptTemplate);

            return this.modelService.generate(prompt, p);
        }catch (RuntimeException e) {
            throw new LLMRequestException("We couldnt reach the LM. Try again in 5 minutes.");
        }
    }



    /**
     * Overshadow the prompt
     * @param llmResponse the response from the LLM
     *                    It must be of the type [{pii:_,type:_}]
     * @param prompt the prompt
     * @return the prompt overshadowed
     */
    public Prompt overshadow(String llmResponse, String prompt, String requestId)
            throws OvershadowingIllegalArgumentException, OvershadowingJsonParseException {
        Map<String, Integer> overshadowingKeys = new HashMap<>();
        //Converting it to JSON, so it's easier to work with
        Prompt promptObj = Prompt.create(prompt);
        ObjectMapper mapper = new ObjectMapper().configure(
                JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,
                true);
        try {
            JsonNode piiArray = mapper.readTree(llmResponse);
            int lastPosIndex = 0;
            if(piiArray != null && piiArray.isArray()) {
                for(JsonNode pii : piiArray) {
                    lastPosIndex = this.replacePrompt(promptObj, pii, overshadowingKeys, requestId, lastPosIndex);
                }
            }
            return promptObj;
        }catch (IllegalArgumentException e) {
            throw new OvershadowingIllegalArgumentException(e.getMessage());
        }catch (JsonProcessingException e) {
            throw new OvershadowingJsonParseException(e.getMessage());
        }
    }

    /**
     * Replace the content of the prompt based on the pii type
     * @param prompt the prompt
     * @param pii the pii as JsonNode
     * @param obfuscatedKeys a map of the type {String, Integer}
     */
    @Transactional
    public int replacePrompt(Prompt prompt, JsonNode pii,
                              Map<String, Integer> obfuscatedKeys, String requestId,
                              int lastPosIndex) {
        String value = pii.path("pii").asText().trim();
        String type = pii.path("type").asText().trim();
        String fiveAfter = pii.path("5after").asText().trim();


        Integer typeNumber = obfuscatedKeys.get(type);
        if (typeNumber != null) {
            obfuscatedKeys.replace(type, ++typeNumber);
        } else {
            typeNumber = 1;
            obfuscatedKeys.put(type, typeNumber);
        }
        if(type.trim().isBlank() || type.trim().isEmpty()){
            return lastPosIndex;
        }
        String stringToReplaceWith = "{" + type + "_" + typeNumber + "_" + requestId + "}";
        Pii p = piiRepo.findByContent(value);
        if (p == null){
            try {
                p = PiiFabric.create(stringToReplaceWith, value);
                piiRepo.persist(p);
            }catch (Exception ignored) {}
        }
        if(p != null) {
            try {
                prompt.addPiiToList(p);
                return prompt.replaceStringOnPrompt(p.getId(), p.getContent(), fiveAfter, lastPosIndex);
            }catch (IllegalArgumentException ignored) {}
        }
        return lastPosIndex;
    }

    /**
     * Generate the prompt template and add the keywords to it
     * @param keywords the list of PIIs that must be found
     * @return system prompt
     */
    private String generatePromptTemplate(List<String> keywords, PromptTemplate template) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("keywords", keywords);
        dev.langchain4j.model.input.Prompt prompt = template.apply(variables);
        return prompt.text();
    }

    /**
     * Splits the prompt into text segments
     * @param doc the document to be created. This is not a literal document.
     *            It can be a String. It has to be turned into a Document class,
     *            so it can be split using the LangChain library in order to keep
     *            some context.
     * @return a List of TextSegments
     * @throws InvalidSplitException if something's wrong happens
     */
    public List<TextSegment> splitPrompt(String doc) throws InvalidSplitException{
        Document docObj;
        try {
            docObj = new Document(doc);
        }catch (IllegalArgumentException e) {
            throw new InvalidSplitException(e.getMessage());
        }
        int maxSegmentSize, maxOverlapSize;

        try {
            maxOverlapSize = Integer.parseInt(Utils.getProperty("max.segment.size"));
        }catch (NumberFormatException e) {
            throw new InvalidSplitException("The max overlap size must be an integer");
        }

        try {
            maxSegmentSize = Integer.parseInt(Utils.getProperty("max.overlap.size"));
        }catch (NumberFormatException e) {
            throw new InvalidSplitException("The max overlap size must be an integer");
        }

        try{
            DocumentSplitter splitter = DocumentSplitters
                    .recursive(
                            maxOverlapSize,
                            maxSegmentSize,
                            new HuggingFaceTokenizer()
                    );

            return splitter.split(docObj);
        }catch (Exception e) {
            throw new InvalidSplitException(e.getMessage());
        }
    }

    /**
     * Connects all the segments into one
     * @param segments the segments
     * @return all the prompt
     */
    private String connectPrompt(List<Prompt> segments) {
        String s = "";
        for (Prompt p : segments) {
            s += p.getPrompt();
        }

        return s;
    }

    /**
     * Asks the Review Service to review the desired prompt
     * @param prompt the prompt to be reviewed
     * @param keywords the keywords
     * @param prompts the list with all the prompts
     * @param segment the segment of text to be analised
     * @throws LLMRequestException if something's wrong connecting to the LLM
     * @throws OvershadowingIllegalArgumentException if occurs any problem while obfuscating
     * @throws OvershadowingJsonParseException if there's any problem in the JSON returned by the LLM
     */
    public void reviewPrompt(Prompt prompt, List<String> keywords, List<Prompt> prompts, TextSegment segment)
            throws LLMRequestException, OvershadowingIllegalArgumentException, OvershadowingJsonParseException {
        boolean needsHigherRevision = this.piiRevisionService.needsHigherRevision(prompt, keywords);
        if(needsHigherRevision) {
            this.piiRevisionService.changeToRevisionModel();
            String llmRevision = this.piiRevisionService.LLMPromptRevision(prompt.getPrompt(), keywords);
            ObfuscationRequest r2 = RequestFabric.create(segment.text(), keywords);
            if (llmRevision == null)
                throw new LLMRequestException("Got no response from the LLM while reviewing");
            Prompt p2 = overshadow(llmRevision, prompt.getPrompt(), r2.getId().getId());
            prompt.addPiisToList(p2.getPiis());
            prompts.add(p2);
        }
    }
}
