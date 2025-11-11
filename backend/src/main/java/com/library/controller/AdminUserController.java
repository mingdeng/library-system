package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端用户控制器
 * 
 * @author library-system
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result<IPage<User>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIsDeleted, 0);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        if (role != null && !role.isEmpty() && !"全部".equals(role)) {
            wrapper.eq(User::getRole, role);
        }
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        IPage<User> result = userService.page(page, wrapper);
        return Result.success(result);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> detail(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null || user.getIsDeleted() == 1) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }
    
    /**
     * 新增用户
     */
    @PostMapping
    public Result<User> add(@RequestBody User user) {
        // 检查用户名是否已存在
        User existUser = userService.getByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getRole() == null) {
            user.setRole("READER");
        }
        if ("READER".equals(user.getRole())) {
            if (user.getMaxBorrowCount() == null) {
                user.setMaxBorrowCount(3);
            }
            if (user.getMaxBorrowDays() == null) {
                user.setMaxBorrowDays(30);
            }
            if (user.getCreditScore() == null) {
                user.setCreditScore(100);
            }
        }
        
        userService.save(user);
        return Result.success("新增成功", user);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        
        // 如果传了密码，则加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 不更新密码
            user.setPassword(null);
        }
        
        userService.updateById(user);
        return Result.success("更新成功", user);
    }
    
    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }
}

