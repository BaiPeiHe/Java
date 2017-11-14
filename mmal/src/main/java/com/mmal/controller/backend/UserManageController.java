package com.mmal.controller.backend;

import com.mmal.common.Const;
import com.mmal.common.ServerResponse;
import com.mmal.pojo.User;
import com.mmal.service.IUserService;
import com.mmal.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 后台管理
 */

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String passwoord, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, MD5Util.MD5EncodeUtf8(passwoord));
        if(response.isSuccesee()){
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                // 说明登录的是管理员
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
        return response;
    }

}
