package com.mmal.controller.backend;

import com.mmal.common.Const;
import com.mmal.common.ResponseCode;
import com.mmal.common.ServerResponse;
import com.mmal.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 管理分类
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    /**
     * 添加新分类
     * @param session
     * @param categoryName 分类名称
     * @param parentId 父节点，默认是0
     * @return
     */
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        // 校验是否为管理员

    }

}
