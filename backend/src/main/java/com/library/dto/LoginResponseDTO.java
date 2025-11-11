package com.library.dto;

import lombok.Data;

/**
 * 登录响应DTO
 * 
 * @author library-system
 */
@Data
public class LoginResponseDTO {
    
    /**
     * Token
     */
    private String token;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 角色
     */
    private String role;
}

