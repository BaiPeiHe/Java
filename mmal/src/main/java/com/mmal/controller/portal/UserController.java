package com.mmal.controller.portal;

import com.mmal.common.Const;
import com.mmal.common.ServiceResponse;
import com.mmal.pojo.User;
import com.mmal.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {


    @Autowired
    private IUserService iUserService;

    /*
    * 用户登录
    * */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        // service --> mybatis --> dao

        ServiceResponse<User> response = iUserService.login(username,password);
        if(response.isSuccesee()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }

        return response;
    }

    /*
    * 用户退出登录
    * */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);
    }




}

