package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 预约实体类
 * 
 * @author library-system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reservations")
public class Reservation extends BaseEntity {
    
    /**
     * 预约用户ID
     */
    private Long userId;
    
    /**
     * 图书ID
     */
    private Long bookId;
    
    /**
     * 预约时间
     */
    private LocalDateTime reserveDate;
    
    /**
     * 通知时间（书可借时发送）
     */
    private LocalDateTime notifyDate;
    
    /**
     * 预约失效时间（通知后N天不借）
     */
    private LocalDateTime expireDate;
    
    /**
     * 取消时间
     */
    private LocalDateTime cancelDate;
    
    /**
     * 借阅时间
     */
    private LocalDateTime borrowDate;
    
    /**
     * 状态：WAITING等待中, NOTIFIED已通知, BORROWED已借, EXPIRED已过期, CANCELLED已取消
     */
    private String status;
    
    /**
     * 排队号码（第几个预约）
     */
    private Integer queueNumber;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 取消原因
     */
    private String cancelReason;
}

