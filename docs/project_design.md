# 图书管理系统 - 项目设计文档

## 1. 系统架构

```mermaid
flowchart TD
    subgraph Frontend["前端 (Vue3 + Element Plus)"]
        A[用户界面] --> B[Pinia Store]
        B --> C[Axios API]
    end
    
    subgraph Backend["后端 (Spring Boot 3)"]
        D[Controller层] --> E[Service层]
        E --> F[Mapper层]
        F --> G[(MySQL 8.0)]
    end
    
    subgraph Security["安全层"]
        H[JWT Filter] --> I[权限校验]
    end
    
    C -->|HTTP Request| H
    H --> D
    I --> D
```

## 2. ER 图

```mermaid
erDiagram
    USER ||--o{ BORROW_RECORD : creates
    BOOK ||--o{ BORROW_RECORD : has
    
    USER {
        bigint id PK
        varchar username UK
        varchar password
        varchar email
        varchar phone
        int role
        datetime create_time
        datetime update_time
    }
    
    BOOK {
        bigint id PK
        varchar isbn UK
        varchar title
        varchar author
        varchar publisher
        varchar category
        text description
        varchar cover_url
        int total_count
        int available_count
        tinyint is_new
        datetime publish_date
        datetime create_time
        datetime update_time
    }
    
    BORROW_RECORD {
        bigint id PK
        bigint user_id FK
        bigint book_id FK
        datetime borrow_time
        datetime due_time
        datetime return_time
        int status
        datetime create_time
        datetime update_time
    }
    
    OPERATION_LOG {
        bigint id PK
        bigint user_id
        varchar username
        varchar operation
        varchar method
        varchar params
        varchar ip
        int status
        varchar error_msg
        bigint cost_time
        datetime create_time
    }
```

## 3. 接口清单

### 3.1 AuthController - 认证接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/logout | 用户退出 |
| GET | /api/auth/info | 获取当前用户信息 |

### 3.2 BookController - 图书接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/books | 分页查询图书 |
| GET | /api/books/{id} | 获取图书详情 |
| POST | /api/books | 新增图书 |
| PUT | /api/books/{id} | 编辑图书 |
| DELETE | /api/books/{id} | 删除图书 |
| GET | /api/books/new | 获取新书推荐 |

### 3.3 BorrowController - 借阅接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/borrows | 借阅图书 |
| PUT | /api/borrows/{id}/return | 归还图书 |
| PUT | /api/borrows/{id}/confirm | 确认归还 |
| GET | /api/borrows/current | 当前借阅列表 |
| GET | /api/borrows/history | 借阅历史记录 |

### 3.4 UserController - 用户管理接口 (管理员)
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/users | 分页查询用户 |
| PUT | /api/users/{id}/status | 修改用户状态 |

## 4. UI/UX 规范

### 4.1 色彩系统
- 主色调: `#409EFF` (Element Plus 默认蓝)
- 成功色: `#67C23A`
- 警告色: `#E6A23C`
- 危险色: `#F56C6C`
- 信息色: `#909399`
- 背景色: `#F5F7FA`
- 卡片背景: `#FFFFFF`

### 4.2 字体规范
- 主字体: `-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial`
- 标题字号: 20px / 18px / 16px
- 正文字号: 14px
- 辅助文字: 12px

### 4.3 间距规范
- 页面边距: 24px
- 卡片内边距: 20px
- 元素间距: 16px / 12px / 8px

### 4.4 圆角规范
- 卡片圆角: 8px
- 按钮圆角: 4px
- 输入框圆角: 4px

## 5. UML 类图

### 5.1 用户认证相关类

```mermaid
classDiagram
    class AuthController {
        -AuthService authService
        +register(RegisterDTO) Result
        +login(LoginDTO) Result
        +logout() Result
        +getUserInfo() Result
    }
    
    class AuthService {
        <<interface>>
        +register(RegisterDTO) void
        +login(LoginDTO) TokenVO
        +logout(Long userId) void
        +getUserInfo(Long userId) UserVO
    }
    
    class AuthServiceImpl {
        -UserMapper userMapper
        -JwtUtil jwtUtil
        -PasswordEncoder passwordEncoder
        +register(RegisterDTO) void
        +login(LoginDTO) TokenVO
        +logout(Long userId) void
        +getUserInfo(Long userId) UserVO
    }
    
    class UserMapper {
        <<interface>>
        +selectByUsername(String) User
        +insert(User) int
    }
    
    class User {
        -Long id
        -String username
        -String password
        -String email
        -String phone
        -Integer role
        -LocalDateTime createTime
        -LocalDateTime updateTime
    }
    
    AuthController --> AuthService
    AuthServiceImpl ..|> AuthService
    AuthServiceImpl --> UserMapper
    UserMapper --> User
```

### 5.2 图书管理相关类

```mermaid
classDiagram
    class BookController {
        -BookService bookService
        +list(BookQueryDTO) Result
        +getById(Long) Result
        +add(BookDTO) Result
        +update(Long, BookDTO) Result
        +delete(Long) Result
        +getNewBooks() Result
    }
    
    class BookService {
        <<interface>>
        +list(BookQueryDTO) PageResult~BookVO~
        +getById(Long) BookVO
        +add(BookDTO) void
        +update(Long, BookDTO) void
        +delete(Long) void
        +getNewBooks() List~BookVO~
    }
    
    class BookServiceImpl {
        -BookMapper bookMapper
        +list(BookQueryDTO) PageResult~BookVO~
        +getById(Long) BookVO
        +add(BookDTO) void
        +update(Long, BookDTO) void
        +delete(Long) void
        +getNewBooks() List~BookVO~
    }
    
    class BookMapper {
        <<interface>>
        +selectPage(Page, QueryWrapper) IPage~Book~
        +selectById(Long) Book
        +insert(Book) int
        +updateById(Book) int
        +deleteById(Long) int
        +selectNewBooks() List~Book~
    }
    
    class Book {
        -Long id
        -String isbn
        -String title
        -String author
        -String publisher
        -String category
        -String description
        -String coverUrl
        -Integer totalCount
        -Integer availableCount
        -Boolean isNew
        -LocalDate publishDate
    }
    
    BookController --> BookService
    BookServiceImpl ..|> BookService
    BookServiceImpl --> BookMapper
    BookMapper --> Book
```

## 6. 时序图

### 6.1 用户注册时序图

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant UM as UserMapper
    participant DB as Database
    
    C->>AC: POST /api/auth/register
    AC->>AC: @Valid 参数校验
    AC->>AS: register(RegisterDTO)
    AS->>UM: selectByUsername(username)
    UM->>DB: SELECT * FROM user WHERE username = ?
    DB-->>UM: null
    UM-->>AS: null (用户不存在)
    AS->>AS: passwordEncoder.encode(password)
    AS->>UM: insert(User)
    UM->>DB: INSERT INTO user ...
    DB-->>UM: 1
    UM-->>AS: success
    AS-->>AC: void
    AC-->>C: Result.success("注册成功")
```

### 6.2 用户登录时序图

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthService
    participant UM as UserMapper
    participant JWT as JwtUtil
    participant DB as Database
    
    C->>AC: POST /api/auth/login
    AC->>AC: @Valid 参数校验
    AC->>AS: login(LoginDTO)
    AS->>UM: selectByUsername(username)
    UM->>DB: SELECT * FROM user WHERE username = ?
    DB-->>UM: User
    UM-->>AS: User
    AS->>AS: passwordEncoder.matches(password, user.password)
    AS->>JWT: generateToken(userId, username)
    JWT-->>AS: token
    AS-->>AC: TokenVO(token, userInfo)
    AC-->>C: Result.success(TokenVO)
```

### 6.3 图书借阅时序图

```mermaid
sequenceDiagram
    participant C as Client
    participant BC as BorrowController
    participant BS as BorrowService
    participant BM as BookMapper
    participant RM as BorrowRecordMapper
    participant DB as Database
    
    C->>BC: POST /api/borrows
    BC->>BC: 从Token获取userId
    BC->>BS: borrow(userId, bookId)
    BS->>BM: selectById(bookId)
    BM->>DB: SELECT * FROM book WHERE id = ?
    DB-->>BM: Book
    BM-->>BS: Book
    BS->>BS: 检查库存 availableCount > 0
    BS->>RM: insert(BorrowRecord)
    RM->>DB: INSERT INTO borrow_record ...
    DB-->>RM: 1
    BS->>BM: updateById(book) // availableCount - 1
    BM->>DB: UPDATE book SET available_count = ? WHERE id = ?
    DB-->>BM: 1
    BS-->>BC: void
    BC-->>C: Result.success("借阅成功")
```
