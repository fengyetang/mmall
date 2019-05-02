package com.mmall.dao;

import com.mmall.pojo.Person;
import com.mmall.pojo.PersonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PersonMapper {
    long countByExample(PersonExample example);

    int deleteByExample(PersonExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Person record);

    int insertSelective(Person record);

    List<Person> selectByExample(PersonExample example);

    Person selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Person record, @Param("example") PersonExample example);

    int updateByExample(@Param("record") Person record, @Param("example") PersonExample example);

    int updateByPrimaryKeySelective(Person record);

    int updateByPrimaryKey(Person record);

    int deleteByPersonIdUserId(@Param("userId")Integer userId,@Param("personId")Integer personId);
    int updateByPersion(Person record);

    Person selectByPersionIdUserId(@Param("userId")Integer userId,@Param("personId")Integer personId);

    List<Person> selectByUserId(@Param("userId")Integer userId);
}