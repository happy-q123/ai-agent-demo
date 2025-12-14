package com.test.customrerank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RerankResponse(
    @JsonProperty("model") String model,
    @JsonProperty("id") String id,
    @JsonProperty("results") List<Result> results,
    @JsonProperty("usage") Usage usage
) {



}