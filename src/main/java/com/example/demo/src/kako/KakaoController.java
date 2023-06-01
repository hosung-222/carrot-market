package com.example.demo.src.kako;

import com.example.demo.src.kako.model.OAuthToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("")
public class KakaoController {

    @GetMapping("/oauth/callback/kakao")
    public @ResponseBody String kakaoCallBack(String code){

        //Post 방식으로 key=value 데이터를 요청(to kakao)
        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type","application/x-www-form-urlencoded");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> parms = new LinkedMultiValueMap<>();
        parms.add("grant_type","authorization_code");
        parms.add("client_id","ec4f853d1fed61aab05e54a06d071668");
        parms.add("redirect_uri","https://castleserver.shop/oauth/callback/kakao");
        parms.add("code",code);

        // HttpHeader , HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(parms,httpHeaders);

        //Http 요청하기 - POST방식- response 변수의 응답을 받음
        ResponseEntity responseEntity = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue((String)responseEntity.getBody(), OAuthToken.class);
        }catch (JsonProcessingException e ){
            e.printStackTrace();
        }
        System.out.println(oAuthToken.getAccess_token());
        return "카카오 인증완료" + responseEntity.getBody();
    }
}
