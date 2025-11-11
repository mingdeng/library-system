package com.library.dto;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 带图书详情的借阅记录DTO
 *
 * @author library-system
 */
@Data
public class BorrowRecordWithBookDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 借阅用户ID
     */
    private Long userId;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 借阅时间
     */
    private LocalDateTime borrowDate;

    /**
     * 应还时间
     */
    private LocalDateTime dueDate;

    /**
     * 实际归还时间
     */
    private LocalDateTime returnDate;

    /**
     * 续借次数
     */
    private Integer renewCount;

    /**
     * 最后续借时间
     */
    private LocalDateTime lastRenewDate;

    /**
     * 状态：BORROWED借出, RETURNED已还, OVERDUE逾期, LOST丢失, DAMAGED损坏
     */
    private String status;

    /**
     * 借书操作员ID
     */
    private Long borrowOperatorId;

    /**
     * 还书操作员ID
     */
    private Long returnOperatorId;

    /**
     * 借阅方式：COUNTER柜台, SELF自助, ONLINE在线
     */
    private String borrowType;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    /**
     * 逾期罚款（元）
     */
    private BigDecimal overdueFee;

    /**
     * 损坏赔偿（元）
     */
    private BigDecimal damageFee;

    /**
     * 是否已支付：0未付 1已付
     */
    private Integer isPaid;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 借书备注
     */
    private String borrowRemark;

    /**
     * 还书备注
     */
    private String returnRemark;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String review;
    
    /**
     * 图书详情
     */
    private Book book;
}