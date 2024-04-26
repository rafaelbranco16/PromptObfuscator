package prompt.overshadowing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ObfuscateRequestDTO {
    @NonNull
    @JsonProperty("prompt")
    private String prompt;
    @NonNull
    @JsonProperty("keywords")
    private List<String> keywords;
}