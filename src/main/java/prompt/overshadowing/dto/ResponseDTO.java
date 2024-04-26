package prompt.overshadowing.dto;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ResponseDTO {
    @NonNull
    @Setter
    private String id;
    private int code;
    @NonNull
    private String prompt;

}
