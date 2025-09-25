package com.midas.shootpointer.domain.post.elasticsearch.mapper;

import com.midas.shootpointer.domain.post.dto.response.PostResponse;
import com.midas.shootpointer.domain.post.elasticsearch.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
public class PostElasticSearchMapperImpl implements PostElasticSearchMapper {
    @Override
    public PostResponse docToResponse(PostDocument doc) {
        return PostResponse.builder()
                .title(doc.getTitle())
                .content(doc.getContent())
                .createdAt(doc.getCreatedAt())
                .memberName(doc.getMemberName())
                .hashTag(doc.getHashTag())
                .highlightUrl(doc.getHighlightUrl())
                .likeCnt(doc.getLikeCnt())
                .modifiedAt(doc.getModifiedAt())
                .postId(doc.getPostId())
                .build();
    }

    @Override
    public PostDocument entityToDoc(PostEntity post) {
        return PostDocument.builder()
                .content(post.getContent())
                .title(post.getTitle())
                .hashTag(post.getHashTag())
                .postId(post.getPostId())
                .likeCnt(post.getLikeCnt())
                .memberName(post.getMember().getUsername())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .highlightUrl(post.getHighlight().getHighlightURL())
                .build();
    }
}
