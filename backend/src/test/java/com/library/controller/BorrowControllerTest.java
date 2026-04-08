package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.Constants;
import com.library.dto.BorrowDTO;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import com.library.service.AuthService;
import com.library.service.BorrowService;
import com.library.vo.BorrowRecordVO;
import com.library.vo.PageResult;
import com.library.vo.TokenVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    private String userToken;
    private String adminToken;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // 创建普通用户
        RegisterDTO userDTO = new RegisterDTO();
        userDTO.setUsername("borrowuser");
        userDTO.setPassword("password123");
        authService.register(userDTO);
        testUser = userMapper.selectByUsername("borrowuser");
        
        LoginDTO userLogin = new LoginDTO();
        userLogin.setUsername("borrowuser");
        userLogin.setPassword("password123");
        TokenVO userTokenVO = authService.login(userLogin);
        userToken = userTokenVO.getToken();

        // 创建管理员
        RegisterDTO adminDTO = new RegisterDTO();
        adminDTO.setUsername("borrowadmin");
        adminDTO.setPassword("password123");
        authService.register(adminDTO);
        
        User admin = userMapper.selectByUsername("borrowadmin");
        admin.setRole(Constants.Role.ADMIN);
        userMapper.updateById(admin);
        
        LoginDTO adminLogin = new LoginDTO();
        adminLogin.setUsername("borrowadmin");
        adminLogin.setPassword("password123");
        TokenVO adminTokenVO = authService.login(adminLogin);
        adminToken = adminTokenVO.getToken();

        // 创建测试图书
        testBook = new Book();
        testBook.setIsbn("978-7-111-88888-8");
        testBook.setTitle("借阅控制器测试图书");
        testBook.setAuthor("测试作者");
        testBook.setPublisher("测试出版社");
        testBook.setCategory("计算机");
        testBook.setTotalCount(5);
        testBook.setAvailableCount(5);
        testBook.setIsNew(0);
        testBook.setDeleted(0);
        bookMapper.insert(testBook);
    }

    @Test
    @DisplayName("POST /api/borrows - 借阅图书")
    void testBorrow() throws Exception {
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        dto.setDays(30);

        mockMvc.perform(post("/api/borrows")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("借阅成功"));
    }

    @Test
    @DisplayName("POST /api/borrows - 借阅图书（库存不足）")
    void testBorrow_NoStock() throws Exception {
        testBook.setAvailableCount(0);
        bookMapper.updateById(testBook);

        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());

        mockMvc.perform(post("/api/borrows")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("该图书已无可借库存"));
    }

    @Test
    @DisplayName("PUT /api/borrows/{id}/return - 归还图书")
    void testReturnBook() throws Exception {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 获取借阅记录ID
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = result.getList().get(0).getId();

        mockMvc.perform(put("/api/borrows/" + recordId + "/return")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("归还成功"));
    }

    @Test
    @DisplayName("PUT /api/borrows/{id}/confirm - 确认归还（管理员）")
    void testConfirmReturn_Admin() throws Exception {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 获取借阅记录ID
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = result.getList().get(0).getId();

        mockMvc.perform(put("/api/borrows/" + recordId + "/confirm")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("确认成功"));
    }

    @Test
    @DisplayName("PUT /api/borrows/{id}/confirm - 确认归还（普通用户无权限）")
    void testConfirmReturn_User_Forbidden() throws Exception {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        // 获取借阅记录ID
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(testUser.getId(), 1, 10);
        Long recordId = result.getList().get(0).getId();

        mockMvc.perform(put("/api/borrows/" + recordId + "/confirm")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("GET /api/borrows/current - 获取当前借阅")
    void testGetCurrentBorrows() throws Exception {
        // 先借阅
        BorrowDTO dto = new BorrowDTO();
        dto.setBookId(testBook.getId());
        borrowService.borrow(testUser.getId(), dto);

        mockMvc.perform(get("/api/borrows/current")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("GET /api/borrows/history - 获取借阅历史")
    void testGetBorrowHistory() throws Exception {
        mockMvc.perform(get("/api/borrows/history")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @DisplayName("GET /api/borrows/all - 获取所有借阅记录（管理员）")
    void testGetAllRecords_Admin() throws Exception {
        mockMvc.perform(get("/api/borrows/all")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @DisplayName("GET /api/borrows/all - 获取所有借阅记录（普通用户无权限）")
    void testGetAllRecords_User_Forbidden() throws Exception {
        mockMvc.perform(get("/api/borrows/all")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }
}
