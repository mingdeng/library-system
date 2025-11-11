-- ========================================
-- 图书管理系统数据库设计
-- 版本: 1.0
-- 日期: 2025-10-23
-- ========================================

-- 删除已存在的表(开发环境用,生产环境请注释)
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS borrow_records;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS operation_logs;
DROP TABLE IF EXISTS system_config;
DROP TABLE IF EXISTS users;

-- ========================================
-- 1. 用户表(读者 + 管理员统一管理)
-- ========================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    
    -- 登录信息
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名(登录账号)',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    role VARCHAR(20) NOT NULL COMMENT '角色: READER读者, LIBRARIAN管理员, ADMIN系统管理员',
    
    -- 基本信息
    real_name VARCHAR(50) COMMENT '真实姓名',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    gender TINYINT COMMENT '性别: 0未知 1男 2女',
    birth_date DATE COMMENT '出生日期',
    
    -- 读者专用字段
    student_id VARCHAR(30) COMMENT '学号/工号',
    department VARCHAR(100) COMMENT '院系/部门',
    reader_type TINYINT COMMENT '读者类型: 1本科生 2研究生 3教师 4校外人员',
    credit_score INT DEFAULT 100 COMMENT '信用分(默认100,逾期扣分)',
    max_borrow_count INT DEFAULT 3 COMMENT '最大可借数量',
    max_borrow_days INT DEFAULT 30 COMMENT '最大借阅天数',
    
    -- 管理员专用字段
    post_id BIGINT COMMENT '岗位ID(管理员)',
    post_name VARCHAR(50) COMMENT '岗位名称',
    permissions VARCHAR(500) COMMENT '权限列表(JSON格式)',
    
    -- 状态字段
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0禁用 2待审核',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    
    -- 时间字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    
    -- 索引
    INDEX idx_username (username),
    INDEX idx_student_id (student_id),
    INDEX idx_phone (phone),
    INDEX idx_role (role),
    INDEX idx_status (status),
    INDEX idx_reader_type (reader_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表(读者+管理员)';

-- ========================================
-- 2. 图书表
-- ========================================
CREATE TABLE books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    
    -- 基本信息
    isbn VARCHAR(20) COMMENT 'ISBN号',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    subtitle VARCHAR(200) COMMENT '副标题',
    author VARCHAR(100) COMMENT '作者',
    translator VARCHAR(100) COMMENT '译者',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    edition VARCHAR(50) COMMENT '版次(如"第3版")',
    pages INT COMMENT '页数',
    price DECIMAL(10,2) COMMENT '定价(元)',
    
    -- 分类信息
    category VARCHAR(50) COMMENT '分类(如"计算机/编程")',
    tags VARCHAR(200) COMMENT '标签(JSON数组)',
    classification_number VARCHAR(50) COMMENT '分类号(中图法)',
    
    -- 描述信息
    summary TEXT COMMENT '内容简介',
    catalog TEXT COMMENT '目录',
    cover_url VARCHAR(255) COMMENT '封面图片URL',
    
    -- 库存信息
    total_quantity INT DEFAULT 1 COMMENT '总数量(馆藏总数)',
    available_quantity INT DEFAULT 1 COMMENT '可借数量',
    location VARCHAR(50) COMMENT '书架位置(如"A区3层05架")',
    
    -- 状态与统计
    status TINYINT DEFAULT 1 COMMENT '状态: 1上架 0下架 2维修中 3丢失',
    borrow_count INT DEFAULT 0 COMMENT '累计借阅次数',
    reserve_count INT DEFAULT 0 COMMENT '当前预约人数',
    popularity_score DECIMAL(5,2) DEFAULT 0 COMMENT '热度分数(用于推荐)',
    
    -- 元数据
    created_by BIGINT COMMENT '创建人ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_isbn (isbn),
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_popularity (popularity_score),
    INDEX idx_location (location),
    FULLTEXT INDEX ft_search (title, author, publisher) WITH PARSER ngram -- 中文全文搜索
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- ========================================
-- 3. 借阅记录表
-- ========================================
CREATE TABLE borrow_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '借阅记录ID',
    
    -- 关联信息
    user_id BIGINT NOT NULL COMMENT '借阅用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    
    -- 时间信息
    borrow_date DATETIME NOT NULL COMMENT '借阅时间',
    due_date DATETIME NOT NULL COMMENT '应还时间',
    return_date DATETIME COMMENT '实际归还时间',
    
    -- 续借信息
    renew_count INT DEFAULT 0 COMMENT '续借次数',
    last_renew_date DATETIME COMMENT '最后续借时间',
    
    -- 状态信息
    status VARCHAR(20) NOT NULL COMMENT '状态: BORROWED借出, RETURNED已还, OVERDUE逾期, LOST丢失, DAMAGED损坏',
    
    -- 操作信息
    borrow_operator_id BIGINT COMMENT '借书操作员ID',
    return_operator_id BIGINT COMMENT '还书操作员ID',
    borrow_type VARCHAR(20) DEFAULT 'COUNTER' COMMENT '借阅方式: COUNTER柜台, SELF自助, ONLINE在线',
    
    -- 费用信息
    overdue_days INT DEFAULT 0 COMMENT '逾期天数',
    overdue_fee DECIMAL(10,2) DEFAULT 0 COMMENT '逾期罚款(元)',
    damage_fee DECIMAL(10,2) DEFAULT 0 COMMENT '损坏赔偿(元)',
    is_paid TINYINT DEFAULT 0 COMMENT '是否已支付: 0未付 1已付',
    pay_time DATETIME COMMENT '支付时间',
    
    -- 备注
    borrow_remark VARCHAR(200) COMMENT '借书备注(如"书籍轻微破损")',
    return_remark VARCHAR(200) COMMENT '还书备注',
    
    -- 评价(可选功能)
    rating TINYINT COMMENT '评分(1-5星)',
    review TEXT COMMENT '评价内容',
    
    -- 元数据
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_borrow_date (borrow_date),
    INDEX idx_due_date (due_date),
    INDEX idx_return_date (return_date),
    INDEX idx_user_status (user_id, status), -- 组合索引,查询用户当前借阅
    
    -- 外键
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- ========================================
-- 4. 预约表
-- ========================================
CREATE TABLE reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    
    -- 关联信息
    user_id BIGINT NOT NULL COMMENT '预约用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    
    -- 时间信息
    reserve_date DATETIME NOT NULL COMMENT '预约时间',
    notify_date DATETIME COMMENT '通知时间(书可借时发送)',
    expire_date DATETIME COMMENT '预约失效时间(通知后N天不借)',
    cancel_date DATETIME COMMENT '取消时间',
    borrow_date DATETIME COMMENT '借阅时间',
    
    -- 状态信息
    status VARCHAR(20) NOT NULL COMMENT '状态: WAITING等待中, NOTIFIED已通知, BORROWED已借, EXPIRED已过期, CANCELLED已取消',
    
    -- 队列信息
    queue_number INT COMMENT '排队号码(第几个预约)',
    
    -- 备注
    remark VARCHAR(200) COMMENT '备注',
    cancel_reason VARCHAR(200) COMMENT '取消原因',
    
    -- 元数据
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status),
    INDEX idx_reserve_date (reserve_date),
    INDEX idx_book_status (book_id, status), -- 查询某书的预约队列
    UNIQUE INDEX uk_user_book_waiting (user_id, book_id, status) -- 防止重复预约(同一用户同一书同一状态只能有一条)
    
    -- 外键
    -- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    -- FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书预约表';

-- ========================================
-- 5. 通知消息表
-- ========================================
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    
    -- 接收者
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    
    -- 消息内容
    type VARCHAR(20) NOT NULL COMMENT '消息类型: DUE_REMIND到期提醒, OVERDUE逾期通知, RESERVE预约通知, SYSTEM系统消息',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    
    -- 关联信息(可选)
    related_type VARCHAR(20) COMMENT '关联类型: BORROW借阅, RESERVATION预约',
    related_id BIGINT COMMENT '关联ID',
    
    -- 状态
    is_read TINYINT DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
    is_sent TINYINT DEFAULT 0 COMMENT '是否已发送: 0未发 1已发',
    send_type VARCHAR(20) DEFAULT 'APP' COMMENT '发送方式: APP站内信, SMS短信, EMAIL邮件, WECHAT微信',
    send_result VARCHAR(200) COMMENT '发送结果(失败时记录原因)',
    
    -- 时间
    send_time DATETIME COMMENT '发送时间',
    read_time DATETIME COMMENT '阅读时间',
    
    -- 元数据
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time),
    INDEX idx_user_read (user_id, is_read) -- 查询用户未读消息
    
    -- 外键
    -- FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知消息表';

-- ========================================
-- 6. 系统配置表
-- ========================================
CREATE TABLE system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    
    config_key VARCHAR(50) UNIQUE NOT NULL COMMENT '配置键',
    config_value VARCHAR(500) NOT NULL COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '数据类型: INT, DECIMAL, STRING, JSON, BOOLEAN',
    config_group VARCHAR(50) COMMENT '配置分组',
    description VARCHAR(200) COMMENT '描述说明',
    
    -- 元数据
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ========================================
-- 7. 操作日志表(可选,用于审计)
-- ========================================
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    
    -- 操作信息
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型: LOGIN登录, BORROW借书, RETURN还书, ADD_BOOK添加图书等',
    operation_desc VARCHAR(200) COMMENT '操作描述',
    
    -- 请求信息
    request_method VARCHAR(10) COMMENT '请求方法(GET/POST等)',
    request_url VARCHAR(200) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数(JSON)',
    
    -- 响应信息
    response_result VARCHAR(20) COMMENT '响应结果: SUCCESS成功, FAIL失败',
    response_msg TEXT COMMENT '响应消息',
    
    -- 环境信息
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '浏览器信息',
    
    -- 时间
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    execution_time INT COMMENT '执行时长(毫秒)',
    
    -- 索引
    INDEX idx_operator_id (operator_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ========================================
-- 初始化系统配置数据
-- ========================================
INSERT INTO system_config (config_key, config_value, config_type, config_group, description) VALUES
-- 借阅规则
('max_borrow_count', '3', 'INT', 'borrow_rule', '默认最大可借数量'),
('max_borrow_days', '30', 'INT', 'borrow_rule', '默认最大借阅天数'),
('max_renew_count', '2', 'INT', 'borrow_rule', '最大续借次数'),
('renew_days', '15', 'INT', 'borrow_rule', '每次续借延长天数'),

-- 费用规则
('overdue_fee_per_day', '0.50', 'DECIMAL', 'fee_rule', '每天逾期罚款(元)'),
('max_overdue_fee', '50.00', 'DECIMAL', 'fee_rule', '单本图书最高罚款(元)'),
('damage_fee_rate', '1.5', 'DECIMAL', 'fee_rule', '损坏赔偿倍率(书价*倍率)'),
('lost_fee_rate', '2.0', 'DECIMAL', 'fee_rule', '丢失赔偿倍率(书价*倍率)'),

-- 预约规则
('reserve_expire_days', '3', 'INT', 'reserve_rule', '预约保留天数(通知后N天内不借则失效)'),
('max_reserve_count', '5', 'INT', 'reserve_rule', '单个用户最大预约数量'),

-- 提醒规则
('due_remind_days', '3', 'INT', 'remind_rule', '到期前N天提醒'),
('remind_time', '09:00', 'STRING', 'remind_rule', '每天提醒时间'),

-- 信用分规则
('credit_deduct_per_day', '5', 'INT', 'credit_rule', '逾期每天扣除信用分'),
('credit_freeze_threshold', '60', 'INT', 'credit_rule', '信用分低于此值冻结借阅权限'),
('credit_recover_per_month', '10', 'INT', 'credit_rule', '每月自动恢复信用分'),

-- 系统设置
('library_name', '在线图书馆', 'STRING', 'system', '图书馆名称'),
('library_email', 'library@example.com', 'STRING', 'system', '图书馆邮箱'),
('library_phone', '400-123-4567', 'STRING', 'system', '图书馆电话'),
('open_time', '08:00-22:00', 'STRING', 'system', '开放时间');

-- ========================================
-- 初始化测试数据
-- ========================================

-- 插入管理员账号(密码: admin123, 需要用BCrypt加密)
INSERT INTO users (username, password, role, real_name, phone, email, status, post_name) VALUES
('admin', '$2a$10$0Omj9XIoaz7/2lQbBU3FnuS61H/1ehsz/juokamXxmPwqFQbtcu0O', 'ADMIN', '系统管理员', '13800138000', 'admin@example.com', 1, '系统管理员'),
('librarian', '$2a$10$0Omj9XIoaz7/2lQbBU3FnuS61H/1ehsz/juokamXxmPwqFQbtcu0O', 'LIBRARIAN', '张图书', '13800138001', 'librarian@example.com', 1, '图书管理员');

-- 插入测试读者(密码: 123456)
INSERT INTO users (username, password, role, real_name, student_id, department, phone, email, reader_type, status) VALUES
('reader001', '$2a$10$eu0b8cRDBfkxhtexxUFx0uyi5Kno6BRotb0jfLcXpuTtz2qg9nBvG', 'READER', '李同学', '2021001', '计算机学院', '13900139001', 'reader001@example.com', 1, 1),
('reader002', '$2a$10$eu0b8cRDBfkxhtexxUFx0uyi5Kno6BRotb0jfLcXpuTtz2qg9nBvG', 'READER', '王同学', '2021002', '文学院', '13900139002', 'reader002@example.com', 1, 1),
('reader003', '$2a$10$eu0b8cRDBfkxhtexxUFx0uyi5Kno6BRotb0jfLcXpuTtz2qg9nBvG', 'READER', '刘老师', 'T2021001', '计算机学院', '13900139003', 'teacher@example.com', 3, 1);

-- 插入测试图书
INSERT INTO books (isbn, title, author, publisher, publish_date, category, price, total_quantity, available_quantity, location, summary, status) VALUES
('9787111213826', 'Java核心技术 卷I', 'Cay S. Horstmann', '机械工业出版社', '2020-09-01', '计算机/编程', 119.00, 5, 3, 'A区3层01架', 'Java技术经典参考书，全面介绍Java语言核心概念和库。', 1),
('9787115428028', 'Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '2019-06-01', '计算机/编程', 89.00, 3, 2, 'A区3层02架', '实用的Python编程入门书，通过实例学习Python编程。', 1),
('9787115335500', '算法导论', 'Thomas H. Cormen', '机械工业出版社', '2012-12-01', '计算机/算法', 128.00, 2, 0, 'A区3层03架', '计算机算法领域的经典教材。', 1),
('9787115468581', '深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '2016-11-01', '计算机/系统', 139.00, 4, 4, 'A区3层04架', '从程序员角度理解计算机系统如何工作。', 1),
('9787020002207', '红楼梦', '曹雪芹', '人民文学出版社', '1996-12-01', '文学/古典', 39.50, 10, 8, 'B区1层01架', '中国古典四大名著之一。', 1),
('9787020008735', '三体', '刘慈欣', '重庆出版社', '2008-01-01', '文学/科幻', 23.00, 6, 4, 'B区2层05架', '雨果奖最佳长篇小说，中国科幻巅峰之作。', 1);

-- 插入测试借阅记录
INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, status, borrow_operator_id) VALUES
(3, 1, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 'BORROWED', 2),
(3, 6, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), 'BORROWED', 2),
(4, 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), 'BORROWED', 2);

-- 插入测试预约记录
INSERT INTO reservations (user_id, book_id, reserve_date, status, queue_number) VALUES
(4, 3, NOW(), 'WAITING', 1),
(5, 3, DATE_ADD(NOW(), INTERVAL 1 HOUR), 'WAITING', 2);

-- ========================================
-- 创建视图(可选,方便查询)
-- ========================================

-- 当前借阅视图
CREATE OR REPLACE VIEW v_current_borrows AS
SELECT 
    br.id,
    br.user_id,
    u.username,
    u.real_name,
    u.student_id,
    br.book_id,
    b.title AS book_title,
    b.author,
    b.isbn,
    br.borrow_date,
    br.due_date,
    DATEDIFF(br.due_date, NOW()) AS days_left,
    br.renew_count,
    br.status
FROM borrow_records br
JOIN users u ON br.user_id = u.id
JOIN books b ON br.book_id = b.id
WHERE br.status IN ('BORROWED', 'OVERDUE')
  AND br.is_deleted = 0;

-- 热门图书视图
CREATE OR REPLACE VIEW v_popular_books AS
SELECT 
    b.id,
    b.title,
    b.author,
    b.publisher,
    b.category,
    b.borrow_count,
    b.available_quantity,
    b.total_quantity,
    COUNT(r.id) AS reserve_count
FROM books b
LEFT JOIN reservations r ON b.id = r.book_id AND r.status = 'WAITING'
WHERE b.is_deleted = 0 AND b.status = 1
GROUP BY b.id
ORDER BY b.borrow_count DESC, reserve_count DESC;

-- ========================================
-- 数据库设计说明
-- ========================================
/*
1. 字符集统一使用 utf8mb4,支持emoji和生僻字
2. 所有表都有 is_deleted 字段,使用逻辑删除
3. 所有表都有 create_time 和 update_time
4. 关键查询字段都建立了索引
5. 外键约束使用 RESTRICT 防止误删
6. 预留了扩展字段(如tags、permissions使用JSON)

密码加密说明:
- 示例中的密码hash是BCrypt加密的
- admin/librarian 密码: admin123
- reader001-003 密码: 123456
- 生产环境请使用更强的密码!

建议:
- 开发环境可以用上述测试数据
- 生产环境删除测试数据,只保留admin账号
- 定期备份数据库
- 监控 borrow_records 表大小,考虑分表策略
*/