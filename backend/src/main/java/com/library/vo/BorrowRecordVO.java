package com.library.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录VO
 */
@Data
public class BorrowRecordVO {
    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private Integer status;
    private String statusText;
}
