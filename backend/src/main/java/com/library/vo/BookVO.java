package com.library.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书VO
 */
@Data
public class BookVO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private String description;
    private String coverUrl;
    private Integer totalCount;
    private Integer availableCount;
    private Integer isNew;
    private LocalDate publishDate;
    private LocalDateTime createTime;
}
