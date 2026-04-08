package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 图书Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
    
    @Select("SELECT * FROM book WHERE is_new = 1 AND deleted = 0 ORDER BY create_time DESC LIMIT 10")
    List<Book> selectNewBooks();
}
