package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.Book;

/**
 * 图书服务接口
 * 
 * @author library-system
 */
public interface BookService extends IService<Book> {
    
    /**
     * 分页查询图书（读者端）
     * 
     * @param page 分页参数
     * @param keyword 关键词
     * @param category 分类
     * @return 分页结果
     */
    IPage<Book> pageForReader(Page<Book> page, String keyword, String category);
    
    /**
     * 分页查询图书（管理端）
     * 
     * @param page 分页参数
     * @param keyword 关键词
     * @param category 分类
     * @param status 状态
     * @return 分页结果
     */
    IPage<Book> pageForAdmin(Page<Book> page, String keyword, String category, Integer status);
}

