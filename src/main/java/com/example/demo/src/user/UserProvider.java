package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;
    private final CacheService cacheService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService, CacheService cacheService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
        this.cacheService = cacheService;
    }

    public List<GetUserRes> getUsers() throws BaseException{
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            // Logger를 이용하여 에러를 로그에 기록한다
            logger.error("Error!", exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetUserRes getUser(int userIdx) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkPhoneNum(String PhoneNum) throws BaseException{
        try{
            return userDao.checkPhoneNum(PhoneNum);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserName(String userName) throws BaseException{
        try {
            return userDao.checkUserName(userName);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public PostLoginRes logIn(String phoneNum, String authNum) throws BaseException {

            if (!userDao.checkUserExists(phoneNum)) {
                throw new BaseException(NO_USER_FOR_PHONE_NUM);
            }
            String cachedAuthNum = cacheService.getVerificationCode(phoneNum);

            if (cachedAuthNum == null || !cachedAuthNum.equals(authNum)) {
                throw new BaseException(FAILED_TO_LOGIN);
            } else {
                // 삭제된 인증번호 정보
                System.out.println(cachedAuthNum);
                GetUserIdxRes getUserIdxRes = userDao.findUserIdxByPhoneNum(phoneNum);
                String jwt = jwtService.createJwt(getUserIdxRes.getUserIdx());
                cacheService.deleteVerificationCode(phoneNum);
                return new PostLoginRes(getUserIdxRes.getUserIdx(), jwt);
            }

        }



    public PostLoginRes kakaoLogin(PostLoginReq postLoginReq)throws BaseException{
        User user = userDao.getPhoneNum(postLoginReq);

        if(user.getPhoneNum().equals(user.getPhoneNum())){
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public boolean postUserRegion(int userIdx, int userRegion) throws BaseException{
        try{
            if (userDao.postUserRegion(userIdx, userRegion)>0){
                return true;
            }
            else return false;

        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReviewRes> getMyReviews(int userIdx) throws BaseException{
        try {
            List<GetReviewRes> getReviewRes = userDao.getMyReviews(userIdx);
            return getReviewRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
