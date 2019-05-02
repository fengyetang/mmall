package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Person;

public interface IPersonService {
    ServerResponse add(Integer userId, Person person);
    ServerResponse<String> del(Integer userId,Integer personId);
    ServerResponse update(Integer userId,Person person);
    ServerResponse<Person> select(Integer userId,Integer personId);
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
