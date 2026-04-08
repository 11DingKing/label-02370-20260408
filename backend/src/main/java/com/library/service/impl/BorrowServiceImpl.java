package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.dto.BorrowDTO;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.mapper.UserMapper;
import com.library.service.BorrowService;
import com.library.vo.BorrowRecordVO;
import com.library.vo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 借阅服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {
    
    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrow(Long userId, BorrowDTO dto) {
        log.info("借阅图书: userId={}, bookId={}", userId, dto.getBookId());
        
        // 检查图书
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        
        if (book.getAvailableCount() <= 0) {
            throw new BusinessException("该图书已无可借库存");
        }
        
        // 检查是否已借阅该书
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getBookId, dto.getBookId())
                .eq(BorrowRecord::getStatus, Constants.BorrowStatus.BORROWING);
        if (borrowRecordMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已借阅该图书，请先归还");
        }
        
        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(dto.getBookId());
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(LocalDateTime.now().plusDays(dto.getDays()));
        record.setStatus(Constants.BorrowStatus.BORROWING);
        
        borrowRecordMapper.insert(record);
        
        // 更新库存
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookMapper.updateById(book);
        
        log.info("借阅成功: recordId={}", record.getId());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long userId, Long recordId) {
        log.info("归还图书: userId={}, recordId={}", userId, recordId);
        
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此记录");
        }
        
        if (record.getStatus() != Constants.BorrowStatus.BORROWING) {
            throw new BusinessException("该图书已归还");
        }
        
        // 更新记录
        record.setReturnTime(LocalDateTime.now());
        record.setStatus(Constants.BorrowStatus.RETURNED);
        borrowRecordMapper.updateById(record);
        
        // 更新库存
        Book book = bookMapper.selectById(record.getBookId());
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookMapper.updateById(book);
        
        log.info("归还成功: recordId={}", recordId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReturn(Long recordId) {
        log.info("确认归还: recordId={}", recordId);
        
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        
        if (record.getStatus() != Constants.BorrowStatus.BORROWING) {
            throw new BusinessException("该图书已归还");
        }
        
        // 更新记录
        record.setReturnTime(LocalDateTime.now());
        record.setStatus(Constants.BorrowStatus.RETURNED);
        borrowRecordMapper.updateById(record);
        
        // 更新库存
        Book book = bookMapper.selectById(record.getBookId());
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookMapper.updateById(book);
        
        log.info("确认归还成功: recordId={}", recordId);
    }
    
    @Override
    public PageResult<BorrowRecordVO> getCurrentBorrows(Long userId, Integer pageNum, Integer pageSize) {
        Page<BorrowRecord> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, Constants.BorrowStatus.BORROWING)
                .orderByDesc(BorrowRecord::getBorrowTime);
        
        Page<BorrowRecord> result = borrowRecordMapper.selectPage(page, wrapper);
        
        List<BorrowRecordVO> voList = convertToVOList(result.getRecords());
        return PageResult.of(voList, result.getTotal(), pageNum, pageSize);
    }
    
    @Override
    public PageResult<BorrowRecordVO> getBorrowHistory(Long userId, Integer pageNum, Integer pageSize) {
        Page<BorrowRecord> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
                .orderByDesc(BorrowRecord::getBorrowTime);
        
        Page<BorrowRecord> result = borrowRecordMapper.selectPage(page, wrapper);
        
        List<BorrowRecordVO> voList = convertToVOList(result.getRecords());
        return PageResult.of(voList, result.getTotal(), pageNum, pageSize);
    }
    
    @Override
    public PageResult<BorrowRecordVO> getAllRecords(Integer status, Integer pageNum, Integer pageSize) {
        Page<BorrowRecord> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(BorrowRecord::getStatus, status);
        }
        wrapper.orderByDesc(BorrowRecord::getBorrowTime);
        
        Page<BorrowRecord> result = borrowRecordMapper.selectPage(page, wrapper);
        
        List<BorrowRecordVO> voList = convertToVOList(result.getRecords());
        return PageResult.of(voList, result.getTotal(), pageNum, pageSize);
    }
    
    private List<BorrowRecordVO> convertToVOList(List<BorrowRecord> records) {
        if (records.isEmpty()) {
            return List.of();
        }
        
        // 批量查询图书信息
        List<Long> bookIds = records.stream().map(BorrowRecord::getBookId).distinct().toList();
        Map<Long, Book> bookMap = bookMapper.selectBatchIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));
        
        // 批量查询用户信息
        List<Long> userIds = records.stream().map(BorrowRecord::getUserId).distinct().toList();
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        return records.stream().map(record -> {
            BorrowRecordVO vo = new BorrowRecordVO();
            vo.setId(record.getId());
            vo.setUserId(record.getUserId());
            vo.setBookId(record.getBookId());
            vo.setBorrowTime(record.getBorrowTime());
            vo.setDueTime(record.getDueTime());
            vo.setReturnTime(record.getReturnTime());
            vo.setStatus(record.getStatus());
            vo.setStatusText(Constants.BorrowStatus.getText(record.getStatus()));
            
            Book book = bookMap.get(record.getBookId());
            if (book != null) {
                vo.setBookTitle(book.getTitle());
                vo.setBookAuthor(book.getAuthor());
                vo.setBookIsbn(book.getIsbn());
            }
            
            User user = userMap.get(record.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
}
