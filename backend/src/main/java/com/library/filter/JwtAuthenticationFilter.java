package com.library.filter;

import com.library.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 
 * @author library-system
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取Token
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            try {
                // 从Token中获取用户信息
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                
                // 创建认证对象
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(authority));
                
                // 设置到Security上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Token无效，继续执行过滤器链（会被Security拦截）
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

