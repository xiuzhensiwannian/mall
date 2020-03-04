package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * author  TZQ
 * date  2020/3/4 10:01
 */

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int resultCount=shippingMapper.insert(shipping);
        if (resultCount>0){
            Map map= Maps.newHashMap();
            map.put("shippingId", shipping.getId());
            return  ServerResponse.createBySuccess("新增地址成功", map);
        }
        return  ServerResponse.createByErrorMessage("新增地址失败");
    }

    @Override
    public ServerResponse<String> del(Integer userId, Integer shippingId) {
        //需要同时获取userId以及shippingId，防止用户横向越权
        int resultCount=shippingMapper.deleteByShippingIdUserId(userId, shippingId);
        if (resultCount>0)
            return  ServerResponse.createBySuccess("删除地址成功");
        return  ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int resultCount=shippingMapper.updateByShipping(shipping);
        if (resultCount>0)
            return  ServerResponse.createBySuccess("更新地址成功");
        return  ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping=shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if (shipping==null)
            return  ServerResponse.createByErrorMessage("查询地址失败");
        return  ServerResponse.createBySuccess("查询地址成功", shipping);
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return  ServerResponse.createBySuccess(pageInfo);
    }
}
