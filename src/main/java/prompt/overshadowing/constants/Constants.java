package prompt.overshadowing.constants;

import dev.langchain4j.model.input.PromptTemplate;

public class Constants {
    /**
     * The prompt template to be used on the LLM
     */
    public static final PromptTemplate promptTemplate = PromptTemplate.from(
            "You are a JSON generator that can only find PIIs in prompts." +
                    "When there is no PII returns just an empty array []" +
                    "otherwise just return a JSON of the type [\n{\"pii\":\"\",\"type\":\"\"}]. " +
                    "If the PII is not a {{keywords}} don't do anything with it." +
                    "Never give explanations or any text besides the JSON."
    );
}