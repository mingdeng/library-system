package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类（读者+管理员）
 * 
 * @author library-system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {
    
    /**
     * 用户名（登录账号）
     */
    private String username;
    
    /**
     * 密码（BCrypt加密）
     */
    private String password;
    
    /**
     * 角色：READER读者, LIBRARIAN管理员, ADMIN系统管理员
     */
    private String role;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 身份证号
     */
    private String idCard;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 性别：0未知 1男 2女
     */
    private Integer gender;
    
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    
    /**
     * 学号/工号
     */
    private String studentId;
    
    /**
     * 院系/部门
     */
    private String department;
    
    /**
     * 读者类型：1本科生 2研究生 3教师 4校外人员
     */
    private Integer readerType;
    
    /**
     * 信用分（默认100，逾期扣分）
     */
    private Integer creditScore;
    
    /**
     * 最大可借数量
     */
    private Integer maxBorrowCount;
    
    /**
     * 最大借阅天数
     */
    private Integer maxBorrowDays;
    
    /**
     * 岗位ID（管理员）
     */
    private Long postId;
    
    /**
     * 岗位名称
     */
    private String postName;
    
    /**
     * 权限列表（JSON格式）
     */
    private String permissions;
    
    /**
     * 状态：1正常 0禁用 2待审核
     */
    private Integer status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}

