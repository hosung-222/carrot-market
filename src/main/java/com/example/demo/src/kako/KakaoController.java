package com.example.demo.src.kako;

import com.example.demo.config.BaseException;
import com.example.demo.src.kako.model.KakaoProfile;
import com.example.demo.src.kako.model.OAuthToken;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.transaction.Transactional;


@RestController
@RequestMapping("")
public class KakaoController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserDao userDao;

    private final KakaoTokenJsonData kakaoTokenJsonData;

    public KakaoController(KakaoTokenJsonData kakaoTokenJsonData) {
        this.kakaoTokenJsonData = kakaoTokenJsonData;
    }

    @Transactional
    @GetMapping("/oauth/callback/kakao")
    public @ResponseBody String kakaoCallBack(String code) throws JsonProcessingException, BaseException {

        //Post 방식으로 key=value 데이터를 요청(to kakao)
        RestTemplate rt = new RestTemplate();
        //HttpHeader 오브젝트 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type","application/x-www-form-urlencoded");

        //HttpBody 오브젝트 생성
//        MultiValueMap<String, String> parms = new LinkedMultiValueMap<>();
//        parms.add("grant_type","authorization_code");
//        parms.add("client_id","ec4f853d1fed61aab05e54a06d071668");
//        parms.add("redirect_uri","https://castleserver.shop/oauth/callback/kakao");
//        parms.add("code",code);

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> parms = new LinkedMultiValueMap<>();
        parms.add("grant_type","authorization_code");
        parms.add("client_id","668eca661ab04a292d2e0b1fd6af1d4b");
        parms.add("redirect_uri","http://localhost:9001/oauth/callback/kakao");
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
        OAuthToken oAuthToken;
        String body = (String)responseEntity.getBody();
        oAuthToken = objectMapper.readValue(body, OAuthToken.class);
        System.out.println("카카오 액세스 토큰" + oAuthToken.getAccess_token());





        // 토큰
        RestTemplate rt2 = new RestTemplate();
        HttpHeaders hearder2 = new HttpHeaders();
        hearder2.add("Content-type","application/x-www-form-urlencoded");
        hearder2.add("Authorization","Bearer "+oAuthToken.getAccess_token() );

        HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest = new HttpEntity<>(hearder2);

        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class);

        System.out.println(response2);

        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile ;
        String body2 = response2.getBody();
        kakaoProfile = objectMapper2.readValue(body2, KakaoProfile.class);


        //User 오브젝트
        System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId() + " 이름 : " +kakaoProfile.getProperties().getNickname());
        String garbagePhoneNum = "000-0000-0000";

        // 가입처리
        System.out.println(userProvider.checkUserName(kakaoProfile.getProperties().getNickname()+"_"+kakaoProfile.getId()));
        //이미 존재하는 유저인지 확인
        if(userProvider.checkUserName(kakaoProfile.getProperties().getNickname()+"_"+kakaoProfile.getId()) != 1){
            PostUserReq postUserReq = new PostUserReq(kakaoProfile.getProperties().getNickname()+"_"+kakaoProfile.getId(), garbagePhoneNum);
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return postUserRes.getJwt();
        }

        //로그인 처리
        PostLoginReq postLoginReq = new PostLoginReq(kakaoProfile.getProperties().getNickname()+"_"+kakaoProfile.getId(), garbagePhoneNum);
        PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
        return postLoginRes.getJwt();

    }
}
