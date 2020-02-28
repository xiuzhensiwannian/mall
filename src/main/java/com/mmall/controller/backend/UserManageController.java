package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * author  TZQ
 * date  2020/2/27 17:42
 */

@Controller
@RequestMapping("/manage/user")
public class UserManageController{

    @Autowired
    private  IUserService iUserService;

    @RequestMapping(value="login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User>  login(String username , String password, HttpSession session){
        ServerResponse<User> response=iUserService.login(username, password);
        if (response.isSuccess()){
            User user=response.getData();
            if (user.getRole()==Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER, user);
                return  response;
            }
            return  ServerResponse.createByErrorMessage("不是管理员无法登录");
        }
        return  response;
    }
}
