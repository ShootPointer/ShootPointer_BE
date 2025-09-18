package com.midas.shootpointer.domain.post.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{
    private final PostManager postManager;

    @Transactional
    @Override
    public Long create(PostEntity post, Member member) {
        return postManager.save(member,post,post.getHighlight().getHighlightId());
    }

    @Transactional
    @Override
    public Long update(PostEntity post, Member member,Long postId) {
        return postManager.update(post,member,postId);
    }

    @Transactional
    @Override
    public Long delete(Member member, Long postId) {
        return postManager.delete(postId,member);
    }

}
