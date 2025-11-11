package com.library.service;

import com.library.dto.LoginDTO;
import com.library.dto.LoginResponseDTO;

/**
 * 认证服务接口
 * 
 * @author library-system
 */
public interface AuthService {
    
    /**
     * 读者登录
     * 
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    LoginResponseDTO readerLogin(LoginDTO loginDTO);
    
    /**
     * 管理员登录
     * 
     * @param loginDTO 登录信息
     * @return 登录响应
     */
    LoginResponseDTO adminLogin(LoginDTO loginDTO);
}

