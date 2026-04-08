package com.library.service;

import com.library.vo.PageResult;
import com.library.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 分页查询用户
     */
    PageResult<UserVO> list(String keyword, Integer pageNum, Integer pageSize);
    
    /**
     * 修改用户状态
     */
    void updateStatus(Long id, Integer status);
}
