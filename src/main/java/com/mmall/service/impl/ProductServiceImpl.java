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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author  TZQ
 * date  2020/2/29 14:54
 */

@Service("iProductService")
public class ProductServiceImpl  implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse  saveOrUpdateProduct(Product product){
        if (product!=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String [] subImageArray=product.getSubImages().split(",");
                if (subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }

            if (product.getId()!=null) {
                int resultCount = productMapper.updateByPrimaryKey(product);
                if (resultCount > 0) {
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            }
            else {
                int resultCount = productMapper.insert(product);
                if (resultCount > 0) {
                    return ServerResponse.createBySuccessMessage("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return  ServerResponse.createByErrorMessage("参数错误");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId==null || status==null){
            return  ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount=productMapper.updateByPrimaryKeySelective(product);
        if (resultCount>0){
            return  ServerResponse.createBySuccessMessage("修改产品销售状态成功");
        }
        return  ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId==null){
            return  ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return  ServerResponse.createByErrorMessage("产品不存在");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return  ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo  assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setCategoryId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());;
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/" ));

        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category==null){
            productDetailVo.setParentCategoryId(0);
        }
        else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList=productMapper.selectList();
        List<ProductListVo> productListVoList= Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo result=new PageInfo(productList);
        result.setList(productListVoList);
        return  ServerResponse.createBySuccess(result);
    }

    //获取单个产品vo
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/" ));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return  productListVo;
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)){
            //此处写法可能有误
            productName=new StringBuilder("%" + productName + "%").toString();
        }
        List<Product> productList=productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo=assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo result=new PageInfo(productList);
        result.setList(productListVoList);
        return  ServerResponse.createBySuccess(result);
    }


    /**********************************************************************************************************
     *
     *                                                             下面为前台页面
     *
     ************************************************************************************************************/
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId==null){
            return  ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return  ServerResponse.createByErrorMessage("产品不存在");
        }
        if (product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return  ServerResponse.createByErrorMessage("产品不存在");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return  ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId==null){
            return  ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList=Lists.newArrayList();
        if (categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVoList);
                return  ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList=iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword=new StringBuilder("%"+keyword+"%").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String [] orderByArray=orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
