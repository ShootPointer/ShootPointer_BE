package com.midas.shootpointer.domain.post.business.command;

import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.business.PostManager;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{
    private final PostManager postManager;
    private final PostMapper mapper;


    @Override
    public Long create(PostRequest request, Member member) {
        PostEntity postEntity=mapper.dtoToEntity(request,member);
        return postManager.save(member,postEntity,request.getHighlightId());
    }


    //TODO:jwt 유효성 검사 필요! -> 박재성.
   /* private Member getMember(String token){
        if(jwtUtil.getMemberId(token))
    }*/
}
