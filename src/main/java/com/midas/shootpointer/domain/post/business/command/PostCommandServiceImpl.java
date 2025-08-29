package com.midas.shootpointer.domain.post.business.command;

import com.midas.shootpointer.domain.highlight.entity.HighlightEntity;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{
    private final PostManager postManager;
    private final PostMapper mapper;

    @Transactional
    @Override
    public Long create(PostRequest request, Member member) {
        PostEntity postEntity=mapper.dtoToEntity(request,member);
        return postManager.save(member,postEntity,request.getHighlightId());
    }

    @Transactional
    @Override
    public Long update(PostRequest request, Member member,Long postId) {
        return postManager.update(request,member,postId);
    }

    @Transactional
    @Override
    public Long delete(Member member, Long postId) {
        return postManager.delete(postId,member);
    }

}
