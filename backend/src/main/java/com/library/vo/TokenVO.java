package com.library.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {
    private String token;
    private UserVO user;
}
