package com.library.controller;

import com.library.common.Result;
import com.library.dto.LoginDTO;
import com.library.dto.LoginResponseDTO;
import com.library.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 读者端认证控制器
 * 
 * @author library-system
 */
@RestController
@RequestMapping("/api/reader")
@RequiredArgsConstructor
public class ReaderAuthController {
    
    private final AuthService authService;
    
    /**
     * 读者登录
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO response = authService.readerLogin(loginDTO);
        return Result.success(response);
    }
}

