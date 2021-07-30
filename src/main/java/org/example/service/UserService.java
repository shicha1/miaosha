package org.example.service;

import org.example.error.BusinessException;
import org.example.service.model.UserModel;

public interface UserService {
    //通过用户id获取用户对象的方法
    UserModel getUserById(Integer id);

    /***
     * 通过缓存获取用户对象
     * @param id
     * @return
     */
    UserModel getUserByIdInCache(Integer id);
    void register(UserModel userModel) throws BusinessException;
    UserModel vaildateLogin(String telphone,String encrptPassword) throws BusinessException;
}
