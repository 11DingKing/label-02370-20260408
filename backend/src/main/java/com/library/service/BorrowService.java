package com.library.service;

import com.library.dto.BorrowDTO;
import com.library.vo.BorrowRecordVO;
import com.library.vo.PageResult;

/**
 * 借阅服务接口
 */
public interface BorrowService {
    
    /**
     * 借阅图书
     */
    void borrow(Long userId, BorrowDTO dto);
    
    /**
     * 归还图书
     */
    void returnBook(Long userId, Long recordId);
    
    /**
     * 确认归还
     */
    void confirmReturn(Long recordId);
    
    /**
     * 获取当前借阅列表
     */
    PageResult<BorrowRecordVO> getCurrentBorrows(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取借阅历史
     */
    PageResult<BorrowRecordVO> getBorrowHistory(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取所有借阅记录（管理员）
     */
    PageResult<BorrowRecordVO> getAllRecords(Integer status, Integer pageNum, Integer pageSize);
}
