package com.midas.shootpointer.domain.post.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Document(indexName = "posts",createIndex = true)
@Setting(settingPath = "elasticsearch/post-setting.json")
@Mapping(mappingPath = "elasticsearch/post-mapping.json")
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

    @Builder
    public PostDocument(Long id,String title,String content,String hashTag){
        this.content=content;
        this.hashTag=hashTag;
        this.id=id;
        this.title=title;
    }

    public static PostDocument of(PostEntity post){
        return PostDocument.builder()
                .content(post.getContent())
                .title(post.getTitle())
                .hashTag(post.getHashTag().getName())
                .id(post.getPostId())
                .build();
    }
}
