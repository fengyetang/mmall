package com.mmall.controller.portal;


import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Person;
import com.mmall.pojo.User;
import com.mmall.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/person/")
public class PersonController {

    @Autowired
    private IPersonService iPersonService;

   //这里使用sprinMVC对象绑定的方式，通过person对象接受穿过来的新增信息
    //新增个人信息
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Person person){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
       return iPersonService.add(user.getId(),person);
    }

    //删除接口
    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpSession session,Integer personId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iPersonService.del(user.getId(),personId);
    }

    //更新地址接口
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Person person){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iPersonService.update(user.getId(), person);
    }

    //查看个人信息详情
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Person> select(HttpSession session,Integer personId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iPersonService.select(user.getId(),personId);
    }

    //list分页接口
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageNum",defaultValue = "1")int pageSize,
                                         HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iPersonService.list(user.getId(),pageNum,pageSize);

    }
}
