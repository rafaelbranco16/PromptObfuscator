package prompt.overshadowing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class DesovershadowRequestDTO {
    public DesovershadowRequestDTO() {}
    @NonNull
    @JsonProperty("prompt")
    private String prompt;
}
