package com.example.demo.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),


    /**
     * 400 : Request 오류, Response 오류
     */
    // Common
    REQUEST_ERROR(false, HttpStatus.BAD_REQUEST.value(), "입력값을 확인해주세요."),
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,HttpStatus.FORBIDDEN.value(),"권한이 없는 유저의 접근입니다."),
    RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),

    // users
    USERS_EMPTY_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_PHONENUMBER(false, HttpStatus.BAD_REQUEST.value(), "전화번호를 입력해주세요"),
    POST_USERS_INVALID_PHONENUMBER(false, HttpStatus.BAD_REQUEST.value(), "전화번호 형식을 확인해주세요"),
    POST_USERS_EXISTS_PHONENUBMER(false,HttpStatus.BAD_REQUEST.value(),"중복된 전화번호 입니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"인증번호가 맞지 않습니다."),

    //[POST] /product
    POST_PRODUCT_EMPTY_TITLE(false,HttpStatus.BAD_REQUEST.value(), "상품 이름을 입력해주세요"),
    POST_PRODUCT_EMPTY_CONTENT(false,HttpStatus.BAD_REQUEST.value(), "상품 내용을 입력해주세요"),
    POST_PRODUCT_EMPTY_Image(false,HttpStatus.BAD_REQUEST.value(), "이미지를 넣어주세요"),
    POST_PRODUCT_EMPTY_PRICE(false,HttpStatus.BAD_REQUEST.value(), "가격을 입력해주세요"),
    /**
     * 50 : Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),

    MODIFY_FAIL_PRODUCT(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"상품 수정 실패"),

    //[PATCH] /product/about/{productIdx}
    INVALID_PRODUCTID(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"존재하지 않는 상품입니다."),
    INVALID_PRODUCTID_OR_USERID(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"존재하지 않는 상품이거나 유저입니다."),

    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),

    INVALID_PAGE_NUMBER(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"존재하지 않는 페이지 입니다."),
    NO_PRODUCTS_FOUND(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"페이지에 존재하는 상품을 찾지 못했습니다."),

    NO_USER_FOR_PHONE_NUM(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"회원이 아닙니다."),
    NO_REGION_FOR_USER(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저에 해당하는 지역을 찾지 못했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
