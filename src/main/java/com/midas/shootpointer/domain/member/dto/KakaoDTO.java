package com.midas.shootpointer.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoDTO {

    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
