package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

/**
 * author  TZQ
 * date  2020/3/2 15:33
 */

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId==null || count==null){
            return  ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart==null){
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }
        else {
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId==null || count==null){
            return  ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart!=null)
            cart.setQuantity(count);
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String > productList= Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)){
            return  ServerResponse.createByErrorCodeMessage(
                    ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return  this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId==null)
            return ServerResponse.createBySuccess(0);
        int count=cartMapper.selectCartProductCount(userId);
        return  ServerResponse.createBySuccess(count);
    }



    private  CartVo getCartVoLimit(Integer userId){
        CartVo cartVo=new CartVo();
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList= Lists.newArrayList();

        BigDecimal cartTotalPrice=new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)){
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    int buyLimitCount=0;
                    if (product.getStock()>=cartItem.getQuantity()){
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }
                    else {
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                if (cartItem.getChecked()==Const.Cart.CHECKED){
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return  cartVo;
    }

    private  boolean getAllCheckedStatus(Integer userId){
        if (userId==null)
            return  false;
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }
}
