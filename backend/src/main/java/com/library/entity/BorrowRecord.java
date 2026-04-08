package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录实体
 */
@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long bookId;
    
    private LocalDateTime borrowTime;
    
    /** 应还时间 */
    private LocalDateTime dueTime;
    
    /** 实际归还时间 */
    private LocalDateTime returnTime;
    
    /** 状态: 0-借阅中, 1-已归还, 2-已逾期 */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
