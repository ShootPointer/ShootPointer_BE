package com.midas.shootpointer.domain.post.elasticsearch;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Getter
@Document(indexName = "post",createIndex = true)
@Mapping(mappingPath = "elasticsearch/post-mapping.json")
@Setting(settingPath = "elasticsearch/post-setting.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDocument {
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String hashTag;

    @Field(type = FieldType.Long)
    private Long likeCnt;

    @Field(type = FieldType.Text)
    private String memberName;

    @Field(type = FieldType.Date,format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date,format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    private LocalDateTime modifiedAt;


    @Builder
    public PostDocument(Long id,String title,String content,String hashTag,Long likeCnt,String memberName,LocalDateTime createdAt,LocalDateTime modifiedAt){
        this.content=content;
        this.hashTag=hashTag;
        this.id=id;
        this.title=title;
        this.memberName=memberName;
        this.likeCnt=likeCnt;
        this.createdAt=createdAt;
        this.modifiedAt=modifiedAt;
    }

    public static PostDocument of(PostEntity post){
        return PostDocument.builder()
                .content(post.getContent())
                .title(post.getTitle())
                .hashTag(post.getHashTag().getName())
                .id(post.getPostId())
                .likeCnt(post.getLikeCnt())
                .memberName(post.getMember().getUsername())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }
}
