package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    //产品保存以及更新
    ServerResponse saveOrUpdateProduct(Product product);
    //产品上下架处理
    ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    //产品详情管理
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    //获取当前产品列表
    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId,Integer sharePersonId);
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
