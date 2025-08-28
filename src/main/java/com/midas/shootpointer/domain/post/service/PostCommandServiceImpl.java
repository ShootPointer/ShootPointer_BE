package com.midas.shootpointer.domain.post.service;

import com.midas.shootpointer.domain.highlight.repository.HighlightCommandRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.member.repository.MemberRepository;
import com.midas.shootpointer.domain.post.dto.PostRequest;
import com.midas.shootpointer.domain.post.dto.PostResponse;
import com.midas.shootpointer.domain.post.mapper.PostMapper;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.global.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{
    private final PostCommandRepository postCommandRepository;
    private final MemberRepository memberRepository;
    private final PostMapper mapper;


    @Override
    public Long create(PostRequest request, String token) {

        /*
         * 1. Highlight URL이 유저의 영상으로 일치하는가.
         *
         * 2. 해시태그가 올바른가.
         */


        return null;
    }


    //TODO:jwt 유효성 검사 필요! -> 박재성.
   /* private Member getMember(String token){
        if(jwtUtil.getMemberId(token))
    }*/
}
