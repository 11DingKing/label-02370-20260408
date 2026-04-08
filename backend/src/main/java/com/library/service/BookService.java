package com.library.service;

import com.library.dto.BookDTO;
import com.library.dto.BookQueryDTO;
import com.library.vo.BookVO;
import com.library.vo.PageResult;
import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService {
    
    /**
     * 分页查询图书
     */
    PageResult<BookVO> list(BookQueryDTO query);
    
    /**
     * 获取图书详情
     */
    BookVO getById(Long id);
    
    /**
     * 新增图书
     */
    void add(BookDTO dto);
    
    /**
     * 编辑图书
     */
    void update(Long id, BookDTO dto);
    
    /**
     * 删除图书
     */
    void delete(Long id);
    
    /**
     * 获取新书推荐
     */
    List<BookVO> getNewBooks();
}
