package com.mmal.controller.backend;

import com.mmal.common.Const;
import com.mmal.common.ResponseCode;
import com.mmal.common.ServerResponse;
import com.mmal.pojo.User;
import com.mmal.service.ICategoryService;
import com.mmal.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 管理分类
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加新分类
     * @param session
     * @param categoryName 分类名称
     * @param parentId 父节点，默认是0
     * @return
     */

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        // 校验是否为管理员
        if(iUserService.cheackAdminRole(user).isSuccesee()){
            // 是管理员，添加处理分类的逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        // 校验是否为管理员
        if(iUserService.cheackAdminRole(user).isSuccesee()){
            // 更新 categoryName
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        // 校验是否为管理员
        if(iUserService.cheackAdminRole(user).isSuccesee()){
            // 查询当前分类的所有子节点的 category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        // 校验是否为管理员
        if(iUserService.cheackAdminRole(user).isSuccesee()){
            // 查询当前节点的所有子节点，并且获取还要获取子节点的子节点
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


}
