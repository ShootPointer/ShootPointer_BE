package com.midas.shootpointer.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAutoCompleteResponse {
    @JsonProperty("suggest")
    private String suggest;

    private SearchAutoCompleteResponse(String keyword){
        this.suggest=keyword;
    }
    public static SearchAutoCompleteResponse of(String title){
        return new SearchAutoCompleteResponse(title);
    }
}
