package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Book;
import com.library.service.BookService;
import com.library.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 管理端图书控制器
 * 
 * @author library-system
 */
@RestController
@RequestMapping("/api/admin/books")
@RequiredArgsConstructor
public class AdminBookController {
    
    private final BookService bookService;

    private final MinioService minioService;
    
    /**
     * 分页查询图书列表
     */
    @GetMapping
    public Result<IPage<Book>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        Page<Book> page = new Page<>(current, size);
        IPage<Book> result = bookService.pageForAdmin(page, keyword, category, status);
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
    
    /**
     * 新增图书
     */
    @PostMapping
    public Result<Book> add(@RequestBody Book book) {
        // 设置默认值
        if (book.getTotalQuantity() == null) {
            book.setTotalQuantity(1);
        }
        if (book.getAvailableQuantity() == null) {
            book.setAvailableQuantity(book.getTotalQuantity());
        }
        if (book.getStatus() == null) {
            book.setStatus(1);
        }
        if (book.getBorrowCount() == null) {
            book.setBorrowCount(0);
        }
        
        bookService.save(book);
        return Result.success("新增成功", book);
    }
    
    /**
     * 更新图书
     */
    @PutMapping("/{id}")
    public Result<Book> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.updateById(book);
        return Result.success("更新成功", book);
    }
    
    /**
     * 删除图书（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.success("删除成功");
    }

    // 在图书管理控制器中添加上传封面的方法
    @PostMapping("/upload-cover")
    @Operation(summary = "上传图书封面")
    public Result<Map<String, String>> uploadBookCover(@RequestParam("bookId") Long bookId,
                                             @RequestParam("cover") MultipartFile cover) {
        try {
            if (cover.isEmpty()) {
                return Result.error("请选择文件");
            }

            // 生成文件名
            String fileName = "cover_" + bookId + "_" + System.currentTimeMillis() +
                    cover.getOriginalFilename().substring(cover.getOriginalFilename().lastIndexOf("."));

            // 上传到 MinIO
            String fileUrl = minioService.uploadFile(cover, fileName);

            // 更新图书封面信息到数据库
            Book book = bookService.getById(bookId);
            if (book != null) {
                book.setCoverUrl(fileUrl);
                bookService.updateById(book);
            }

            Map<String, String> resultMap = Map.of("url", fileUrl);
            return Result.success("上传成功", resultMap);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

}

