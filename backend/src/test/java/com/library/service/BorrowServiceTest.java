package com.library.service;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.dto.BorrowDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.UserMapper;
import com.library.vo.BorrowRecordVO;
import com.library.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BorrowServiceTest {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("borrowtestuser");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);
        testUser = userMapper.selectByUsername("borrowtestuser");

        // 创建测试图书
        testBook = new Book();
        testBook.setIsbn("978-7-111-44444-4");
        testBook.setTitle("借阅测试图书");
        testBook.setAuthor("借阅作者");
        testBook.setPublisher("借阅出版社");
        testBook.setCategory("计算机");
        testBook.setTotalCount(3);
        testBook.setAvailableCount(3);
        testBook.setIsNew(0);
        testBook.setDeleted(0);
        bookMapper.insert(testBook);
    }

    @Test
    @DisplayName("借阅图书 - 成功")
    void testBorrow_Success() {
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        dto.setDays(30);

        assertDoesNotThrow(() -> borrowService.borrow(testUser.getId(), dto));

        // 验证库存减少
        Book updatedBook = bookMapper.selectById(testBook.getId());
        assertEquals(2, updatedBook.getAvailableCount());

        // 验证借阅记录
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        assertTrue(result.getList().stream()
            .anyMatch(r -> r.getBookId().equals(testBook.getId())));
    }

    @Test
    @DisplayName("借阅图书 - 图书不存在")
    void testBorrow_BookNotFound() {
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(99999L);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> borrowService.borrow(testUser.getId(), dto));
        assertEquals("图书不存在", exception.getMessage());
    }

    @Test
    @DisplayName("借阅图书 - 库存不足")
    void testBorrow_NoStock() {
        // 设置库存为0
        testBook.setAvailableCount(0);
        bookMapper.updateById(testBook);

        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> borrowService.borrow(testUser.getId(), dto));
        assertEquals("该图书已无可借库存", exception.getMessage());
    }

    @Test
    @DisplayName("借阅图书 - 重复借阅")
    void testBorrow_AlreadyBorrowed() {
        // 先借阅一次
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 再次借阅同一本书
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> borrowService.borrow(testUser.getId(), dto));
        assertEquals("您已借阅该图书，请先归还", exception.getMessage());
    }

    @Test
    @DisplayName("归还图书 - 成功")
    void testReturnBook_Success() {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 获取借阅记录ID
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = result.getList().get(0).getId();

        // 归还
        assertDoesNotThrow(() -> borrowService.returnBook(testUser.getId(), recordId));

        // 验证库存恢复
        Book updatedBook = bookMapper.selectById(testBook.getId());
        assertEquals(3, updatedBook.getAvailableCount());

        // 验证当前借阅为空
        PageResult<BorrowRecordVO> currentBorrows = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        assertTrue(currentBorrows.getList().isEmpty());
    }

    @Test
    @DisplayName("归还图书 - 记录不存在")
    void testReturnBook_RecordNotFound() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> borrowService.returnBook(testUser.getId(), 99999L));
        assertEquals("借阅记录不存在", exception.getMessage());
    }

    @Test
    @DisplayName("归还图书 - 无权操作")
    void testReturnBook_NoPermission() {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 获取借阅记录ID
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = result.getList().get(0).getId();

        // 创建另一个用户
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("anotheruser");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);
        User anotherUser = userMapper.selectByUsername("anotheruser");

        // 另一个用户尝试归还
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> borrowService.returnBook(anotherUser.getId(), recordId));
        assertEquals("无权操作此记录", exception.getMessage());
    }

    @Test
    @DisplayName("归还图书 - 已归还")
    void testReturnBook_AlreadyReturned() {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 获取借阅记录ID
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = result.getList().get(0).getId();

        // 归还
        borrowService.returnBook(testUser.getId(), recordId);

        // 再次归还
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> borrowService.returnBook(testUser.getId(), recordId));
        assertEquals("该图书已归还", exception.getMessage());
    }

    @Test
    @DisplayName("确认归还 - 成功")
    void testConfirmReturn_Success() {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 获取借阅记录ID
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = result.getList().get(0).getId();

        // 管理员确认归还
        assertDoesNotThrow(() -> borrowService.confirmReturn(recordId));

        // 验证库存恢复
        Book updatedBook = bookMapper.selectById(testBook.getId());
        assertEquals(3, updatedBook.getAvailableCount());
    }

    @Test
    @DisplayName("获取当前借阅列表")
    void testGetCurrentBorrows() {
        // 借阅图书
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(testBook.getTitle(), result.getList().get(0).getBookTitle());
        assertEquals(Constants.BorrowStatus.BORROWING, result.getList().get(0).getStatus());
    }

    @Test
    @DisplayName("获取借阅历史")
    void testGetBorrowHistory() {
        // 借阅并归还
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        PageResult<BorrowRecordVO> currentResult = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = currentResult.getList().get(0).getId();
        borrowService.returnBook(testUser.getId(), recordId);

        // 查询历史
        PageResult<BorrowRecordVO> historyResult = borrowService.getBorrowHistory(testUser.getId(), 1, 10);

        assertNotNull(historyResult);
        assertEquals(1, historyResult.getTotal());
        assertEquals(Constants.BorrowStatus.RETURNED, historyResult.getList().get(0).getStatus());
    }

    @Test
    @DisplayName("获取所有借阅记录 - 管理员")
    void testGetAllRecords() {
        // 借阅图书
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 查询所有记录
        PageResult<BorrowRecordVO> result = borrowService.getAllRecords(null, 1, 10);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
    }

    @Test
    @DisplayName("获取所有借阅记录 - 按状态筛选")
    void testGetAllRecords_ByStatus() {
        // 借阅图书
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 查询借阅中的记录
        PageResult<BorrowRecordVO> result = borrowService.getAllRecords(Constants.BorrowStatus.BORROWING, 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().stream()
            .allMatch(r -> r.getStatus() == Constants.BorrowStatus.BORROWING));
    }
}
