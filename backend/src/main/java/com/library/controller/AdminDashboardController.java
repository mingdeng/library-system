package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.BorrowRecordService;
import com.library.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理端数据统计控制器
 * 
 * @author library-system
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final BookService bookService;
    private final UserService userService;
    private final BorrowRecordService borrowRecordService;
    
    /**
     * 获取统计数据
     */
    @GetMapping
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        // 总图书数
        LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
        bookWrapper.eq(Book::getIsDeleted, 0);
        long totalBooks = bookService.count(bookWrapper);
        data.put("totalBooks", totalBooks);
        
        // 总用户数
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getIsDeleted, 0);
        long totalUsers = userService.count(userWrapper);
        data.put("totalUsers", totalUsers);
        
        // 借阅中数量
        LambdaQueryWrapper<BorrowRecord> borrowWrapper = new LambdaQueryWrapper<>();
        borrowWrapper.eq(BorrowRecord::getStatus, "BORROWED");
        borrowWrapper.eq(BorrowRecord::getIsDeleted, 0);
        long borrowingCount = borrowRecordService.count(borrowWrapper);
        data.put("borrowingCount", borrowingCount);
        
        // 今日借阅数
        LambdaQueryWrapper<BorrowRecord> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(BorrowRecord::getIsDeleted, 0);
        todayWrapper.ge(BorrowRecord::getBorrowDate, LocalDateTime.now().toLocalDate().atStartOfDay());
        todayWrapper.lt(BorrowRecord::getBorrowDate, LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay());
        long todayBorrows = borrowRecordService.count(todayWrapper);
        data.put("todayBorrows", todayBorrows);
        
        return Result.success(data);
    }
}

