package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.entity.LikeEntity;
import com.midas.shootpointer.domain.like.repository.LikeCommandRepository;
import com.midas.shootpointer.domain.like.repository.LikeQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostCommandRepository;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeUtilImpl implements LikeUtil {
    private final PostCommandRepository postCommandRepository;
    private final LikeQueryRepository likeQueryRepository;
    private final LikeCommandRepository likeCommandRepository;
    private final PostQueryRepository postQueryRepository;
    private final JdbcTemplate jdbcTemplate;

    /*==========================
    *
    *LikeUtilImpl
    * 게시물의 좋아요 개수 증가.
    * @parm post : 게시물 객체
    * @return void
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 2.
    *
    ==========================**/
    @Override
    @Transactional
    public void increaseLikeCnt(Long postId) {
       //likeCommandRepository.increasesLikeCnt(post.getPostId());


        /**
         * Optimistic Lock 재시도 로직.
         */
        int tryCnt=0;
        boolean success=false;

        while (tryCnt<=100 && !success){
            try {
                PostEntity post=postQueryRepository.findById(postId)
                        .orElseThrow(()->new CustomException(ErrorCode.IS_NOT_EXIST_POST));

                int updatedCount=postCommandRepository.updatedCount(postId,post.getVersion());
                //성공한 경우
                if(updatedCount!=0) success=true;
                else tryCnt++;

            }catch (OptimisticLockException e){
                tryCnt++;
                log.error("Optimistic Lock 실패 : {}", tryCnt);
            }
        }


        /**
         * 재시도 횟수 초과.
         */
        if(!success){
            throw new RuntimeException("Optimistic Lock 재시도 초과");
        }
    }

    /*==========================
    *
    *LikeUtilImpl
    * 게시물의 좋아요 개수 감소.
    * @parm post : 게시물 객체
    * @return void
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 2.
    *
    ==========================**/
    @Override
    public void decreaseLikeCnt(PostEntity post) {
        post.deleteLike();
        postCommandRepository.save(post);
    }

    /*==========================
    *
    *LikeUtilImpl
    * 좋아요 객체를 저장.
    * @parm post : 게시물 객체 / member : 유저 객체
    * @return 저장된 좋아요 객체
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 2.
    *
    ==========================**/
    @Override
    public LikeEntity createLike(PostEntity post, Member member) {
        LikeEntity likeEntity=LikeEntity.builder()
                .member(member)
                .post(post)
                .build();
        return likeCommandRepository.save(likeEntity);
    }

    /*==========================
    *
    *LikeUtilImpl
    * like를 삭제.
    * @parm like : 좋아요 객체
    * @return void
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 2.
    *
    ==========================**/
    @Override
    public void deleteLike(LikeEntity like) {
        likeCommandRepository.delete(like);
    }

    /*==========================
    *
    *LikeUtilImpl
    * memberId와 postId로 like 객체 조회.
    * @parm memberId : 유저Id / postId : 게시판 Id
    * @return like 객체.
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 2.
    *
    ==========================**/
    @Override
    public LikeEntity findByPostIdAndMemberId(UUID memberId, Long postId) {
        return likeQueryRepository.findByPostIdAndMemberId(postId,memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_LIKE));
    }
}
