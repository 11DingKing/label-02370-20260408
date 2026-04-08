package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体
 */
@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String isbn;
    
    private String title;
    
    private String author;
    
    private String publisher;
    
    private String category;
    
    private String description;
    
    private String coverUrl;
    
    /** 总数量 */
    private Integer totalCount;
    
    /** 可借数量 */
    private Integer availableCount;
    
    /** 是否新书: 0-否, 1-是 */
    private Integer isNew;
    
    private LocalDate publishDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
