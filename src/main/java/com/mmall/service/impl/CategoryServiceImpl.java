package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * author  TZQ
 * date  2020/2/28 14:35
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String  categoryName, Integer parentId){
        if (parentId==null || StringUtils.isBlank(categoryName)){
            return  ServerResponse.createByErrorMessage("参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int resultCount=categoryMapper.insert(category);
        if (resultCount > 0) {
            return  ServerResponse.createBySuccess("添加品类传功");
        }
        return  ServerResponse.createByErrorMessage("添加品类失败");
    }

    public  ServerResponse updateCategoryName(Integer categoryId, String  categoryName) {
        if (categoryId==null || StringUtils.isBlank(categoryName)){
            return  ServerResponse.createByErrorMessage("参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int  resultCount=categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0) {
            return  ServerResponse.createBySuccess("更新品类传功");
        }
        return  ServerResponse.createByErrorMessage("更新品类失败");
    }

    public  ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
    List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
    if (CollectionUtils.isEmpty(categoryList)){
        logger.info("未找到当前分类的子分类");
    }
    return  ServerResponse.createBySuccess(categoryList);
    }

    public  ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySer= Sets.newHashSet();
        findChildCategory(categorySer, categoryId);
        List<Integer> categoryList= Lists.newArrayList();
        if (categoryId!=null){
            for (Category categoryItem : categorySer){
                categoryList.add(categoryItem.getId());
            }
        }
        return  ServerResponse.createBySuccess(categoryList);
    }

    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList){
            findChildCategory(categorySet, categoryItem.getId());
        }
        return  categorySet;
    }


}
