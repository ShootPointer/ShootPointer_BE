package com.midas.shootpointer.domain.post.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.midas.shootpointer.domain.post.entity.HashTag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
@Getter
@Document(indexName = "post",createIndex = true)
@Mapping(mappingPath = "elasticsearch/post-mapping.json")
@Setting(settingPath = "elasticsearch/post-setting.json")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDocument {
    @Id
    @Field(type = FieldType.Long)
    private Long postId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private HashTag hashTag;

    @Field(type = FieldType.Long)
    private Long likeCnt;

    @Field(type = FieldType.Text)
    private String memberName;

    @Field(type = FieldType.Date,format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date,format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    private LocalDateTime modifiedAt;

    @Field(type = FieldType.Text)
    private String highlightUrl;

    @Builder
    public PostDocument(Long postId,
                        String title,
                        String content,
                        HashTag hashTag,
                        Long likeCnt,
                        String memberName,
                        LocalDateTime createdAt,
                        LocalDateTime modifiedAt,
                        String highlightUrl
    ){
        this.content=content;
        this.hashTag=hashTag;
        this.postId=postId;
        this.title=title;
        this.memberName=memberName;
        this.likeCnt=likeCnt;
        this.createdAt=createdAt;
        this.modifiedAt=modifiedAt;
        this.highlightUrl=highlightUrl;
    }
}
