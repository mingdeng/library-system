package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.dto.BorrowRecordWithBookDTO;
import com.library.entity.BorrowRecord;

import java.util.List;

/**
 * 借阅记录服务接口
 * 
 * @author library-system
 */
public interface BorrowRecordService extends IService<BorrowRecord> {
    
    /**
     * 借阅图书
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 借阅记录
     */
    BorrowRecord borrowBook(Long userId, Long bookId);
    
    /**
     * 续借图书
     * 
     * @param recordId 借阅记录ID
     * @param userId 用户ID
     * @return 借阅记录
     */
    BorrowRecord renewBook(Long recordId, Long userId);
    
    /**
     * 获取用户的借阅列表
     * 
     * @param userId 用户ID
     * @return 借阅列表
     */
    List<BorrowRecord> getBorrowListByUserId(Long userId);
    
    /**
     * 获取用户的借阅列表（包含图书详情）
     *
     * @param userId 用户ID
     * @return 带图书详情的借阅列表
     */
    List<BorrowRecordWithBookDTO> getBorrowListWithBookByUserId(Long userId);
}