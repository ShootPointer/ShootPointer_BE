package com.midas.shootpointer.domain.post.mapper;

import com.midas.shootpointer.domain.post.entity.PostDocument;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")  // dev 프로파일이 아닐 때만 활성화
public class PostElasticSearchMapperImpl implements PostElasticSearchMapper {

    @Override
    public PostDocument entityToDoc(PostEntity post) {
        return PostDocument.builder()
                .content(post.getContent())
                .title(post.getTitle())
                .hashTag(post.getHashTag().getName())
                .postId(post.getPostId())
                .likeCnt(post.getLikeCnt())
                .memberName(post.getMember().getUsername())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .highlightUrl(post.getHighlight().getHighlightURL())
                .build();
    }
}
