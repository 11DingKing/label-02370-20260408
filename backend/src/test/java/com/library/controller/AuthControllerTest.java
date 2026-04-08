package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.service.AuthService;
import com.library.vo.TokenVO;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/register - 注册成功")
    void testRegister_Success() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("newuser@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    @DisplayName("POST /api/auth/register - 参数校验失败")
    void testRegister_ValidationFailed() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("ab"); // 太短
        dto.setPassword("123"); // 太短

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("POST /api/auth/login - 登录成功")
    void testLogin_Success() throws Exception {
        // 先注册
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("logintest");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);

        // 登录
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("logintest");
        loginDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.user.username").value("logintest"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 用户名或密码错误")
    void testLogin_Failed() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    @DisplayName("GET /api/auth/info - 获取用户信息")
    void testGetUserInfo_Success() throws Exception {
        // 先注册并登录
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("infotest");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("infotest");
        loginDTO.setPassword("password123");
        TokenVO tokenVO = authService.login(loginDTO);

        mockMvc.perform(get("/api/auth/info")
                .header("Authorization", "Bearer " + tokenVO.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("infotest"));
    }

    @Test
    @DisplayName("GET /api/auth/info - 未登录")
    void testGetUserInfo_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @DisplayName("POST /api/auth/logout - 退出登录")
    void testLogout_Success() throws Exception {
        // 先注册并登录
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("logouttest");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("logouttest");
        loginDTO.setPassword("password123");
        TokenVO tokenVO = authService.login(loginDTO);

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + tokenVO.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("退出成功"));
    }
}
