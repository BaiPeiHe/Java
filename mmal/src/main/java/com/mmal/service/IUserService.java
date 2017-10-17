package com.mmal.service;

import com.mmal.common.ServiceResponse;
import com.mmal.pojo.User;

public interface IUserService {

    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str, String type);
}
