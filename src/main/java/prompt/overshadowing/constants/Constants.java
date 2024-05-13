package prompt.overshadowing.constants;

import dev.langchain4j.model.input.PromptTemplate;

public class Constants {
    /**
     * The prompt template to be used on the LLM as system message
     */
    public static final PromptTemplate promptTemplate = PromptTemplate.from(
            "You are a JSON generator that can only find PIIs in prompts." +
                    "When there is no PII returns just an empty array []" +
                    "otherwise just return a JSON of the type [\n{\"pii\":\"\",\"type\":\"\",\"5after\":\"\"}]. " +
                    "The 5after field must have the five characters after where the PII. " +
                    "Give those characters exactly as they are in the text." +
                    "Place the PII for the order they appear and place it again if repeated." +
                    "If the PII you find is not a {{keywords}} don't do anything with it." +
                    "Never give explanations or any text besides the JSON."
    );
    /**
     * The prompt template for the revision
     */
    public static final PromptTemplate llmPromptRevisionTemplate = PromptTemplate.from(
            "You are JSON generator that can only return JSONs in the following format:" +
                    "[\n{\"pii\":\"\",\"type\":\"\"}] and if there's no PII returns just an empty array []" +
                    "I want you to review the text and verify if there is any PII that is not identified. " +
                    "A PII is consider identified when it is between \"{}\" and it you cannot add it to the JSON list." +
                    "Example2:"+
                    "My name is Rafael. " +
                    "You should return [{\"pii\":\"Rafael\", \"type\":\"name\"}]" +
                    "Example:"+
                    "My name is [name_1_GUID]. " +
                    "You should return []" +
                    "If the PII you find is not a {{keywords}} don't do anything with it." +
                    "Never give explanations or any text besides the JSON."
    );
}