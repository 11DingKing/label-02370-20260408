# 图书管理系统

一个基于 Spring Boot 3 + Vue 3 + Element Plus 的图书管理系统。

## How to Run

### Docker 方式（推荐）

```bash
# 启动所有服务
docker-compose up --build -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 本地开发

**后端：**
```bash
cd backend
mvn spring-boot:run
```

**前端：**
```bash
cd frontend-admin
npm install
npm run dev
```

**数据库：**
- 创建 MySQL 数据库 `library_db`
- 执行 `backend/src/main/resources/schema.sql` 初始化表结构

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| Frontend | 8081 | 前端管理界面 |
| Backend | 8080 | 后端 API 服务 |
| MySQL | 3306 | 数据库服务 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |

**说明：** 系统启动时会自动创建以上测试账号。

## 题目内容

### 项目需求

1. **项目文件组织结构**
   - Controller -> Service -> Mapper -> Entity 分层架构
   - 关键配置文件：pom.xml, application.yml

2. **数据库设计**
   - 概念设计：用户、图书、借阅记录、操作日志
   - 逻辑设计：ER 图见 docs/project_design.md
   - 物理设计：schema.sql 建表脚本

3. **系统功能设计与实现**
   - 3.1 用户注册、登录与退出登录
   - 3.2 图书管理系统（新书推荐、图书借阅、当前借阅、借阅记录）
   - 3.3 访问权限控制（JWT + 角色权限）

### 功能模块

- ✅ 用户注册
- ✅ 用户登录
- ✅ 用户退出登录
- ✅ 新书推荐
- ✅ 图书查询
- ✅ 图书借阅
- ✅ 新增图书（管理员）
- ✅ 编辑图书（管理员）
- ✅ 删除图书（管理员）
- ✅ 当前借阅查询
- ✅ 归还图书
- ✅ 确认归还（管理员）
- ✅ 借阅记录查询
- ✅ 用户管理（管理员）
- ✅ JWT 访问权限控制
- ✅ 操作日志记录

## 访问地址

- 前端界面：http://localhost:8081
- 后端 API：http://localhost:8080/api

## 技术栈

**后端：**
- Java 17
- Spring Boot 3.2
- MyBatis-Plus 3.5
- MySQL 8.0
- JWT 认证

**前端：**
- Vue 3
- Vite 5
- Element Plus
- Pinia
- Axios
- Vue Router

## 项目结构

```
label-02370/
├── backend/                    # 后端项目
│   ├── src/main/java/com/library/
│   │   ├── annotation/         # 自定义注解
│   │   ├── aspect/             # AOP切面
│   │   ├── common/             # 公共类
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制器
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── exception/          # 异常处理
│   │   ├── interceptor/        # 拦截器
│   │   ├── mapper/             # MyBatis Mapper
│   │   ├── service/            # 服务层
│   │   ├── util/               # 工具类
│   │   └── vo/                 # 视图对象
│   ├── src/test/               # 后端测试
│   │   ├── java/com/library/
│   │   │   ├── controller/     # Controller测试
│   │   │   ├── service/        # Service测试
│   │   │   └── util/           # 工具类测试
│   │   └── resources/
│   │       ├── application-test.yml
│   │       └── schema-h2.sql
│   ├── src/main/resources/
│   │   ├── application.yml     # 配置文件
│   │   └── schema.sql          # 数据库脚本
│   ├── Dockerfile
│   └── pom.xml
├── frontend-admin/             # 前端项目
│   ├── src/
│   │   ├── __tests__/          # 前端测试
│   │   │   ├── api/            # API测试
│   │   │   ├── components/     # 组件测试
│   │   │   ├── stores/         # Store测试
│   │   │   └── utils/          # 工具测试
│   │   ├── api/                # API 接口
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── styles/             # 样式文件
│   │   └── views/              # 页面组件
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── vitest.config.js        # 测试配置
│   └── package.json
├── docs/
│   └── project_design.md       # 项目设计文档
├── docker-compose.yml
├── .gitignore
├── label-02370.md              # 对话记录
└── README.md
```

## 测试

### 后端测试

```bash
cd backend
mvn test
```

测试覆盖：
- AuthServiceTest: 8个测试用例（注册、登录、退出、用户信息）
- BookServiceTest: 10个测试用例（CRUD、查询、新书推荐）
- BorrowServiceTest: 11个测试用例（借阅、归还、记录查询）
- UserServiceTest: 5个测试用例（用户管理）
- JwtUtilTest: 7个测试用例（Token生成、解析、验证）
- PasswordEncoderTest: 6个测试用例（加密、验证）
- Controller集成测试: 权限验证、API接口测试

### 前端测试

```bash
cd frontend-admin
npm install
npm run test
```

测试覆盖：
- user.test.js: 7个测试用例（状态管理）
- request.test.js: 5个测试用例（请求拦截）
- validators.test.js: 10个测试用例（表单验证）
- Login.test.js: 6个测试用例（组件交互）

**测试结果：28个前端测试用例全部通过**
