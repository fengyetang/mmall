package com.mmall.dao;

import com.mmall.pojo.Teamproduct;
import com.mmall.pojo.TeamproductExample;
import com.mmall.pojo.TeamproductKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TeamproductMapper {
    long countByExample(TeamproductExample example);

    int deleteByExample(TeamproductExample example);

    int deleteByPrimaryKey(TeamproductKey key);

    int insert(Teamproduct record);

    int insertSelective(Teamproduct record);

    List<Teamproduct> selectByExample(TeamproductExample example);

    Teamproduct selectByPrimaryKey(TeamproductKey key);

    int updateByExampleSelective(@Param("record") Teamproduct record, @Param("example") TeamproductExample example);

    int updateByExample(@Param("record") Teamproduct record, @Param("example") TeamproductExample example);

    int updateByPrimaryKeySelective(Teamproduct record);

    int updateByPrimaryKey(Teamproduct record);
}