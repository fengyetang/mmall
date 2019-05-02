package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.PersonMapper;
import com.mmall.pojo.Person;
import com.mmall.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iPersonService")
public class PersonServiceImpl implements IPersonService {

    @Autowired
    private PersonMapper personMapper;
    //下面是springMVC中的对象绑定，用person对象传入前端写下的地址信息
    public ServerResponse add(Integer userId,Person person){
       person.setUserId(userId);
      int rowCount=personMapper.insert(person);
      if (rowCount>0){
          Map result= Maps.newHashMap();
          result.put("personId",person.getId());
          return ServerResponse.createBySuccess("新建个人信息成功",result);
      }
      return ServerResponse.createByErrorMessage("新建个人信息失败");
    }

    public ServerResponse<String> del(Integer userId,Integer personId){
        int resultCount=personMapper.deleteByPersonIdUserId(userId,personId);
        if (resultCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createBySuccess("删除地址失败");
    }

    public ServerResponse update(Integer userId,Person person){
        person.setUserId(userId);
        int rowCount=personMapper.updateByPersion(person);
        if (rowCount>0){
            return ServerResponse.createBySuccess("更新个人信息成功");
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    //个人信息详情查看
    public ServerResponse<Person> select(Integer userId,Integer personId){
      Person person=personMapper.selectByPersionIdUserId(userId,personId);
      if (person==null){
          return ServerResponse.createByErrorMessage("无法查询到该地址");
      }
      return ServerResponse.createBySuccess("更新地址成功",person);
    }

    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Person> personList=personMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(personList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
