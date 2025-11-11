package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书实体类
 * 
 * @author library-system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("books")
public class Book extends BaseEntity {
    
    /**
     * ISBN号
     */
    private String isbn;
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 副标题
     */
    private String subtitle;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 译者
     */
    private String translator;
    
    /**
     * 出版社
     */
    private String publisher;
    
    /**
     * 出版日期
     */
    private LocalDate publishDate;
    
    /**
     * 版次
     */
    private String edition;
    
    /**
     * 页数
     */
    private Integer pages;
    
    /**
     * 定价（元）
     */
    private BigDecimal price;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 标签（JSON数组）
     */
    private String tags;
    
    /**
     * 分类号（中图法）
     */
    private String classificationNumber;
    
    /**
     * 内容简介
     */
    private String summary;
    
    /**
     * 目录
     */
    private String catalog;
    
    /**
     * 封面图片URL
     */
    private String coverUrl;
    
    /**
     * 总数量（馆藏总数）
     */
    private Integer totalQuantity;
    
    /**
     * 可借数量
     */
    private Integer availableQuantity;
    
    /**
     * 书架位置
     */
    private String location;
    
    /**
     * 状态：1上架 0下架 2维修中 3丢失
     */
    private Integer status;
    
    /**
     * 累计借阅次数
     */
    private Integer borrowCount;
    
    /**
     * 当前预约人数
     */
    private Integer reserveCount;
    
    /**
     * 热度分数（用于推荐）
     */
    private BigDecimal popularityScore;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
}

