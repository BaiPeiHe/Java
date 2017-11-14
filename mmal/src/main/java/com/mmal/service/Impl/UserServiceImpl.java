package com.mmal.service.Impl;

import com.mmal.common.Const;
import com.mmal.common.ServerResponse;
import com.mmal.common.TokenCache;
import com.mmal.dao.UserMapper;
import com.mmal.pojo.User;
import com.mmal.service.IUserService;
import com.mmal.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService{


    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount  = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);

        System.out.println("md5Password = " + md5Password);

        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登陆成功",user);
    }

    public ServerResponse<String> register(User user){

        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccesee()){
            return validResponse;
        }

        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccesee()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str, String type){
        if(StringUtils.isNoneBlank(type)){
            // 开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("email 已存在");
                }
            }
        }
        else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse selectQuestion(String username){

        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccesee()){
            // 用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String quesion = userMapper.selectQuestionByUsername(username);

        if(StringUtils.isNotBlank(quesion)){
            return ServerResponse.createBySuccess(quesion);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }


    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            // 问题以及答案都是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetTocken){
        if(StringUtils.isBlank(forgetTocken)){
            return ServerResponse.createByErrorMessage("参数错误，token 是必须的参数");
        }

        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccesee()){
            // 用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token 无效或过期");
        }

        if(StringUtils.equals(token,forgetTocken)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }
        else {
            return ServerResponse.createByErrorMessage("token 错误，请重新请求重置密码的 token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        // 防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户。
        // 因为我们要查询一个 count(1)，如果不指定 id，那么结果就是 true 啦，count > 0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));

        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }

        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInfomation(User user){
        // username 是不能被更新的
        // email 也需要进行交易，校验新的 email 是不是已经存在，并且如果存在的话，能是当前用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email 已经存在，请更换 email 后再尝试更新");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }



    // backend


    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse cheackAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }



}
