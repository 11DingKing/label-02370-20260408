package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.BusinessException;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import com.library.vo.PageResult;
import com.library.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    
    @Override
    public PageResult<UserVO> list(String keyword, Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = userMapper.selectPage(page, wrapper);
        
        List<UserVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        
        return PageResult.of(voList, result.getTotal(), pageNum, pageSize);
    }
    
    @Override
    public void updateStatus(Long id, Integer status) {
        log.info("修改用户状态: id={}, status={}", id, status);
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
        
        log.info("修改用户状态成功: id={}", id);
    }
    
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
