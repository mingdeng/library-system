package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.common.BusinessException;
import com.library.dto.BorrowRecordWithBookDTO;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BookService;
import com.library.service.BorrowRecordService;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 借阅记录服务实现类
 * 
 * @author library-system
 */
@Service
@RequiredArgsConstructor
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {
    
    private final BookService bookService;
    private final UserService userService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecord borrowBook(Long userId, Long bookId) {
        // 查询用户
        User user = userService.getById(userId);
        if (user == null || user.getIsDeleted() == 1) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 检查用户信用分
        if (user.getCreditScore() != null && user.getCreditScore() < 60) {
            throw new BusinessException("信用分不足，无法借阅");
        }
        
        // 查询图书
        Book book = bookService.getById(bookId);
        if (book == null || book.getIsDeleted() == 1) {
            throw new BusinessException("图书不存在");
        }
        
        // 检查图书状态
        if (book.getStatus() != 1) {
            throw new BusinessException("图书已下架");
        }
        
        // 检查可借数量
        if (book.getAvailableQuantity() == null || book.getAvailableQuantity() <= 0) {
            throw new BusinessException("图书已被借完");
        }
        
        // 检查用户当前借阅数量
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId);
        wrapper.in(BorrowRecord::getStatus, "BORROWED", "OVERDUE");
        wrapper.eq(BorrowRecord::getIsDeleted, 0);
        long currentBorrowCount = this.count(wrapper);
        
        int maxBorrowCount = user.getMaxBorrowCount() != null ? user.getMaxBorrowCount() : 3;
        if (currentBorrowCount >= maxBorrowCount) {
            throw new BusinessException("已达到最大借阅数量");
        }
        
        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        
        int maxBorrowDays = user.getMaxBorrowDays() != null ? user.getMaxBorrowDays() : 30;
        record.setDueDate(LocalDateTime.now().plusDays(maxBorrowDays));
        record.setStatus("BORROWED");
        record.setBorrowType("ONLINE");
        record.setRenewCount(0);
        
        this.save(record);
        
        // 更新图书可借数量
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        book.setBorrowCount(book.getBorrowCount() != null ? book.getBorrowCount() + 1 : 1);
        bookService.updateById(book);
        
        return record;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecord renewBook(Long recordId, Long userId) {
        // 查询借阅记录
        BorrowRecord record = this.getById(recordId);
        if (record == null || record.getIsDeleted() == 1) {
            throw new BusinessException("借阅记录不存在");
        }
        
        // 验证用户
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此借阅记录");
        }
        
        // 检查状态
        if (!"BORROWED".equals(record.getStatus())) {
            throw new BusinessException("只能续借借阅中的图书");
        }
        
        // 检查续借次数
        int renewCount = record.getRenewCount() != null ? record.getRenewCount() : 0;
        if (renewCount >= 2) {
            throw new BusinessException("已达到最大续借次数");
        }
        
        // 续借：延长15天
        record.setDueDate(record.getDueDate().plusDays(15));
        record.setRenewCount(renewCount + 1);
        record.setLastRenewDate(LocalDateTime.now());
        
        this.updateById(record);
        
        return record;
    }
    
    @Override
    public List<BorrowRecord> getBorrowListByUserId(Long userId) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId);
        wrapper.eq(BorrowRecord::getIsDeleted, 0);
        wrapper.orderByDesc(BorrowRecord::getBorrowDate);
        return this.list(wrapper);
    }
    
    @Override
    public List<BorrowRecordWithBookDTO> getBorrowListWithBookByUserId(Long userId) {
        // 获取用户的借阅记录
        List<BorrowRecord> borrowRecords = getBorrowListByUserId(userId);
        
        // 获取涉及的图书ID列表
        List<Long> bookIds = borrowRecords.stream()
                .map(BorrowRecord::getBookId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询图书详情
        List<Book> books = bookIds.isEmpty() ? new ArrayList<>() : bookService.listByIds(bookIds);
        
        // 构建图书ID到图书对象的映射
        java.util.Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));
        
        // 构建结果
        return borrowRecords.stream().map(record -> {
            BorrowRecordWithBookDTO dto = new BorrowRecordWithBookDTO();
            // 复制BorrowRecord的所有属性到DTO
            BeanUtils.copyProperties(record, dto);
            // 设置图书详情
            dto.setBook(bookMap.get(record.getBookId()));
            return dto;
        }).collect(Collectors.toList());
    }
}