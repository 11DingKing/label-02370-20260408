package com.library.controller;

import com.library.annotation.Log;
import com.library.annotation.RequireAdmin;
import com.library.dto.BorrowDTO;
import com.library.service.BorrowService;
import com.library.vo.BorrowRecordVO;
import com.library.vo.PageResult;
import com.library.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅控制器
 */
@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {
    
    private final BorrowService borrowService;
    
    /**
     * 借阅图书
     */
    @Log("借阅图书")
    @PostMapping
    public Result<Void> borrow(HttpServletRequest request, @Valid @RequestBody BorrowDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        borrowService.borrow(userId, dto);
        return Result.success("借阅成功", null);
    }
    
    /**
     * 归还图书
     */
    @Log("归还图书")
    @PutMapping("/{id}/return")
    public Result<Void> returnBook(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        borrowService.returnBook(userId, id);
        return Result.success("归还成功", null);
    }
    
    /**
     * 确认归还（管理员）
     */
    @Log("确认归还")
    @RequireAdmin
    @PutMapping("/{id}/confirm")
    public Result<Void> confirmReturn(@PathVariable Long id) {
        borrowService.confirmReturn(id);
        return Result.success("确认成功", null);
    }
    
    /**
     * 获取当前借阅列表
     */
    @GetMapping("/current")
    public Result<PageResult<BorrowRecordVO>> getCurrentBorrows(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<BorrowRecordVO> result = borrowService.getCurrentBorrows(userId, pageNum, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取借阅历史
     */
    @GetMapping("/history")
    public Result<PageResult<BorrowRecordVO>> getBorrowHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<BorrowRecordVO> result = borrowService.getBorrowHistory(userId, pageNum, pageSize);
        return Result.success(result);
    }
    
    /**
     * 获取所有借阅记录（管理员）
     */
    @RequireAdmin
    @GetMapping("/all")
    public Result<PageResult<BorrowRecordVO>> getAllRecords(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<BorrowRecordVO> result = borrowService.getAllRecords(status, pageNum, pageSize);
        return Result.success(result);
    }
}
