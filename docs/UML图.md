# 图书管理系统 - UML 设计图

## 一、UML 类图

### 1.1 实体类图

```
┌─────────────────────────────────────┐
│              User                    │
├─────────────────────────────────────┤
│ - id: Long                          │
│ - username: String                  │
│ - password: String                  │
│ - email: String                     │
│ - phone: String                     │
│ - role: Integer (0-用户, 1-管理员)   │
│ - status: Integer (0-禁用, 1-启用)   │
│ - createTime: LocalDateTime         │
│ - updateTime: LocalDateTime         │
├─────────────────────────────────────┤
│ + getId(): Long                     │
│ + setId(id: Long): void             │
│ + getUsername(): String             │
│ + setUsername(username: String): void│
│ ...                                 │
└─────────────────────────────────────┘
                 │
                 │ 1
                 │
                 ▼ *
┌─────────────────────────────────────┐
│          BorrowRecord               │
├─────────────────────────────────────┤
│ - id: Long                          │
│ - userId: Long                      │
│ - bookId: Long                      │
│ - borrowTime: LocalDateTime         │
│ - dueTime: LocalDateTime            │
│ - returnTime: LocalDateTime         │
│ - status: Integer                   │
│   (0-借阅中, 1-已归还, 2-已逾期)     │
│ - createTime: LocalDateTime         │
│ - updateTime: LocalDateTime         │
├─────────────────────────────────────┤
│ + getId(): Long                     │
│ + getUserId(): Long                 │
│ + getBookId(): Long                 │
│ + getStatus(): Integer              │
│ ...                                 │
└─────────────────────────────────────┘
                 │
                 │ *
                 │
                 ▼ 1
┌─────────────────────────────────────┐
│              Book                    │
├─────────────────────────────────────┤
│ - id: Long                          │
│ - isbn: String                      │
│ - title: String                     │
│ - author: String                    │
│ - publisher: String                 │
│ - category: String                  │
│ - description: String               │
│ - coverUrl: String                  │
│ - totalCount: Integer               │
│ - availableCount: Integer           │
│ - isNew: Integer (0-否, 1-是)       │
│ - publishDate: LocalDate            │
│ - createTime: LocalDateTime         │
│ - updateTime: LocalDateTime         │
│ - deleted: Integer                  │
├─────────────────────────────────────┤
│ + getId(): Long                     │
│ + getTitle(): String                │
│ + getAvailableCount(): Integer      │
│ ...                                 │
└─────────────────────────────────────┘
```

### 1.2 服务层类图

```
┌─────────────────────────────────────┐
│      <<interface>>                  │
│        AuthService                  │
├─────────────────────────────────────┤
│ + register(dto: RegisterDTO): void  │
│ + login(dto: LoginDTO): TokenVO     │
│ + logout(userId: Long): void        │
│ + getUserInfo(userId: Long): UserVO │
└─────────────────────────────────────┘
                 △
                 │
                 │ implements
                 │
┌─────────────────────────────────────┐
│        AuthServiceImpl              │
├─────────────────────────────────────┤
│ - userMapper: UserMapper            │
│ - jwtUtil: JwtUtil                  │
│ - passwordEncoder: PasswordEncoder  │
├─────────────────────────────────────┤
│ + register(dto: RegisterDTO): void  │
│ + login(dto: LoginDTO): TokenVO     │
│ + logout(userId: Long): void        │
│ + getUserInfo(userId: Long): UserVO │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│      <<interface>>                  │
│        BookService                  │
├─────────────────────────────────────┤
│ + list(query: BookQueryDTO):        │
│       PageResult<BookVO>            │
│ + getById(id: Long): BookVO         │
│ + add(dto: BookDTO): void           │
│ + update(id: Long, dto: BookDTO)    │
│ + delete(id: Long): void            │
│ + getNewBooks(): List<BookVO>       │
└─────────────────────────────────────┘
                 △
                 │
                 │ implements
                 │
┌─────────────────────────────────────┐
│        BookServiceImpl              │
├─────────────────────────────────────┤
│ - bookMapper: BookMapper            │
├─────────────────────────────────────┤
│ + list(query: BookQueryDTO):        │
│       PageResult<BookVO>            │
│ + getById(id: Long): BookVO         │
│ + add(dto: BookDTO): void           │
│ + update(id: Long, dto: BookDTO)    │
│ + delete(id: Long): void            │
│ + getNewBooks(): List<BookVO>       │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│      <<interface>>                  │
│       BorrowService                 │
├─────────────────────────────────────┤
│ + borrow(userId: Long,              │
│          bookId: Long): void        │
│ + returnBook(userId: Long,          │
│              recordId: Long): void  │
│ + confirmReturn(recordId: Long)     │
│ + getCurrentBorrows(userId: Long,   │
│     query): PageResult<BorrowVO>    │
│ + getBorrowHistory(userId: Long,    │
│     query): PageResult<BorrowVO>    │
└─────────────────────────────────────┘
                 △
                 │
                 │ implements
                 │
┌─────────────────────────────────────┐
│       BorrowServiceImpl             │
├─────────────────────────────────────┤
│ - borrowRecordMapper                │
│ - bookMapper: BookMapper            │
│ - userMapper: UserMapper            │
├─────────────────────────────────────┤
│ + borrow(userId, bookId): void      │
│ + returnBook(userId, recordId)      │
│ + confirmReturn(recordId: Long)     │
│ + getCurrentBorrows(...)            │
│ + getBorrowHistory(...)             │
└─────────────────────────────────────┘
```

### 1.3 控制器层类图

```
┌─────────────────────────────────────┐
│        AuthController               │
│        @RestController              │
│     @RequestMapping("/api/auth")    │
├─────────────────────────────────────┤
│ - authService: AuthService          │
├─────────────────────────────────────┤
│ + register(dto: RegisterDTO):       │
│       Result<?>                     │
│ + login(dto: LoginDTO):             │
│       Result<TokenVO>               │
│ + logout(): Result<?>               │
│ + getUserInfo(): Result<UserVO>     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│        BookController               │
│        @RestController              │
│    @RequestMapping("/api/books")    │
├─────────────────────────────────────┤
│ - bookService: BookService          │
├─────────────────────────────────────┤
│ + list(query: BookQueryDTO):        │
│       Result<PageResult<BookVO>>    │
│ + getById(id: Long):                │
│       Result<BookVO>                │
│ + add(dto: BookDTO): Result<?>      │
│ + update(id, dto): Result<?>        │
│ + delete(id: Long): Result<?>       │
│ + getNewBooks():                    │
│       Result<List<BookVO>>          │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│       BorrowController              │
│        @RestController              │
│   @RequestMapping("/api/borrows")   │
├─────────────────────────────────────┤
│ - borrowService: BorrowService      │
├─────────────────────────────────────┤
│ + borrow(dto: BorrowDTO):           │
│       Result<?>                     │
│ + returnBook(id: Long):             │
│       Result<?>                     │
│ + confirmReturn(id: Long):          │
│       Result<?>                     │
│ + getCurrentBorrows(query):         │
│       Result<PageResult>            │
│ + getBorrowHistory(query):          │
│       Result<PageResult>            │
└─────────────────────────────────────┘
```

---

## 二、UML 时序图

### 2.1 用户注册时序图

```
┌──────┐     ┌──────────────┐     ┌─────────────┐     ┌──────────┐     ┌────────┐
│Client│     │AuthController│     │AuthService  │     │UserMapper│     │Database│
└──┬───┘     └──────┬───────┘     └──────┬──────┘     └────┬─────┘     └───┬────┘
   │                │                    │                 │               │
   │ POST /api/auth/register             │                 │               │
   │ {username, password, email}         │                 │               │
   │───────────────>│                    │                 │               │
   │                │                    │                 │               │
   │                │ @Valid 参数校验     │                 │               │
   │                │──────────┐         │                 │               │
   │                │          │         │                 │               │
   │                │<─────────┘         │                 │               │
   │                │                    │                 │               │
   │                │ register(dto)      │                 │               │
   │                │───────────────────>│                 │               │
   │                │                    │                 │               │
   │                │                    │ selectByUsername(username)      │
   │                │                    │────────────────>│               │
   │                │                    │                 │               │
   │                │                    │                 │ SELECT * FROM │
   │                │                    │                 │ user WHERE    │
   │                │                    │                 │ username = ?  │
   │                │                    │                 │──────────────>│
   │                │                    │                 │               │
   │                │                    │                 │     null      │
   │                │                    │                 │<──────────────│
   │                │                    │                 │               │
   │                │                    │      null       │               │
   │                │                    │<────────────────│               │
   │                │                    │                 │               │
   │                │                    │ passwordEncoder.encode(password)│
   │                │                    │──────────┐      │               │
   │                │                    │          │      │               │
   │                │                    │<─────────┘      │               │
   │                │                    │                 │               │
   │                │                    │ insert(user)    │               │
   │                │                    │────────────────>│               │
   │                │                    │                 │               │
   │                │                    │                 │ INSERT INTO   │
   │                │                    │                 │ user ...      │
   │                │                    │                 │──────────────>│
   │                │                    │                 │               │
   │                │                    │                 │      1        │
   │                │                    │                 │<──────────────│
   │                │                    │                 │               │
   │                │                    │    success      │               │
   │                │                    │<────────────────│               │
   │                │                    │                 │               │
   │                │      void          │                 │               │
   │                │<───────────────────│                 │               │
   │                │                    │                 │               │
   │ Result.success("注册成功")          │                 │               │
   │<───────────────│                    │                 │               │
   │                │                    │                 │               │
```

### 2.2 用户登录时序图

```
┌──────┐     ┌──────────────┐     ┌─────────────┐     ┌──────────┐     ┌───────┐
│Client│     │AuthController│     │AuthService  │     │UserMapper│     │JwtUtil│
└──┬───┘     └──────┬───────┘     └──────┬──────┘     └────┬─────┘     └───┬───┘
   │                │                    │                 │               │
   │ POST /api/auth/login                │                 │               │
   │ {username, password}                │                 │               │
   │───────────────>│                    │                 │               │
   │                │                    │                 │               │
   │                │ login(dto)         │                 │               │
   │                │───────────────────>│                 │               │
   │                │                    │                 │               │
   │                │                    │ selectByUsername(username)      │
   │                │                    │────────────────>│               │
   │                │                    │                 │               │
   │                │                    │      User       │               │
   │                │                    │<────────────────│               │
   │                │                    │                 │               │
   │                │                    │ passwordEncoder.matches()       │
   │                │                    │──────────┐      │               │
   │                │                    │          │      │               │
   │                │                    │<─────────┘ true │               │
   │                │                    │                 │               │
   │                │                    │ generateToken(userId, username) │
   │                │                    │────────────────────────────────>│
   │                │                    │                 │               │
   │                │                    │              token               │
   │                │                    │<────────────────────────────────│
   │                │                    │                 │               │
   │                │    TokenVO         │                 │               │
   │                │    {token, user}   │                 │               │
   │                │<───────────────────│                 │               │
   │                │                    │                 │               │
   │ Result.success(TokenVO)             │                 │               │
   │<───────────────│                    │                 │               │
   │                │                    │                 │               │
```

### 2.3 图书借阅时序图

```
┌──────┐     ┌────────────────┐     ┌──────────────┐     ┌──────────┐     ┌──────────────────┐
│Client│     │BorrowController│     │BorrowService │     │BookMapper│     │BorrowRecordMapper│
└──┬───┘     └───────┬────────┘     └──────┬───────┘     └────┬─────┘     └────────┬─────────┘
   │                 │                     │                  │                    │
   │ POST /api/borrows                     │                  │                    │
   │ {bookId: 1}                           │                  │                    │
   │────────────────>│                     │                  │                    │
   │                 │                     │                  │                    │
   │                 │ 从Token获取userId    │                  │                    │
   │                 │──────────┐          │                  │                    │
   │                 │          │          │                  │                    │
   │                 │<─────────┘          │                  │                    │
   │                 │                     │                  │                    │
   │                 │ borrow(userId, bookId)                 │                    │
   │                 │────────────────────>│                  │                    │
   │                 │                     │                  │                    │
   │                 │                     │ selectById(bookId)                    │
   │                 │                     │─────────────────>│                    │
   │                 │                     │                  │                    │
   │                 │                     │      Book        │                    │
   │                 │                     │<─────────────────│                    │
   │                 │                     │                  │                    │
   │                 │                     │ 检查库存 availableCount > 0           │
   │                 │                     │──────────┐       │                    │
   │                 │                     │          │       │                    │
   │                 │                     │<─────────┘       │                    │
   │                 │                     │                  │                    │
   │                 │                     │ insert(BorrowRecord)                  │
   │                 │                     │─────────────────────────────────────>│
   │                 │                     │                  │                    │
   │                 │                     │                  │         1          │
   │                 │                     │<─────────────────────────────────────│
   │                 │                     │                  │                    │
   │                 │                     │ updateById(book) │                    │
   │                 │                     │ availableCount-1 │                    │
   │                 │                     │─────────────────>│                    │
   │                 │                     │                  │                    │
   │                 │                     │        1         │                    │
   │                 │                     │<─────────────────│                    │
   │                 │                     │                  │                    │
   │                 │       void          │                  │                    │
   │                 │<────────────────────│                  │                    │
   │                 │                     │                  │                    │
   │ Result.success("借阅成功")            │                  │                    │
   │<────────────────│                     │                  │                    │
   │                 │                     │                  │                    │
```

### 2.4 图书归还时序图

```
┌──────┐     ┌────────────────┐     ┌──────────────┐     ┌──────────┐     ┌──────────────────┐
│Client│     │BorrowController│     │BorrowService │     │BookMapper│     │BorrowRecordMapper│
└──┬───┘     └───────┬────────┘     └──────┬───────┘     └────┬─────┘     └────────┬─────────┘
   │                 │                     │                  │                    │
   │ PUT /api/borrows/{id}/return          │                  │                    │
   │────────────────>│                     │                  │                    │
   │                 │                     │                  │                    │
   │                 │ returnBook(userId, recordId)           │                    │
   │                 │────────────────────>│                  │                    │
   │                 │                     │                  │                    │
   │                 │                     │ selectById(recordId)                  │
   │                 │                     │─────────────────────────────────────>│
   │                 │                     │                  │                    │
   │                 │                     │           BorrowRecord                │
   │                 │                     │<─────────────────────────────────────│
   │                 │                     │                  │                    │
   │                 │                     │ 验证借阅记录属于当前用户              │
   │                 │                     │──────────┐       │                    │
   │                 │                     │          │       │                    │
   │                 │                     │<─────────┘       │                    │
   │                 │                     │                  │                    │
   │                 │                     │ updateById(record)                    │
   │                 │                     │ status=1, returnTime=now              │
   │                 │                     │─────────────────────────────────────>│
   │                 │                     │                  │                    │
   │                 │                     │         1        │                    │
   │                 │                     │<─────────────────────────────────────│
   │                 │                     │                  │                    │
   │                 │                     │ selectById(bookId)                    │
   │                 │                     │─────────────────>│                    │
   │                 │                     │                  │                    │
   │                 │                     │      Book        │                    │
   │                 │                     │<─────────────────│                    │
   │                 │                     │                  │                    │
   │                 │                     │ updateById(book) │                    │
   │                 │                     │ availableCount+1 │                    │
   │                 │                     │─────────────────>│                    │
   │                 │                     │                  │                    │
   │                 │                     │        1         │                    │
   │                 │                     │<─────────────────│                    │
   │                 │                     │                  │                    │
   │                 │       void          │                  │                    │
   │                 │<────────────────────│                  │                    │
   │                 │                     │                  │                    │
   │ Result.success("归还成功")            │                  │                    │
   │<────────────────│                     │                  │                    │
   │                 │                     │                  │                    │
```

### 2.5 JWT 认证拦截时序图

```
┌──────┐     ┌──────────────┐     ┌───────┐     ┌──────────────┐
│Client│     │JwtInterceptor│     │JwtUtil│     │ Controller   │
└──┬───┘     └──────┬───────┘     └───┬───┘     └──────┬───────┘
   │                │                 │                │
   │ GET /api/books │                 │                │
   │ Header: Authorization: Bearer xxx│                │
   │───────────────>│                 │                │
   │                │                 │                │
   │                │ preHandle()     │                │
   │                │──────────┐      │                │
   │                │          │      │                │
   │                │<─────────┘      │                │
   │                │                 │                │
   │                │ 提取Token       │                │
   │                │──────────┐      │                │
   │                │          │      │                │
   │                │<─────────┘      │                │
   │                │                 │                │
   │                │ parseToken(token)                │
   │                │────────────────>│                │
   │                │                 │                │
   │                │     Claims      │                │
   │                │     {userId,    │                │
   │                │      username}  │                │
   │                │<────────────────│                │
   │                │                 │                │
   │                │ 存入ThreadLocal │                │
   │                │──────────┐      │                │
   │                │          │      │                │
   │                │<─────────┘      │                │
   │                │                 │                │
   │                │ return true (放行)               │
   │                │─────────────────────────────────>│
   │                │                 │                │
   │                │                 │    处理请求    │
   │                │                 │       │        │
   │                │                 │       ▼        │
   │                │                 │    返回结果    │
   │                │                 │                │
   │                │ afterCompletion()                │
   │                │<─────────────────────────────────│
   │                │                 │                │
   │                │ 清理ThreadLocal │                │
   │                │──────────┐      │                │
   │                │          │      │                │
   │                │<─────────┘      │                │
   │                │                 │                │
   │     Result     │                 │                │
   │<───────────────│                 │                │
   │                │                 │                │
```

---

## 三、类关系说明

### 3.1 实体关系
- **User** 与 **BorrowRecord**：一对多关系，一个用户可以有多条借阅记录
- **Book** 与 **BorrowRecord**：一对多关系，一本书可以被多次借阅

### 3.2 分层架构
```
┌─────────────────────────────────────────────────────────┐
│                    Controller 层                         │
│   AuthController, BookController, BorrowController      │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                     Service 层                           │
│   AuthService, BookService, BorrowService, UserService  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                     Mapper 层                            │
│   UserMapper, BookMapper, BorrowRecordMapper            │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    Database                              │
│              MySQL (library_db)                          │
└─────────────────────────────────────────────────────────┘
```

### 3.3 核心设计模式
1. **MVC 模式**：Controller-Service-Mapper 三层架构
2. **依赖注入**：通过 @Autowired / @RequiredArgsConstructor 注入依赖
3. **接口隔离**：Service 层定义接口，Impl 类实现具体逻辑
4. **AOP 切面**：日志记录、权限校验通过切面实现
