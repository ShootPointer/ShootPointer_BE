package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.repository.LikeQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LikeValidationImpl implements LikeValidation{
    private final LikeQueryRepository likeQueryRepository;
    /*==========================
    *
    *LikeValidationImpl
    *
    * @parm memberId : 유저의 Id값, postId : 게시글 Id
    * @return 해당 게시물에 대해서 이미 좋아요를 누른 상태이면 INVALID_CREATE_LIKE를 반환합니다.
    * @author kimdoyeon
    * @version 1.0.0
    * @date 25. 9. 1.
    *
    ==========================**/
    @Override
    public void isValidCreateLike(UUID memberId, Long postId) {
        boolean isValid=likeQueryRepository.existByMemberIdAndPostId(memberId,postId);
        /**
         * 이미 좋아요를 누른 경우
         */
        if(isValid) throw new CustomException(ErrorCode.INVALID_CREATE_LIKE);
    }

}
