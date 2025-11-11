package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 
 * @author library-system
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        wrapper.eq(User::getIsDeleted, 0);
        return this.getOne(wrapper);
    }
}

