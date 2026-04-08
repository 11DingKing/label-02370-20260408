package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.Constants;
import com.library.dto.BookDTO;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import com.library.service.AuthService;
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
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    private String userToken;
    private String adminToken;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // 创建普通用户
        RegisterDTO userDTO = new RegisterDTO();
        userDTO.setUsername("bookuser");
        userDTO.setPassword("password123");
        authService.register(userDTO);
        
        LoginDTO userLogin = new LoginDTO();
        userLogin.setUsername("bookuser");
        userLogin.setPassword("password123");
        TokenVO userTokenVO = authService.login(userLogin);
        userToken = userTokenVO.getToken();

        // 创建管理员
        RegisterDTO adminDTO = new RegisterDTO();
        adminDTO.setUsername("bookadmin");
        adminDTO.setPassword("password123");
        authService.register(adminDTO);
        
        User admin = userMapper.selectByUsername("bookadmin");
        admin.setRole(Constants.Role.ADMIN);
        userMapper.updateById(admin);
        
        LoginDTO adminLogin = new LoginDTO();
        adminLogin.setUsername("bookadmin");
        adminLogin.setPassword("password123");
        TokenVO adminTokenVO = authService.login(adminLogin);
        adminToken = adminTokenVO.getToken();

        // 创建测试图书
        testBook = new Book();
        testBook.setIsbn("978-7-111-55555-5");
        testBook.setTitle("控制器测试图书");
        testBook.setAuthor("测试作者");
        testBook.setPublisher("测试出版社");
        testBook.setCategory("计算机");
        testBook.setTotalCount(5);
        testBook.setAvailableCount(5);
        testBook.setIsNew(1);
        testBook.setDeleted(0);
        bookMapper.insert(testBook);
    }

    @Test
    @DisplayName("GET /api/books - 查询图书列表")
    void testList() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + userToken)
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").isNumber());
    }

    @Test
    @DisplayName("GET /api/books - 关键词搜索")
    void testList_WithKeyword() throws Exception {
        mockMvc.perform(get("/api/books")
                .header("Authorization", "Bearer " + userToken)
                .param("keyword", "控制器测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("GET /api/books/{id} - 获取图书详情")
    void testGetById() throws Exception {
        mockMvc.perform(get("/api/books/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("控制器测试图书"));
    }

    @Test
    @DisplayName("GET /api/books/new - 获取新书推荐")
    void testGetNewBooks() throws Exception {
        mockMvc.perform(get("/api/books/new")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /api/books - 新增图书（管理员）")
    void testAdd_Admin() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setIsbn("978-7-111-66666-6");
        dto.setTitle("新增图书");
        dto.setAuthor("新作者");
        dto.setPublisher("新出版社");
        dto.setCategory("文学");
        dto.setTotalCount(3);

        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("新增成功"));
    }

    @Test
    @DisplayName("POST /api/books - 新增图书（普通用户无权限）")
    void testAdd_User_Forbidden() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setIsbn("978-7-111-77777-7");
        dto.setTitle("新增图书");
        dto.setAuthor("新作者");
        dto.setTotalCount(3);

        mockMvc.perform(post("/api/books")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("PUT /api/books/{id} - 编辑图书（管理员）")
    void testUpdate_Admin() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setIsbn(testBook.getIsbn());
        dto.setTitle("更新后的标题");
        dto.setAuthor("更新后的作者");
        dto.setPublisher("更新后的出版社");
        dto.setCategory("文学");
        dto.setTotalCount(10);

        mockMvc.perform(put("/api/books/" + testBook.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("编辑成功"));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - 删除图书（管理员）")
    void testDelete_Admin() throws Exception {
        mockMvc.perform(delete("/api/books/" + testBook.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - 删除图书（普通用户无权限）")
    void testDelete_User_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/books/" + testBook.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }
}
