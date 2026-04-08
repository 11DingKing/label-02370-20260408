package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.BusinessException;
import com.library.dto.BookDTO;
import com.library.dto.BookQueryDTO;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import com.library.vo.BookVO;
import com.library.vo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    
    private final BookMapper bookMapper;
    
    @Override
    public PageResult<BookVO> list(BookQueryDTO query) {
        Page<Book> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(Book::getTitle, query.getKeyword())
                    .or()
                    .like(Book::getAuthor, query.getKeyword())
                    .or()
                    .like(Book::getIsbn, query.getKeyword())
            );
        }
        
        // 分类筛选
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(Book::getCategory, query.getCategory());
        }
        
        // 新书筛选
        if (query.getIsNew() != null) {
            wrapper.eq(Book::getIsNew, query.getIsNew());
        }
        
        wrapper.orderByDesc(Book::getCreateTime);
        
        Page<Book> result = bookMapper.selectPage(page, wrapper);
        
        List<BookVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        
        return PageResult.of(voList, result.getTotal(), query.getPageNum(), query.getPageSize());
    }
    
    @Override
    public BookVO getById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        return toVO(book);
    }
    
    @Override
    public void add(BookDTO dto) {
        log.info("新增图书: {}", dto.getTitle());
        
        // 检查ISBN是否重复
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsbn, dto.getIsbn());
        if (bookMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("ISBN已存在");
        }
        
        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        book.setAvailableCount(dto.getTotalCount());
        
        bookMapper.insert(book);
        log.info("新增图书成功: {}", dto.getTitle());
    }
    
    @Override
    public void update(Long id, BookDTO dto) {
        log.info("编辑图书: id={}", id);
        
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        // 检查ISBN是否重复（排除自身）
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsbn, dto.getIsbn()).ne(Book::getId, id);
        if (bookMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("ISBN已存在");
        }
        
        // 计算可借数量变化
        int diff = dto.getTotalCount() - book.getTotalCount();
        int newAvailable = book.getAvailableCount() + diff;
        if (newAvailable < 0) {
            throw new BusinessException("总数量不能小于已借出数量");
        }
        
        BeanUtils.copyProperties(dto, book);
        book.setId(id);
        book.setAvailableCount(newAvailable);
        
        bookMapper.updateById(book);
        log.info("编辑图书成功: id={}", id);
    }
    
    @Override
    public void delete(Long id) {
        log.info("删除图书: id={}", id);
        
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        if (!book.getAvailableCount().equals(book.getTotalCount())) {
            throw new BusinessException("该图书有未归还的借阅，无法删除");
        }
        
        bookMapper.deleteById(id);
        log.info("删除图书成功: id={}", id);
    }
    
    @Override
    public List<BookVO> getNewBooks() {
        List<Book> books = bookMapper.selectNewBooks();
        return books.stream().map(this::toVO).collect(Collectors.toList());
    }
    
    private BookVO toVO(Book book) {
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        return vo;
    }
}
