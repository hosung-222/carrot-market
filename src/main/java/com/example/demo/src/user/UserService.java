package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if (userProvider.checkPhoneNum(postUserReq.getPhoneNum()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_PHONENUBMER);
        }

        try {
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt, userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try {
            int result = userDao.modifyUserName(patchUserReq);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void deleteUser(int userIdx) throws BaseException {

        try {
            userDao.deleteUser(userIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Boolean selectMainRegion(int userIdx, String userRegion) throws BaseException{

        if(!userDao.findUserRegion(userIdx, userRegion))
            throw new BaseException(NO_REGION_FOR_USER);
        try{
            if(userDao.selectMainRegion(userIdx, userRegion)>0)
                return true;
            else return false;

        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Boolean sendReview(int userIdx, PostReviewReq postReviewReq)throws BaseException{
//        if (userDao.findUserByUserIdx(userIdx)){
//            throw new BaseException(USERS_EMPTY_USER_ID);
//        }
//        if (userDao.findUserByUserIdx(postReviewReq.getSendUserIdx())){
//            throw new BaseException(USERS_EMPTY_USER_ID);
//        }
        try {
            if(userDao.sendReview(userIdx, postReviewReq)>0)
                return true;

            else throw new BaseException(FAIL_SEND_REVIEW);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
