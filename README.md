# 图书管理系统

## 项目简介

图书管理系统是一套完整的图书管理解决方案，包含后端服务和两个独立的前端应用（读者端和管理端）。

![图书管理系统-读者端](.\images\图书管理系统-读者端.png)

![图书管理系统-读者端-查看详情](.\images\图书管理系统-读者端-查看详情.png)

![图书管理系统-管理端](.\images\图书管理系统-管理端.png)

![图书管理系统-管理端-编辑图书](.\images\图书管理系统-管理端-编辑图书.png)

## 技术栈

### 后端
- Spring Boot 3.2.0
- Java 17
- Maven
- MyBatis-Plus 3.5.5
- MySQL
- Spring Security
- JWT (jjwt 0.12.3)
- Lombok
- Knife4j
- MinIO

### 前端
- Vue 3.3.4
- Vue Router 4.2.5
- Pinia 2.1.7
- Element Plus 2.4.4
- Axios 1.6.2
- Vite 5.0.8

## 项目结构

```
library-system/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/library/
│   │   │   │   ├── common/     # 公共类（统一响应、异常处理等）
│   │   │   │   ├── config/      # 配置类（跨域、MyBatis-Plus、Security等）
│   │   │   │   ├── controller/ # 控制器层
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── filter/      # 过滤器（JWT认证）
│   │   │   │   ├── mapper/      # Mapper接口
│   │   │   │   ├── service/     # 服务层
│   │   │   │   └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       └── application-prod.yml
│   └── pom.xml
├── reader-frontend/            # 读者端前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia状态管理
│   │   ├── views/             # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
├── admin-frontend/            # 管理端前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia状态管理
│   │   ├── views/             # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
├── library_system.sql          # 数据库脚本
└── README.md                  # 项目说明文档
```

## 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 5.7+ 或 8.0+

## 快速开始

### 1. 数据库准备

1. 创建MySQL数据库：
```sql
CREATE DATABASE library_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本：
```bash
mysql -u root -p library_system < library_system.sql
```

或者直接在MySQL客户端中执行 `library_system.sql` 文件。

### 2. 后端启动

1. 进入后端目录：
```bash
cd backend
```

2. 修改数据库配置（如需要）：
编辑 `src/main/resources/application-dev.yml`，确认数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.6.100:3306/library_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 1234
```

3. 编译并启动：
```bash
mvn clean install
mvn spring-boot:run
```

或者使用IDE直接运行 `LibrarySystemApplication.java`

后端服务默认运行在：http://localhost:8080

### 3. 读者端启动

1. 进入读者端目录：
```bash
cd reader-frontend
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

读者端默认运行在：http://localhost:3000/reader

### 4. 管理端启动

1. 进入管理端目录：
```bash
cd admin-frontend
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

管理端默认运行在：http://localhost:3001/admin

## 默认账号

### 管理员账号
- 用户名：`admin`
- 密码：`admin123`

### 图书管理员账号
- 用户名：`librarian`
- 密码：`admin123`

### 读者账号
- 用户名：`reader001`
- 密码：`123456`

## 功能说明

### 读者端功能
- ✅ 用户登录
- ✅ 图书搜索和浏览
- ✅ 图书详情查看
- ✅ 图书借阅
- ✅ 图书续借
- ✅ 我的借阅列表
- ✅ 个人中心

### 管理端功能
- ✅ 管理员登录
- ✅ 数据统计（总图书数、总用户数、借阅中数量、今日借阅数）
- ✅ 图书管理（增删改查）
- ✅ 用户管理（增删改查）
- ✅ 搜索和筛选功能

## API接口说明

### 读者端接口
- `POST /api/reader/login` - 读者登录
- `GET /api/reader/books` - 获取图书列表（支持分页、搜索、分类筛选）
- `GET /api/reader/books/:id` - 获取图书详情
- `POST /api/reader/borrows` - 借阅图书
- `POST /api/reader/borrows/renew` - 续借图书
- `GET /api/reader/borrows` - 获取我的借阅列表

### 管理端接口
- `POST /api/admin/login` - 管理员登录
- `GET /api/admin/dashboard` - 获取统计数据
- `GET /api/admin/books` - 获取图书列表（支持分页、搜索、筛选）
- `GET /api/admin/books/:id` - 获取图书详情
- `POST /api/admin/books` - 新增图书
- `PUT /api/admin/books/:id` - 更新图书
- `DELETE /api/admin/books/:id` - 删除图书
- `GET /api/admin/users` - 获取用户列表（支持分页、搜索、筛选）
- `GET /api/admin/users/:id` - 获取用户详情
- `POST /api/admin/users` - 新增用户
- `PUT /api/admin/users/:id` - 更新用户
- `DELETE /api/admin/users/:id` - 删除用户

## 注意事项

1. **数据库连接**：确保MySQL服务已启动，并且数据库连接信息正确。

2. **JWT Token**：所有需要认证的接口都需要在请求头中携带Token：
   ```
   Authorization: Bearer <token>
   ```

3. **跨域配置**：后端已配置跨域，允许前端访问。

4. **密码加密**：所有密码都使用BCrypt加密存储。

5. **逻辑删除**：删除操作使用逻辑删除，不会真正删除数据库记录。

## 开发说明

### 后端开发
- 使用MyBatis-Plus进行数据库操作
- 统一使用`Result<T>`作为响应结构
- 使用`BusinessException`抛出业务异常
- 使用JWT进行身份认证

### 前端开发
- 使用Pinia进行状态管理
- 使用Element Plus作为UI组件库
- 使用Axios进行HTTP请求
- 请求拦截器自动添加Token

## 常见问题

1. **后端启动失败**：检查数据库连接配置和MySQL服务状态。

2. **前端无法连接后端**：检查后端服务是否启动，以及代理配置是否正确。

3. **登录失败**：确认用户名和密码正确，检查数据库中的密码是否为BCrypt加密格式。

4. **Token过期**：Token默认有效期为24小时，过期后需要重新登录。

## 许可证

本项目仅供学习和参考使用。

## 联系方式

如有问题或建议，欢迎通过邮箱联系：[37485197@qq.com](mailto:37485197@qq.com)

