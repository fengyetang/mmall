package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/*
* 前台商品详情，列表，搜索，动态排序功能
* */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId,Integer sharePersonId){

        //todo 多传进来一个分享者id
        //disSuff,意思为分销后缀
       /* Map<String,Object> disSuff= Maps.newHashMap();
        disSuff.put("商品编号",productId);
        disSuff.put("分享者编号",disSuff);*/
        return iProductService.getProductDetail(productId,sharePersonId);

    }

    //前台用户查看商品列表，返回列表需要分页，所以返回PageInfo类
    //required置为false，说明关键字也可以不用传进来
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId", required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "")String orderBy ){

          return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);

    }



   //  在商品详情界面点击分享按钮，生成专属url
    @RequestMapping("share.do")
    @ResponseBody
    public ServerResponse<String> share(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        int sharePersonId=user.getId();
        String disurl="http://localhost:8080/product/detail.do?productId="+productId+"?sharePersonId?"+sharePersonId;
        return ServerResponse.createBySuccess(disurl);
    }
}
