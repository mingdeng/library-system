package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.User;

/**
 * 用户服务接口
 * 
 * @author library-system
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户
     */
    User getByUsername(String username);
}

