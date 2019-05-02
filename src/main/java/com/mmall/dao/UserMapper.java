package com.mmall.dao;

import com.mmall.pojo.User;
import com.mmall.pojo.UserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    int checkUsername(String username);
    int checkEmail(String email);
    User selectLogin(@Param("username")String username,@Param("password")String password);
    String selectQuestionByUsername(String username);
    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);
    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);

}