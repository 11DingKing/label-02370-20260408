package com.library.controller;

import com.library.annotation.Log;
import com.library.annotation.RequireAdmin;
import com.library.service.UserService;
import com.library.vo.PageResult;
import com.library.vo.Result;
import com.library.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器（管理员）
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RequireAdmin
public class UserController {
    
    private final UserService userService;
    
    /**
     * 分页查询用户
     */
    @GetMapping
    public Result<PageResult<UserVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<UserVO> result = userService.list(keyword, pageNum, pageSize);
        return Result.success(result);
    }
    
    /**
     * 修改用户状态
     */
    @Log("修改用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("修改成功", null);
    }
}
