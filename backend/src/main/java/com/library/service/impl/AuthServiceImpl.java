package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.dto.LoginDTO;
import com.library.dto.LoginResponseDTO;
import com.library.entity.User;
import com.library.service.AuthService;
import com.library.service.UserService;
import com.library.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 * 
 * @author library-system
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public LoginResponseDTO readerLogin(LoginDTO loginDTO) {
        // 查询用户
        User user = userService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证角色
        if (!"READER".equals(user.getRole())) {
            throw new BusinessException("该账号不是读者账号");
        }
        
        // 验证状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 注册或修改密码时的正确做法
        /*String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);*/

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建响应
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        
        return response;
    }
    
    @Override
    public LoginResponseDTO adminLogin(LoginDTO loginDTO) {
        // 查询用户
        User user = userService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证角色
        if (!"ADMIN".equals(user.getRole()) && !"LIBRARIAN".equals(user.getRole())) {
            throw new BusinessException("该账号不是管理员账号");
        }
        
        // 验证状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 注册或修改密码时的正确做法
        /*String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);*/

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            log.warn("密码验证失败 - 用户名: {}", loginDTO.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建响应
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        
        return response;
    }
}

