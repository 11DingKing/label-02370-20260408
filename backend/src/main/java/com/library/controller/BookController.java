package com.library.controller;

import com.library.annotation.Log;
import com.library.annotation.RequireAdmin;
import com.library.dto.BookDTO;
import com.library.dto.BookQueryDTO;
import com.library.service.BookService;
import com.library.vo.BookVO;
import com.library.vo.PageResult;
import com.library.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书控制器
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    
    /**
     * 分页查询图书
     */
    @GetMapping
    public Result<PageResult<BookVO>> list(BookQueryDTO query) {
        PageResult<BookVO> result = bookService.list(query);
        return Result.success(result);
    }
    
    /**
     * 获取图书详情
     */
    @GetMapping("/{id}")
    public Result<BookVO> getById(@PathVariable Long id) {
        BookVO book = bookService.getById(id);
        return Result.success(book);
    }
    
    /**
     * 新增图书（管理员）
     */
    @Log("新增图书")
    @RequireAdmin
    @PostMapping
    public Result<Void> add(@Valid @RequestBody BookDTO dto) {
        bookService.add(dto);
        return Result.success("新增成功", null);
    }
    
    /**
     * 编辑图书（管理员）
     */
    @Log("编辑图书")
    @RequireAdmin
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody BookDTO dto) {
        bookService.update(id, dto);
        return Result.success("编辑成功", null);
    }
    
    /**
     * 删除图书（管理员）
     */
    @Log("删除图书")
    @RequireAdmin
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.success("删除成功", null);
    }
    
    /**
     * 获取新书推荐
     */
    @GetMapping("/new")
    public Result<List<BookVO>> getNewBooks() {
        List<BookVO> books = bookService.getNewBooks();
        return Result.success(books);
    }
}
