package com.midas.shootpointer.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /**
     * - ERROR CODE CONVENTION
     * 00       0           00
     * {Domain} {Package}   {ERROR_NUM}
     * <p>
     * - Domain
     * community:                  10
     * highlight:                  20
     * member:                     30
     * global:                     40

     * <p>
     * - Package
     * controller:                 1
     * service:                    2
     * repository:                 3
     * global:                     4
     * infrastructure:             5
     * <p>
     * - Error Num
     * 01 ~ 99 (Increasing Num From 01)
     * <p>
     * e.g. review, service, 의 1번 째 정의한 Exception -> 10201
     */

    INTERNAL_SERVER_ERROR(50000,HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 오류"),
    NOT_FOUND_END_POINT(40400,HttpStatus.NOT_FOUND,"존재하지 않은 API입니다."),

    // 302(member - service) part
    INVALID_KAKAO_AUTH_CODE(30201, HttpStatus.BAD_REQUEST, "카카오 토큰 요청 실패"),
    KAKAO_TOKEN_REQUEST_FAIL(30202, HttpStatus.BAD_REQUEST, "카카오 토큰 응답 파싱 실패"),
    KAKAO_TOKEN_RESPONSE_INVALID(30203, HttpStatus.BAD_REQUEST, "카카오 사용자 정보 요청 실패"),
    KAKAO_USERINFO_FAIL(30204, HttpStatus.BAD_REQUEST, "카카오 인증 코드가 유효하지 않음"),
    JSON_OBJECT_PARSE_FAIL(30205, HttpStatus.INTERNAL_SERVER_ERROR, "Json 파싱 실패"),

    // 404(global - util) part
    AES_ENCRYPT_FAIL(40401, HttpStatus.INTERNAL_SERVER_ERROR, "AES 암호화 실패"),
    AES_DECRYPT_FAIL(40402, HttpStatus.INTERNAL_SERVER_ERROR, "AES 복호화 실패"),
    JWT_CREATE_FAIL(40403, HttpStatus.INTERNAL_SERVER_ERROR, "JWT 생성 실패"),
    JWT_PARSE_FAIL(40404, HttpStatus.INTERNAL_SERVER_ERROR, "JWT 파싱 실패"),
    JWT_DECODE_FAIL(40405, HttpStatus.INTERNAL_SERVER_ERROR, "JWT 디코딩 실패"),
    SPRING_CONTEXT_BEAN_NOT_FOUND(40406, HttpStatus.INTERNAL_SERVER_ERROR, "스프링 빈을 찾을 수 없음"),


    //5XX
    FAILED_SEND_IMAGE_TO_OPENCV(20501,HttpStatus.GATEWAY_TIMEOUT,"OpenCV 등 번호 이미지 전송 실패")
    ;




    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
