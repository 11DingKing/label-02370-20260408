package com.library.dto;

import lombok.Data;

/**
 * 图书查询DTO
 */
@Data
public class BookQueryDTO {
    private String keyword;
    private String category;
    private Integer isNew;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
