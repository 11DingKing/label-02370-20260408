package com.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * 图书DTO
 */
@Data
public class BookDTO {
    @NotBlank(message = "ISBN不能为空")
    private String isbn;
    
    @NotBlank(message = "书名不能为空")
    private String title;
    
    @NotBlank(message = "作者不能为空")
    private String author;
    
    private String publisher;
    
    private String category;
    
    private String description;
    
    private String coverUrl;
    
    @NotNull(message = "总数量不能为空")
    @Min(value = 1, message = "总数量至少为1")
    private Integer totalCount;
    
    private Integer isNew;
    
    private LocalDate publishDate;
}
