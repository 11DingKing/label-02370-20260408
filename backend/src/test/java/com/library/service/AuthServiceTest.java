package com.library.service;

import com.library.common.BusinessException;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.vo.TokenVO;
import com.library.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        // 清理测试数据
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void testRegister_Success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");
        dto.setPhone("13800138000");

        assertDoesNotThrow(() -> authService.register(dto));

        User user = userMapper.selectByUsername("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(0, user.getRole()); // 普通用户
        assertEquals(1, user.getStatus()); // 启用状态
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void testRegister_UsernameExists() {
        // 先注册一个用户
        RegisterDTO dto1 = new RegisterDTO();
        dto1.setUsername("existuser");
        dto1.setPassword("password123");
        authService.register(dto1);

        // 再次注册相同用户名
        RegisterDTO dto2 = new RegisterDTO();
        dto2.setUsername("existuser");
        dto2.setPassword("password456");

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.register(dto2));
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void testLogin_Success() {
        // 先注册
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("loginuser");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);

        // 登录
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("loginuser");
        loginDTO.setPassword("password123");

        TokenVO tokenVO = authService.login(loginDTO);

        assertNotNull(tokenVO);
        assertNotNull(tokenVO.getToken());
        assertNotNull(tokenVO.getUser());
        assertEquals("loginuser", tokenVO.getUser().getUsername());
    }

    @Test
    @DisplayName("用户登录 - 用户名不存在")
    void testLogin_UserNotFound() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("password123");

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginDTO));
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLogin_WrongPassword() {
        // 先注册
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("wrongpwduser");
        registerDTO.setPassword("correctpassword");
        authService.register(registerDTO);

        // 使用错误密码登录
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("wrongpwduser");
        loginDTO.setPassword("wrongpassword");

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginDTO));
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("用户登录 - 账号被禁用")
    void testLogin_AccountDisabled() {
        // 先注册
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("disableduser");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);

        // 禁用账号
        User user = userMapper.selectByUsername("disableduser");
        user.setStatus(0);
        userMapper.updateById(user);

        // 尝试登录
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("disableduser");
        loginDTO.setPassword("password123");

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.login(loginDTO));
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("获取用户信息 - 成功")
    void testGetUserInfo_Success() {
        // 先注册
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("infouser");
        registerDTO.setPassword("password123");
        registerDTO.setEmail("info@example.com");
        authService.register(registerDTO);

        User user = userMapper.selectByUsername("infouser");
        UserVO userVO = authService.getUserInfo(user.getId());

        assertNotNull(userVO);
        assertEquals("infouser", userVO.getUsername());
        assertEquals("info@example.com", userVO.getEmail());
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void testGetUserInfo_UserNotFound() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> authService.getUserInfo(99999L));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    @DisplayName("用户退出 - 成功")
    void testLogout_Success() {
        // 先注册
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("logoutuser");
        registerDTO.setPassword("password123");
        authService.register(registerDTO);

        User user = userMapper.selectByUsername("logoutuser");
        
        // 退出不应抛出异常
        assertDoesNotThrow(() -> authService.logout(user.getId()));
    }
}
