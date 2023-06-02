package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){
        String getUsersQuery = "SELECT U.user_Name AS userName, U.user_Degree AS userDegree, U.retrade_rate AS retradeRate, U.response_Rate AS responseRate,\n" +
                "       (SELECT COUNT(*) FROM product WHERE user_idx = U.user_idx) AS cntProduct,\n" +
                "       GROUP_CONCAT(R.region_name SEPARATOR ', ') AS userRegions\n" +
                "FROM user U\n" +
                "JOIN user_region UR ON U.user_idx = UR.user_idx\n" +
                "JOIN region R ON UR.region_idx = R.region_idx\n" +
                "GROUP BY U.user_idx;";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getString("userName"),
                        rs.getDouble("userDegree"),
                        rs.getInt("retradeRate"),
                        rs.getInt("responseRate"),
                        rs.getInt("cntProduct"),
                        Arrays.asList(rs.getString("userRegions").split(", ")))
                );
    }


    public GetUserRes getUser(int userIdx){
        String getUserQuery = "SELECT U.user_Name AS userName, U.user_Degree AS userDegree, U.retrade_rate AS retradeRate, U.response_Rate AS responseRate,\n" +
                "       (SELECT COUNT(*) FROM product WHERE user_idx = U.user_idx) AS cntProduct,\n" +
                "       GROUP_CONCAT(R.region_name SEPARATOR ', ') AS userRegions\n" +
                "FROM user U\n" +
                "JOIN user_region UR ON U.user_idx = UR.user_idx\n" +
                "JOIN region R ON UR.region_idx = R.region_idx\n" +
                "WHERE U.user_idx = ?\n" +
                "GROUP BY U.user_idx;";
        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("userName"),
                        rs.getDouble("userDegree"),
                        rs.getInt("retradeRate"),
                        rs.getInt("responseRate"),
                        rs.getInt("cntProduct"),
                        Arrays.asList(rs.getString("userRegions").split(", "))
                ),
                getUserParams);
    }
    

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into user (user_name, phone_num) VALUES (?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getPhoneNum()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int checkPhoneNum(String PhoneNum){
        String checkPhoneNumQuery = "select exists(select phone_num from user where phone_num = ?)";
        String checkEmailParams = PhoneNum;
        return this.jdbcTemplate.queryForObject(checkPhoneNumQuery,
                int.class,
                checkEmailParams);

    }

    public int checkUserName(String userName){
        String checkUserNameQuery = "select exists(select user_name from user where user_name = ?)";
        String checkUserNameParams = userName;
        return this.jdbcTemplate.queryForObject(checkUserNameQuery,int.class,checkUserNameParams);
    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update user set user_name = ? where user_idx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};
        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);


    }
    public int deleteUser(int userIdx){
        String deleteUserQuery = "delete from user where user_idx = ?";
        int deleteUserParams = userIdx;
        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }

    public User getPhoneNum(PostLoginReq postLoginReq){
        String getPhoneNumQuery = "select user_idx,user_name, phone_num from user where user_name = ?\n";
        String getPhoneNumParams = postLoginReq.getUserName();

        return this.jdbcTemplate.queryForObject(getPhoneNumQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("user_idx"),
                        rs.getString("user_name"),
                        rs.getString("phone_num")
                ),
                getPhoneNumParams
                );

    }


}
