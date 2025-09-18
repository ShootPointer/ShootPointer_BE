package com.midas.shootpointer.domain.like.helper;

import com.midas.shootpointer.domain.like.repository.LikeQueryRepository;
import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeValidationImplTest {
    @InjectMocks
    private LikeValidationImpl likeValidation;

    @Mock
    private LikeQueryRepository likeQueryRepository;


    @Test
    @DisplayName("해당 게시물에 대해서 이미 좋아요를 누른 상태이면 INVALID_CREATE_LIKE를 반환합니다._FAIL")
    void isValidCreateLike_FAIL(){
        //given
        UUID memberId=UUID.randomUUID();
        Long postId=1111L;

        //when
        when(likeQueryRepository.existByMemberIdAndPostId(memberId,postId))
                .thenReturn(true);
        CustomException customException=catchThrowableOfType(()->
                likeValidation.isValidCreateLike(memberId,postId),
                CustomException.class
        );

        //then
        assertThat(customException).isNotNull();
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.INVALID_CREATE_LIKE);
    }

    @Test
    @DisplayName("해당 게시물에 대해서 좋아요를 누르지 않은 상태이면 에외를 발생시키지 않습니다._SUCCESS")
    void isValidCreateLike_SUCCESS(){
        //given
        UUID memberId=UUID.randomUUID();
        Long postId=1111L;

        //when
        when(likeQueryRepository.existByMemberIdAndPostId(memberId,postId))
                .thenReturn(false);
        CustomException customException=catchThrowableOfType(()->
                        likeValidation.isValidCreateLike(memberId,postId),
                CustomException.class
        );

        //then
        assertThat(customException).isNull();
    }

}