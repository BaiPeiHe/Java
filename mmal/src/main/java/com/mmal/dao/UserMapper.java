package com.mmal.dao;

import com.mmal.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    // 直接全部更新
    User selectByPrimaryKey(Integer id);

    // 那个属性不为空更新那个
    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username,@Param("password") String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question")String question,@Param("answer") String answer);

    int updatePasswordByUsername(@Param("username")String username, @Param("passwordNew")String passwordNew);

    int checkPassword(@Param("password") String password,@Param("userId") Integer userId);

    int checkEmailByUserId(@Param("email") String email, @Param("userId") Integer userId);

}