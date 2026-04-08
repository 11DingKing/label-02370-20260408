package com.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 借阅DTO
 */
@Data
public class BorrowDTO {
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
    
    /** 借阅天数，默认30天 */
    private Integer days = 30;
}
