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
     * backnumber :                50
     * post:                       60
     * like:                       70
     * comment:                    80

     * <p>
     * - Package
     * entity                      0
     * controller:                 1
     * service:                    2
     * repository:                 3
     * global:                     4
     * infrastructure:             5
     * helper                      6
     * business                    7
     * <p>
     * - Error Num
     * 01 ~ 99 (Increasing Num From 01)
     * <p>
     * e.g. review, service, 의 1번 째 정의한 Exception -> 10201
     */

    INTERNAL_SERVER_ERROR(50000,HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 오류"),
    NOT_FOUND_END_POINT(40400,HttpStatus.NOT_FOUND,"존재하지 않은 API입니다."),
    
    
    NOT_FOUND_HIGHLIGHT_ID(20201,HttpStatus.NOT_FOUND,"해당 하이라이트 영상을 찾을 수 없습니다."),
    NOT_MATCH_HIGHLIGHT_VIDEO(20202,HttpStatus.BAD_REQUEST,"잘못된 요청입니다."),
    INVALID_FILE_TYPE(20203,HttpStatus.BAD_REQUEST,"지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(20204,HttpStatus.BAD_REQUEST,"파일의 크기가 초과했습니다.(제한 : 100MB)"),
    FILE_UPLOAD_FAILED(20205,HttpStatus.BAD_REQUEST,"파일 업로드에 실패했습니다."),

    //300(member - entity) part
    IS_AGGREGATION_TRUE(30001,HttpStatus.BAD_REQUEST,"이미 하이라이트 영상 정보 수집에 동의했습니다."),
    // 302(member - service) part
    INVALID_KAKAO_AUTH_CODE(30201, HttpStatus.BAD_REQUEST, "카카오 토큰 요청 실패"),
    KAKAO_TOKEN_REQUEST_FAIL(30202, HttpStatus.BAD_REQUEST, "카카오 토큰 응답 파싱 실패"),
    KAKAO_TOKEN_RESPONSE_INVALID(30203, HttpStatus.BAD_REQUEST, "카카오 사용자 정보 요청 실패"),
    KAKAO_USERINFO_FAIL(30204, HttpStatus.BAD_REQUEST, "카카오 인증 코드가 유효하지 않음"),
    JSON_OBJECT_PARSE_FAIL(30205, HttpStatus.INTERNAL_SERVER_ERROR, "Json 파싱 실패"),
    
    // 306(member - helper) part
    INVALID_KAKAO_USER_INFO(30601, HttpStatus.BAD_REQUEST, "카카오 사용자 정보가 유효하지 않습니다."),
    INVALID_EMAIL(30602, HttpStatus.BAD_REQUEST, "이메일이 비어있거나 유효하지 않습니다."),
    INVALID_NICKNAME(30603, HttpStatus.BAD_REQUEST, "닉네임이 비어있거나 유효하지 않습니다."),
    INVALID_EMAIL_FORMAT(30604, HttpStatus.BAD_REQUEST, "이메일 형식이 잘못되었습니다."),
    KAKAO_AUTH_ERROR(30605, HttpStatus.BAD_REQUEST, "카카오 인증 과정에서 오류가 발생했습니다."),
    DUPLICATE_EMAIL(30606, HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    UNAUTHORIZED_MEMBER_ACCESS(30607, HttpStatus.FORBIDDEN, "해당 멤버에 대한 접근 권한이 없습니다."),

    // 404(global - util) part
    AES_ENCRYPT_FAIL(40401, HttpStatus.INTERNAL_SERVER_ERROR, "AES 암호화 실패"),
    AES_DECRYPT_FAIL(40402, HttpStatus.INTERNAL_SERVER_ERROR, "AES 복호화 실패"),
    JWT_CREATE_FAIL(40403, HttpStatus.INTERNAL_SERVER_ERROR, "JWT 생성 실패"),
    JWT_PARSE_FAIL(40404, HttpStatus.INTERNAL_SERVER_ERROR, "JWT 파싱 실패"),
    JWT_DECODE_FAIL(40405, HttpStatus.INTERNAL_SERVER_ERROR, "JWT 디코딩 실패"),
    SPRING_CONTEXT_BEAN_NOT_FOUND(40406, HttpStatus.INTERNAL_SERVER_ERROR, "스프링 빈을 찾을 수 없음"),
    JWT_MEMBER_ID_INVALID(40407, HttpStatus.INTERNAL_SERVER_ERROR, "JWT에서 memberId(UUID) 추출 실패"),
    JWT_REQUEST_NOT_FOUND(40408, HttpStatus.NOT_FOUND, "요청 Context를 찾을 수 없음"),
    JWT_HEADER_NOT_FOUND(40409, HttpStatus.NOT_FOUND, "Authorization 헤더가 없음"),

    INTERNAL_ERROR_OF_PYTHON_SERVER(40501,HttpStatus.BAD_REQUEST,"파이썬 서버 내부 오류입니다."),

    //5XX
    FAILED_SEND_IMAGE_TO_OPENCV(20501,HttpStatus.GATEWAY_TIMEOUT,"OpenCV 등 번호 이미지 전송 실패"),
    FAILED_POST_API_RETRY_TO_OPENCV(20502, HttpStatus.REQUEST_TIMEOUT, "OpenCV 파일 전송 횟수가 초과했습니다."),


    // 502(backnumber - service) part
    //* TODO : 현재 멤버 관련 로직이 Kakao 밖에 없어서 Member 도메인에 예외처리가 처음 생긴게 BackNumber 도메인임. << 이 부분은 추후에 Member 도메인에 로직 생기면 바꿀게요~
    MEMBER_NOT_FOUND(50201, HttpStatus.NOT_FOUND, "Member를 찾을 수 없음."),


    //706(like - helper)
    INVALID_CREATE_LIKE(70601,HttpStatus.BAD_REQUEST,"이미 좋아요한 게시글입니다."),
    INVALID_DELETE_LIKE(70602,HttpStatus.BAD_REQUEST,"잘못된 좋아요 요청입니다."),
    NOT_FOUND_LIKE(70603,HttpStatus.NOT_FOUND,"좋아요를 찾을 수 없습니다."),

    //200(highlight - entity) part
    IS_NOT_CORRECT_MEMBERS_HIGHLIGHT_ID(20001,HttpStatus.FORBIDDEN,"유저의 하이라이트 영상이 아닙니다."),
    EXISTED_SELECTED(20002,HttpStatus.BAD_REQUEST,"이미 선택된 하이라이트 영상입니다."),

    //206(highlight - helper) part,
    IS_NOT_CORRECT_HASH_TAG(20602,HttpStatus.BAD_REQUEST,"잘못된 카테고리 입력입니다."),
    NOT_EXIST_HIGHLIGHT(20603,HttpStatus.FORBIDDEN,"존재하지 않는 하이라이트 영상입니다."),

    //606(post-helper) part
    DELETED_POST(60601,HttpStatus.FORBIDDEN,"삭제된 게시물입니다."),
    IS_NOT_MEMBERS_POST(60602,HttpStatus.FORBIDDEN,"유저의 게시물이 아닙니다."),
    NOT_EXIST_ORDER_TYPE(60603,HttpStatus.BAD_REQUEST,"잘못된 조회 방식입니다."),
    IS_NOT_VALID_SIZE(60604, HttpStatus.BAD_REQUEST,"유효하지 않은 요청입니다."),

    //607(post-business) part
    IS_NOT_EXIST_POST(60701,HttpStatus.FORBIDDEN,"존재하지 않는 게시물입니다."),
    
    // 806(comment - helper) part
    IS_NOT_EXIST_COMMENT(80601, HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    FORBIDDEN_COMMENT_ACCESS(80602, HttpStatus.FORBIDDEN, "댓글 접근 권한이 없습니다."),
    INVALID_INPUT_VALUE(80603, HttpStatus.BAD_REQUEST, "올바른 입력값이 아닙니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
