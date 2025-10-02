package com.midas.shootpointer.domain.post.helper.elastic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
class PostElasticSearchValidatorImplTest {
    @InjectMocks
    private PostElasticSearchValidatorImpl elasticSearchValidator;

    @Test
    @DisplayName("검색 키워드가 #로 시작하면 true를 반환합니다.")
    void isHashTagSearch() {
        //given
        String keyword1="#input";
        String keyword2="i#nput";
        String keyword3="# input";

        //when
        boolean bool1=elasticSearchValidator.isHashTagSearch(keyword1);
        boolean bool2=elasticSearchValidator.isHashTagSearch(keyword2);
        boolean bool3=elasticSearchValidator.isHashTagSearch(keyword3);

        //then
        assertThat(bool1).isTrue();
        assertThat(bool2).isFalse();
        assertThat(bool3).isTrue();
    }
}