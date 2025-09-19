package com.midas.shootpointer.domain.post.elasticsearch;

import com.midas.shootpointer.domain.post.entity.PostEntity;
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

    @Field(type = FieldType.Long)
    private Long likeCnt;

    @Field(type = FieldType.Text)
    private String memberName;

    @Builder
    public PostDocument(Long id,String title,String content,String hashTag,Long likeCnt,String memberName){
        this.content=content;
        this.hashTag=hashTag;
        this.id=id;
        this.title=title;
        this.memberName=memberName;
        this.likeCnt=likeCnt;
    }

    public static PostDocument of(PostEntity post){
        return PostDocument.builder()
                .content(post.getContent())
                .title(post.getTitle())
                .hashTag(post.getHashTag().getName())
                .id(post.getPostId())
                .likeCnt(post.getLikeCnt())
                .memberName(post.getMember().getUsername())
                .build();
    }
}
