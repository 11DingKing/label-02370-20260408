package com.library.service;

import com.library.common.BusinessException;
import com.library.dto.BookDTO;
import com.library.dto.BookQueryDTO;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.vo.BookVO;
import com.library.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookMapper bookMapper;

    private Book testBook;

    @BeforeEach
    void setUp() {
        // 创建测试图书
        testBook = new Book();
        testBook.setIsbn("978-7-111-11111-1");
        testBook.setTitle("测试图书");
        testBook.setAuthor("测试作者");
        testBook.setPublisher("测试出版社");
        testBook.setCategory("计算机");
        testBook.setDescription("这是一本测试图书");
        testBook.setTotalCount(5);
        testBook.setAvailableCount(5);
        testBook.setIsNew(1);
        testBook.setPublishDate(LocalDate.now());
        testBook.setDeleted(0);
        bookMapper.insert(testBook);
    }

    @Test
    @DisplayName("分页查询图书 - 无条件")
    void testList_NoCondition() {
        BookQueryDTO query = new BookQueryDTO();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<BookVO> result = bookService.list(query);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
        assertFalse(result.getList().isEmpty());
    }

    @Test
    @DisplayName("分页查询图书 - 关键词搜索")
    void testList_WithKeyword() {
        BookQueryDTO query = new BookQueryDTO();
        query.setKeyword("测试");
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<BookVO> result = bookService.list(query);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 1);
        assertTrue(result.getList().stream()
            .anyMatch(b -> b.getTitle().contains("测试") || b.getAuthor().contains("测试")));
    }

    @Test
    @DisplayName("分页查询图书 - 分类筛选")
    void testList_WithCategory() {
        BookQueryDTO query = new BookQueryDTO();
        query.setCategory("计算机");
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<BookVO> result = bookService.list(query);

        assertNotNull(result);
        assertTrue(result.getList().stream()
            .allMatch(b -> "计算机".equals(b.getCategory())));
    }

    @Test
    @DisplayName("获取图书详情 - 成功")
    void testGetById_Success() {
        BookVO bookVO = bookService.getById(testBook.getId());

        assertNotNull(bookVO);
        assertEquals(testBook.getIsbn(), bookVO.getIsbn());
        assertEquals(testBook.getTitle(), bookVO.getTitle());
        assertEquals(testBook.getAuthor(), bookVO.getAuthor());
    }

    @Test
    @DisplayName("获取图书详情 - 图书不存在")
    void testGetById_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> bookService.getById(99999L));
        assertEquals("图书不存在", exception.getMessage());
    }

    @Test
    @DisplayName("新增图书 - 成功")
    void testAdd_Success() {
        BookDTO dto = new BookDTO();
        dto.setIsbn("978-7-111-22222-2");
        dto.setTitle("新增测试图书");
        dto.setAuthor("新增作者");
        dto.setPublisher("新增出版社");
        dto.setCategory("文学");
        dto.setTotalCount(3);
        dto.setIsNew(0);

        assertDoesNotThrow(() -> bookService.add(dto));

        // 验证新增成功
        BookQueryDTO query = new BookQueryDTO();
        query.setKeyword("新增测试图书");
        query.setPageNum(1);
        query.setPageSize(10);
        PageResult<BookVO> result = bookService.list(query);
        
        assertTrue(result.getList().stream()
            .anyMatch(b -> "978-7-111-22222-2".equals(b.getIsbn())));
    }

    @Test
    @DisplayName("新增图书 - ISBN重复")
    void testAdd_DuplicateIsbn() {
        BookDTO dto = new BookDTO();
        dto.setIsbn(testBook.getIsbn()); // 使用已存在的ISBN
        dto.setTitle("重复ISBN图书");
        dto.setAuthor("作者");
        dto.setTotalCount(1);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> bookService.add(dto));
        assertEquals("ISBN已存在", exception.getMessage());
    }

    @Test
    @DisplayName("编辑图书 - 成功")
    void testUpdate_Success() {
        BookDTO dto = new BookDTO();
        dto.setIsbn(testBook.getIsbn());
        dto.setTitle("更新后的标题");
        dto.setAuthor("更新后的作者");
        dto.setPublisher("更新后的出版社");
        dto.setCategory("文学");
        dto.setTotalCount(10);
        dto.setIsNew(0);

        assertDoesNotThrow(() -> bookService.update(testBook.getId(), dto));

        BookVO updated = bookService.getById(testBook.getId());
        assertEquals("更新后的标题", updated.getTitle());
        assertEquals("更新后的作者", updated.getAuthor());
        assertEquals(10, updated.getTotalCount());
    }

    @Test
    @DisplayName("编辑图书 - 图书不存在")
    void testUpdate_NotFound() {
        BookDTO dto = new BookDTO();
        dto.setIsbn("978-7-111-33333-3");
        dto.setTitle("不存在的图书");
        dto.setAuthor("作者");
        dto.setTotalCount(1);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> bookService.update(99999L, dto));
        assertEquals("图书不存在", exception.getMessage());
    }

    @Test
    @DisplayName("编辑图书 - 总数量小于已借出数量")
    void testUpdate_TotalCountTooSmall() {
        // 模拟已借出2本
        testBook.setAvailableCount(3);
        bookMapper.updateById(testBook);

        BookDTO dto = new BookDTO();
        dto.setIsbn(testBook.getIsbn());
        dto.setTitle(testBook.getTitle());
        dto.setAuthor(testBook.getAuthor());
        dto.setTotalCount(1); // 总数量设为1，但已借出2本

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> bookService.update(testBook.getId(), dto));
        assertEquals("总数量不能小于已借出数量", exception.getMessage());
    }

    @Test
    @DisplayName("删除图书 - 成功")
    void testDelete_Success() {
        assertDoesNotThrow(() -> bookService.delete(testBook.getId()));

        // 验证已删除（逻辑删除）
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> bookService.getById(testBook.getId()));
        assertEquals("图书不存在", exception.getMessage());
    }

    @Test
    @DisplayName("删除图书 - 有未归还借阅")
    void testDelete_HasBorrows() {
        // 模拟有借出
        testBook.setAvailableCount(3);
        bookMapper.updateById(testBook);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> bookService.delete(testBook.getId()));
        assertEquals("该图书有未归还的借阅，无法删除", exception.getMessage());
    }

    @Test
    @DisplayName("获取新书推荐")
    void testGetNewBooks() {
        List<BookVO> newBooks = bookService.getNewBooks();

        assertNotNull(newBooks);
        assertTrue(newBooks.stream().allMatch(b -> b.getIsNew() == 1));
    }
}
