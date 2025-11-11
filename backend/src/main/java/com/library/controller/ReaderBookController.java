package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 读者端图书控制器
 * 
 * @author library-system
 */
@RestController
@RequestMapping("/api/reader/books")
@RequiredArgsConstructor
public class ReaderBookController {
    
    private final BookService bookService;
    
    /**
     * 分页查询图书列表
     */
    @GetMapping
    public Result<IPage<Book>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        Page<Book> page = new Page<>(current, size);
        IPage<Book> result = bookService.pageForReader(page, keyword, category);
        return Result.success(result);
    }
    
    /**
     * 获取图书详情
     */
    @GetMapping("/{id}")
    public Result<Book> detail(@PathVariable Long id) {
        Book book = bookService.getById(id);
        if (book == null || book.getIsDeleted() == 1) {
            return Result.error("图书不存在");
        }
        return Result.success(book);
    }
}

