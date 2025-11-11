package com.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 图书管理系统启动类
 * 
 * @author library-system
 */
@SpringBootApplication
@MapperScan("com.library.mapper")
public class LibrarySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySystemApplication.class, args);
    }
}

