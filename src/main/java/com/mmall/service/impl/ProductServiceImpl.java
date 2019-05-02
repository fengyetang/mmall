package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("IProductService")
public class ProductServiceImpl implements IProductService{
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;
    public ServerResponse saveOrUpdateProduct(Product product){
        if (product!=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
            String[] subImageArray=product.getSubImages().split(",");
            if (subImageArray.length>0){
                product.setMainImage((subImageArray[0]));
            }
            }
            if (product.getId()!=null){
                int rowCount=productMapper.updateByPrimaryKey(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
               return ServerResponse.createBySuccess("更新产品失败");

            }
            else {
                int rowCount=productMapper.insert(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("更新产品失败");
            }
            }

        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

     public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId==null||status==null){
           return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount=productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
     }

     public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //VO对象--value object
         //pojo->bo(business object)->vo(view object)
         ProductDetailVo productDetailVo= assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

     }
     private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageHost
         productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
         //parentCategoryId

         Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
         if (category==null){
             productDetailVo.setParentCategoryId(0);//默认为根节点
         }else {
             productDetailVo.setParentCategoryId(category.getParentId());
         }

        //ctreaTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
         //updateTime
         productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
     }

     public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){

        //startPage--start
         //填充自己的sql查询逻辑
         //pageHelper-收尾
         PageHelper.startPage(pageNum,pageSize);
         List<Product> productList=productMapper.selectList();
         List<ProductListVo> productListVoList= Lists.newArrayList();
         for (Product productItem:productList){
             ProductListVo productListVo=assembleProductListVo(productItem);
             productListVoList.add(productListVo);
         }
         PageInfo pageResult=new PageInfo(productList);
         pageResult.setList(productListVoList);
         return ServerResponse.createBySuccess(pageResult);
     }

     //productlistvo的组装方法
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName=new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> productList=productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product productItem:productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    //前台产品详情查看
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId,Integer sharePersonId){
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //VO对象--value object
        //pojo->bo(business object)->vo(view object)
        if (product.getStatus()!=Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo= assembleProductDetailVo(product);
        productDetailVo.setSharePersonId(sharePersonId);
        return ServerResponse.createBySuccess(productDetailVo);
    }
    //通过关键词或者类目id搜索产品
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if (StringUtils.isBlank(keyword)&&categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList=new ArrayList<Integer>();
        if (categoryId!=null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类，并且还没有关键字，这时候返回一个空的结果集不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //这里的getData获取了serverresponse中的T
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
            if (StringUtils.isNotBlank(keyword)){
                keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
            }
        //分页开始,分页大小容量
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
           if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
              //split是根据_来分割传过来的orderBy,传过来的orderBy例如price_DES
               String[] orderByArray=orderBy.split("_");
               //下面的orderBy方法格式为（"price desc"）
               //分页排序功能的运用
               PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
           }
        }
        //三元运算符 公式？a:b  意思为，算出来的如果是，则返回a，不是则返回b，如下面的实例
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        //学习foreach运算最好的例子！！！！！！
        for (Product product:productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        //将分页的集合置为productListVoList
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }


}
