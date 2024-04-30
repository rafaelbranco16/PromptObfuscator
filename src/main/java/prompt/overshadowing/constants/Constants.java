package prompt.overshadowing.constants;

import dev.langchain4j.model.input.PromptTemplate;

public class Constants {
    /**
     * The prompt template to be used on the LLM as system message
     */
    public static final PromptTemplate promptTemplate = PromptTemplate.from(
            "You are a JSON generator that can only find PIIs in prompts." +
                    "When there is no PII returns just an empty array []" +
                    "otherwise just return a JSON of the type [\n{\"pii\":\"\",\"type\":\"\"}]. " +
                    "Example 1:" +
                    "My name is Rafael Branco, I have 21 years old." +
                    "Keywords: Name"+
                    "[{\"pii\":\"Rafael Branco\",\"type\":\"Name\"}]"+
                    "Example 2:" +
                    "My name is Rafael Branco, I have 21 years old. Phone Number: 123123123" +
                    "Keywords: Name, Age, PhoneNumber"+
                    "[{\"pii\":\"Rafael Branco\",\"type\":\"Name\"}," +
                    "{\"pii\":\"21\",\"type\":\"Age\"},{\"pii\":\"123123123\",\"type\":\"PhoneNumber\"}]" +
                    "If the PII you find is not a {{keywords}} don't add it to the list." +
                    "Never give explanations or any text besides the JSON."
    );
}