package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * author  TZQ
 * date  2020/3/2 15:21
 */

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.add(user.getId(), productId, count);
    }

    @RequestMapping("update")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.update(user.getId(), productId, count);
    }

    @RequestMapping("delete")
    @ResponseBody
    public ServerResponse<CartVo> delete(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping("list")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.list(user.getId());
    }


    @RequestMapping("select_all")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null)
            return ServerResponse.createBySuccess(0);
        return iCartService.getCartProductCount(user.getId());
    }
}

