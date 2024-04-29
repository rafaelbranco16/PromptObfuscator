package prompt.overshadowing.model.langfuse;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import prompt.overshadowing.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChatPrompt {
    private final String type;
    private final String name;
    private final boolean isActive;
    private final List<PromptContent> prompt;
    private final Object config;
    public ChatPrompt(String config) {
        this.type = Utils.getProperty("langfuse.prompt.type");
        this.name = Utils.getProperty("langfuse.prompt.name.prefix") +
                UUID.randomUUID().toString().replace("-", "");
        this.isActive = true;
        this.prompt = new ArrayList<>();
        this.config = config;
    }
    public void addPrompt(String role, String content) {
        prompt.add(new PromptContent(role, content));
    }
}

@Getter
class PromptContent {
    private final String role;
    private final String content;
    public PromptContent(String role, String content) {
        this.role = role;
        this.content = content;
    }
}