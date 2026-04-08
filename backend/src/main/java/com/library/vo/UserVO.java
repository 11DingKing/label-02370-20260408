package com.library.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户VO
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
}
