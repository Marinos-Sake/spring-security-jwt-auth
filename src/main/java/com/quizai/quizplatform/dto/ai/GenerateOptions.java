package com.quizai.quizplatform.dto.ai;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class GenerateOptions {

    @JsonProperty("num_ctx")
    private Integer numCtx;

    @JsonProperty("num_predict")
    private Integer numPredict;

    private Double temperature;

    @JsonProperty("top_p")
    private Double topP;

    @JsonProperty("repeat_penalty")
    private Double repeatPenalty;
}
