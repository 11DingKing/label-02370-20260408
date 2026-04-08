package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String username;
    
    /** 操作描述 */
    private String operation;
    
    /** 请求方法 */
    private String method;
    
    /** 请求参数 */
    private String params;
    
    private String ip;
    
    /** 状态: 0-失败, 1-成功 */
    private Integer status;
    
    private String errorMsg;
    
    /** 耗时(ms) */
    private Long costTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
