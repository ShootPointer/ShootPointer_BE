package com.midas.shootpointer.domain.post.helper;

import com.midas.shootpointer.domain.highlight.repository.HighlightQueryRepository;
import com.midas.shootpointer.domain.member.entity.Member;
import com.midas.shootpointer.domain.post.entity.HashTag;
import com.midas.shootpointer.domain.post.entity.PostEntity;
import com.midas.shootpointer.domain.post.repository.PostQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostValidationImpl implements PostValidation{
    private final HighlightQueryRepository highlightQueryRepository;

    /*==========================
    *
    *PostValidationImpl
    *
    * @parm member : 멤버 객체 , highlightId : 하이라이트 Id
    * @return 유저의 하이라이트 영상이면 true, 아니면 CustomException 발생.
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 28.
    *
    ==========================**/
    @Override
    public void isValidateHighlightId(Member member, UUID highlightId) {
        boolean isValid=highlightQueryRepository.existsByHighlightIdAndMember(highlightId,member.getMemberId());
        if(!isValid) throw new CustomException(ErrorCode.IS_NOT_CORRECT_MEMBERS_HIGHLIGHT_ID);
    }

    /*==========================
    *
    *PostValidationImpl
    *
    * @parm Object 값
    * @return 기존 설정한 HashTag ENUM의 형태가 아니면 CustomException 발생
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 28.
    *
    ==========================**/
    @Override
    public void isValidPostHashTag(Object o) {
        if(o.getClass() != HashTag.class) throw new CustomException(ErrorCode.IS_NOT_CORRECT_HASH_TAG);
    }

    /*==========================
    *
    *PostValidationImpl
    *
    * @parm postEntity 게시물 엔티티
    * @return 삭제된 게시물 접근 시 CustomException 발생
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 29.
    *
    ==========================**/
    @Override
    public void isDeleted(PostEntity postEntity) {
        boolean deleted=postEntity.getIsDeleted();
        if(deleted) throw new CustomException(ErrorCode.DELETED_POST);
    }


    /*==========================
    *
    *PostValidationImpl
    *
    * @parm postEntity 게시물 엔티티 member 멤버 엔티티
    * @return 멤버의 게시물 접근 시 CustomException 발생
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 8. 29.
    *
    ==========================**/
    @Override
    public void isMembersPost(PostEntity postEntity, Member member) {
        if(!postEntity.getMember().getMemberId().equals(member.getMemberId())){
            throw new CustomException(ErrorCode.IS_NOT_MEMBERS_POST);
        }
    }


}
