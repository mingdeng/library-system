package com.library.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自动填充处理器
 * 
 * @author library-system
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    
    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasGetter("createTime")) {
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        }
        if (metaObject.hasGetter("updateTime")) {
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }
    }
    
    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasGetter("updateTime")) {
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }
    }
}

