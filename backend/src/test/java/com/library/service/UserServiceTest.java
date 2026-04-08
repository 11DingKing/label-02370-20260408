package com.library.service;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.dto.RegisterDTO;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.vo.PageResult;
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
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        for (int i = 1; i <= 5; i++) {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("testuser" + i);
            dto.setPassword("password123");
            dto.setEmail("testuser" + i + "@example.com");
            authService.register(dto);
        }
    }

    @Test
    @DisplayName("分页查询用户 - 无条件")
    void testList_NoCondition() {
        PageResult<UserVO> result = userService.list(null, 1, 10);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
    }

    @Test
    @DisplayName("分页查询用户 - 关键词搜索")
    void testList_WithKeyword() {
        PageResult<UserVO> result = userService.list("testuser1", 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().stream()
            .anyMatch(u -> u.getUsername().contains("testuser1") || 
                          (u.getEmail() != null && u.getEmail().contains("testuser1"))));
    }

    @Test
    @DisplayName("分页查询用户 - 分页")
    void testList_Pagination() {
        PageResult<UserVO> page1 = userService.list(null, 1, 2);
        PageResult<UserVO> page2 = userService.list(null, 2, 2);

        assertNotNull(page1);
        assertNotNull(page2);
        assertEquals(2, page1.getList().size());
        
        // 确保两页数据不同
        assertNotEquals(page1.getList().get(0).getId(), page2.getList().get(0).getId());
    }

    @Test
    @DisplayName("修改用户状态 - 禁用")
    void testUpdateStatus_Disable() {
        User user = userMapper.selectByUsername("testuser1");
        
        assertDoesNotThrow(() -> userService.updateStatus(user.getId(), Constants.UserStatus.DISABLED));

        User updatedUser = userMapper.selectById(user.getId());
        assertEquals(Constants.UserStatus.DISABLED, updatedUser.getStatus());
    }

    @Test
    @DisplayName("修改用户状态 - 启用")
    void testUpdateStatus_Enable() {
        User user = userMapper.selectByUsername("testuser1");
        // 先禁用
        user.setStatus(Constants.UserStatus.DISABLED);
        userMapper.updateById(user);

        // 再启用
        assertDoesNotThrow(() -> userService.updateStatus(user.getId(), Constants.UserStatus.ENABLED));

        User updatedUser = userMapper.selectById(user.getId());
        assertEquals(Constants.UserStatus.ENABLED, updatedUser.getStatus());
    }

    @Test
    @DisplayName("修改用户状态 - 用户不存在")
    void testUpdateStatus_UserNotFound() {
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> userService.updateStatus(99999L, Constants.UserStatus.DISABLED));
        assertEquals("用户不存在", exception.getMessage());
    }
}
