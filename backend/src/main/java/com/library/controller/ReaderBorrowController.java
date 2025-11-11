package com.library.controller;

import com.library.common.Result;
import com.library.dto.BorrowRecordWithBookDTO;
import com.library.entity.BorrowRecord;
import com.library.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 读者端借阅控制器
 * 
 * @author library-system
 */
@RestController
@RequestMapping("/api/reader/borrows")
@RequiredArgsConstructor
public class ReaderBorrowController {
    
    private final BorrowRecordService borrowRecordService;
    
    /**
     * 借阅图书
     */
    @PostMapping
    public Result<BorrowRecord> borrow(@RequestParam Long bookId, @RequestParam Long userId) {
        BorrowRecord record = borrowRecordService.borrowBook(userId, bookId);
        return Result.success("借阅成功", record);
    }
    
    /**
     * 续借图书
     */
    @PostMapping("/renew")
    public Result<BorrowRecord> renew(@RequestParam Long recordId, @RequestParam Long userId) {
        BorrowRecord record = borrowRecordService.renewBook(recordId, userId);
        return Result.success("续借成功", record);
    }
    
    /**
     * 获取我的借阅列表
     */
    @GetMapping
    public Result<List<BorrowRecordWithBookDTO>> list(@RequestParam Long userId) {
        List<BorrowRecordWithBookDTO> list = borrowRecordService.getBorrowListWithBookByUserId(userId);
        return Result.success(list);
    }
}