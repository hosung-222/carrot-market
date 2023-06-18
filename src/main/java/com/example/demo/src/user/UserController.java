package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexPhoneNumber;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final SmsService smsService;
    @Autowired
    private final CacheService cacheService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, SmsService smsService, CacheService cacheService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.smsService = smsService;
        this.cacheService = cacheService;
    }

    /**
     * 전체 회원 조회 API
     * [GET] /users
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping()
    public BaseResponse<List<GetUserRes>> getUsers() {
        try{
            // Get Users
            List<GetUserRes> getUsersRes = userProvider.getUsers();
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 마이페이지 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        try{
            GetUserRes getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if(postUserReq.getPhoneNum() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }
        //폰번호 정규 표현식
        if(!isRegexPhoneNumber(postUserReq.getPhoneNum())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /////////////////////


    /**
     * 전화번호 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestParam("phoneNum")String phoneNum, @RequestParam("authNum") String authNum){
//        if(phoneNum == null){
//            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
//        }
        //폰번호 정규 표현식
//        if(!isRegexPhoneNumber(phoneNum)){
//            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
//        }
        try{
            PostLoginRes postLoginRes = userProvider.logIn(phoneNum,authNum);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 인증 번호 발송 API
     * @param phoneNum
     * @return
     */
    @ResponseBody
    @PostMapping("/send-one")
    public String sendOne(@RequestParam("phoneNum") String phoneNum) {
        Random random = new Random();
        String randNum = "";
        for (int i = 0; i < 5; i++) {
            String s = Integer.toString(random.nextInt(10));
            randNum += s;
        }
        cacheService.saveVerificationCode(phoneNum, randNum);
        this.smsService.sendOne(phoneNum,randNum);


        return randNum;
    }


    /**
     * 유저 이름 변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getUserName());
            userService.modifyUserName(patchUserReq);

            String result = userIdx +"번 유저 이름이 " +user.getUserName() + " 으로 수정 완료되었습니다";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 삭제 API
     * [DELETE] /users/:userIdx
     * @return userIdx
     */
    @ResponseBody
    @DeleteMapping("/{userIdx}")
    public BaseResponse<String> deleteUser(@PathVariable ("userIdx") int userIdx){
        try {
            userService.deleteUser(userIdx);
            String result = userIdx + "번 유저가 삭제되었습니다.";

            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * 유저 지역 추가 API
     * @param userRegion
     * @param userIdx
     * @return
     */
    @ResponseBody
    @PostMapping("/{userIdx}")
    public BaseResponse<String> postUserRegion (@RequestParam("userRegion") int userRegion, @PathVariable("userIdx") int userIdx){
        try{
           if(userProvider.postUserRegion(userIdx, userRegion)) {
               String result = userIdx + " 번 유저의 지역 설정 완료";
               return new BaseResponse<>(result);
           }else {
               String result = "지역 설정 실패";
               return new BaseResponse<>(result);
           }

        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 대표 지역 설정 API
     * @param mainRegion
     * @param userIdx
     * @return
     */
    @ResponseBody
    @PostMapping("/mainregion/{userIdx}")
    public BaseResponse<String> selectMainRegion(@RequestParam("mainRegion") String mainRegion, @PathVariable("userIdx") int userIdx) {
        try {
            if(userService.selectMainRegion(userIdx,mainRegion)) {
                return new BaseResponse<>(userIdx + "메인지역 설정 완료");
            }
            else return new BaseResponse<>("메인 지역 설정 실패");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 거래 리뷰 남기기
     * @param userIdx
     * @param postReviewReq
     * @return
     */
    @ResponseBody
    @PostMapping("/review/{userIdx}")
    public BaseResponse<String> sendReview(@PathVariable("userIdx") int userIdx,@RequestBody PostReviewReq postReviewReq){
        try {
            userService.sendReview(userIdx, postReviewReq);
            return new BaseResponse<>("리뷰를 작성했습니다.");
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 리뷰 확인하기
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/review/{userIdx}")
    public BaseResponse<List<GetReviewRes>> getMyReviews(@PathVariable("userIdx") int userIdx){
        try{
            List<GetReviewRes> getReviewRes = userProvider.getMyReviews(userIdx);
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
