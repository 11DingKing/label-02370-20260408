# 图书管理系统 - 文档目录

## 项目文档清单

| 文档名称 | 说明 | 文件路径 |
|---------|------|---------|
| 项目设计文档 | 系统架构、ER图、接口清单、UML类图、时序图 | [project_design.md](./project_design.md) |
| UML 设计图 | 详细的 UML 类图和时序图（文本格式） | [UML图.md](./UML图.md) |

## UML 时序图清单

`docs/UML图.md` 文件包含以下时序图：

| 时序图 | 章节 | 说明 |
|-------|------|------|
| 用户注册时序图 | 2.1 | 完整的注册流程：参数校验→用户名查重→密码加密→数据库插入 |
| 用户登录时序图 | 2.2 | 完整的登录流程：用户查询→密码验证→JWT生成→返回Token |
| 图书借阅时序图 | 2.3 | 借阅流程：库存检查→重复借阅检查→创建记录→扣减库存 |
| 图书归还时序图 | 2.4 | 归还流程：记录验证→状态更新→恢复库存 |
| JWT认证拦截时序图 | 2.5 | 请求拦截：Token提取→解析验证→存入ThreadLocal→放行 |
| 知识点总结 | 涉及的技术知识点详解 | [知识点总结.md](./知识点总结.md) |
| 代码截图说明 | 各功能模块核心代码（用于截图） | [代码截图说明.md](./代码截图说明.md) |

## 配置文件

| 文件 | 说明 | 路径 |
|-----|------|-----|
| application.yml | Spring Boot 配置（YAML格式） | backend/src/main/resources/application.yml |
| application.properties | Spring Boot 配置（Properties格式） | backend/src/main/resources/application.properties |
| schema.sql | 数据库初始化脚本 | backend/src/main/resources/schema.sql |

## 作业提交检查清单

- [x] 用户注册、登录功能
- [x] JWT Token 认证
- [x] 图书 CRUD 管理
- [x] 图书借阅、归还功能
- [x] 借阅记录查询
- [x] 用户管理（管理员）
- [x] 新书推荐功能
- [x] 分页查询
- [x] 参数校验
- [x] 全局异常处理
- [x] AOP 日志记录
- [x] 事务管理
- [x] 前端 Vue3 + Element Plus
- [x] Pinia 状态管理
- [x] 路由守卫权限控制
- [x] ER 图
- [x] UML 类图
- [x] UML 时序图
- [x] 知识点总结文档
- [x] 配置文件（yml + properties）
- [ ] 代码截图（需手动截取）

## 默认账号

| 角色 | 用户名 | 密码 |
|-----|-------|------|
| 管理员 | admin | admin123 |
| 普通用户 | user | user123 |

## 技术栈

**后端：**
- Spring Boot 3.2.0
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JWT (jjwt 0.12.3)
- Lombok

**前端：**
- Vue 3.4
- Pinia 2.1
- Vue Router 4.2
- Element Plus 2.4
- Axios 1.6
- Vite 5.0
