package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import org.springframework.stereotype.Service;

/**
 * 图书服务实现类
 * 
 * @author library-system
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    
    @Override
    public IPage<Book> pageForReader(Page<Book> page, String keyword, String category) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsDeleted, 0);
        wrapper.eq(Book::getStatus, 1); // 只显示上架的图书
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword)
                    .or().like(Book::getPublisher, keyword));
        }
        
        // 分类筛选
        if (category != null && !category.trim().isEmpty() && !"全部".equals(category)) {
            wrapper.like(Book::getCategory, category);
        }
        
        wrapper.orderByDesc(Book::getBorrowCount);
        
        return this.page(page, wrapper);
    }
    
    @Override
    public IPage<Book> pageForAdmin(Page<Book> page, String keyword, String category, Integer status) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsDeleted, 0);
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword)
                    .or().like(Book::getIsbn, keyword));
        }
        
        // 分类筛选
        if (category != null && !category.trim().isEmpty() && !"全部".equals(category)) {
            wrapper.like(Book::getCategory, category);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Book::getStatus, status);
        }
        
        wrapper.orderByDesc(Book::getCreateTime);
        
        return this.page(page, wrapper);
    }
}

