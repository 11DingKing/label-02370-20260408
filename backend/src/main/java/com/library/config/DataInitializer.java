package com.library.config;

import com.library.common.Constants;
import com.library.entity.Book;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import com.library.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 数据初始化器 - 创建默认账号和示例图书
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        initUsers();
        initBooks();
    }
    
    private void initUsers() {
        // 创建管理员账号
        if (userMapper.selectByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@library.com");
            admin.setRole(Constants.Role.ADMIN);
            admin.setStatus(Constants.UserStatus.ENABLED);
            userMapper.insert(admin);
            log.info("创建管理员账号: admin / admin123");
        }
        
        // 创建普通用户账号
        if (userMapper.selectByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@library.com");
            user.setRole(Constants.Role.USER);
            user.setStatus(Constants.UserStatus.ENABLED);
            userMapper.insert(user);
            log.info("创建普通用户账号: user / user123");
        }
    }
    
    private void initBooks() {
        // 检查是否已有图书数据
        Long count = bookMapper.selectCount(null);
        if (count > 0) {
            log.info("图书数据已存在，跳过初始化");
            return;
        }
        
        log.info("开始初始化图书数据...");
        
        // 文学类
        createBook("978-7-02-008179-6", "红楼梦", "曹雪芹", "人民文学出版社", "文学", 
            "中国古典四大名著之一，以贾宝玉、林黛玉、薛宝钗的爱情婚姻悲剧为主线，描绘了封建社会末期的社会生活画卷。", 
            10, 1, LocalDate.of(2008, 7, 1));
        
        createBook("978-7-02-008180-2", "西游记", "吴承恩", "人民文学出版社", "文学",
            "中国古典四大名著之一，讲述唐僧师徒四人西天取经的神话故事，充满奇幻色彩。",
            8, 1, LocalDate.of(2010, 5, 1));
        
        createBook("978-7-02-008181-9", "三国演义", "罗贯中", "人民文学出版社", "文学",
            "中国古典四大名著之一，描写了东汉末年到西晋初年之间近百年的历史风云。",
            6, 0, LocalDate.of(2006, 3, 1));
        
        createBook("978-7-02-008182-6", "水浒传", "施耐庵", "人民文学出版社", "文学",
            "中国古典四大名著之一，描写了北宋末年以宋江为首的108位好汉在梁山起义的故事。",
            5, 0, LocalDate.of(2005, 8, 1));
        
        createBook("978-7-5442-5678-9", "活着", "余华", "作家出版社", "文学",
            "讲述了农村人福贵悲惨的人生遭遇，是一部充满血泪的小说。",
            12, 1, LocalDate.of(2012, 8, 1));
        
        createBook("978-7-5302-1234-5", "围城", "钱钟书", "人民文学出版社", "文学",
            "中国现代文学经典，以幽默讽刺的笔调描写了抗战时期知识分子的生活。",
            7, 0, LocalDate.of(1991, 2, 1));
        
        // 计算机类
        createBook("978-7-111-42036-8", "Java编程思想", "Bruce Eckel", "机械工业出版社", "计算机",
            "Java领域的经典著作，全面深入地讲解了Java语言的核心概念和编程技术。",
            15, 1, LocalDate.of(2007, 6, 1));
        
        createBook("978-7-115-29Mo0-3", "深入理解计算机系统", "Randal E.Bryant", "机械工业出版社", "计算机",
            "从程序员的角度讲解计算机系统，被誉为计算机科学的经典教材。",
            8, 1, LocalDate.of(2016, 11, 1));
        
        createBook("978-7-115-41Mo5-8", "算法导论", "Thomas H.Cormen", "机械工业出版社", "计算机",
            "算法领域的权威著作，全面介绍了算法的设计与分析方法。",
            6, 0, LocalDate.of(2013, 1, 1));
        
        createBook("978-7-121-31234-5", "Spring实战", "Craig Walls", "人民邮电出版社", "计算机",
            "Spring框架的实战指南，涵盖Spring核心、Spring MVC、Spring Boot等内容。",
            10, 1, LocalDate.of(2020, 3, 1));
        
        createBook("978-7-121-35678-9", "Vue.js设计与实现", "霍春阳", "人民邮电出版社", "计算机",
            "深入讲解Vue.js 3的设计原理和实现细节，适合前端开发者进阶学习。",
            9, 1, LocalDate.of(2022, 1, 1));
        
        // 历史类
        createBook("978-7-101-05678-3", "史记", "司马迁", "中华书局", "历史",
            "中国第一部纪传体通史，被誉为史家之绝唱，无韵之离骚。",
            5, 0, LocalDate.of(1982, 11, 1));
        
        createBook("978-7-108-04567-8", "万历十五年", "黄仁宇", "生活·读书·新知三联书店", "历史",
            "以1587年为切入点，分析明朝中后期的政治、经济和社会状况。",
            8, 1, LocalDate.of(1997, 5, 1));
        
        createBook("978-7-5086-5678-4", "人类简史", "尤瓦尔·赫拉利", "中信出版社", "历史",
            "从认知革命、农业革命到科学革命，讲述人类发展的宏大历史。",
            12, 1, LocalDate.of(2014, 11, 1));
        
        // 经济管理类
        createBook("978-7-111-56789-0", "经济学原理", "曼昆", "北京大学出版社", "经济",
            "经济学入门经典教材，以通俗易懂的方式讲解经济学基本原理。",
            10, 0, LocalDate.of(2015, 5, 1));
        
        createBook("978-7-5086-4567-8", "从零到一", "彼得·蒂尔", "中信出版社", "经济",
            "PayPal创始人分享创业心得，探讨如何创造独特价值。",
            7, 1, LocalDate.of(2015, 1, 1));
        
        // 科普类
        createBook("978-7-5357-8901-2", "时间简史", "史蒂芬·霍金", "湖南科学技术出版社", "科普",
            "探索宇宙起源和命运的科普经典，让普通读者也能理解宇宙奥秘。",
            9, 1, LocalDate.of(2010, 4, 1));
        
        createBook("978-7-5086-6789-5", "三体", "刘慈欣", "重庆出版社", "科幻",
            "中国科幻文学的里程碑之作，获得雨果奖最佳长篇小说奖。",
            15, 1, LocalDate.of(2008, 1, 1));
        
        // 心理学类
        createBook("978-7-5086-3456-7", "思考，快与慢", "丹尼尔·卡尼曼", "中信出版社", "心理学",
            "诺贝尔经济学奖得主的代表作，揭示人类思维的两种模式。",
            8, 1, LocalDate.of(2012, 7, 1));
        
        createBook("978-7-300-12345-6", "社会心理学", "戴维·迈尔斯", "人民邮电出版社", "心理学",
            "社会心理学领域的经典教材，内容全面、案例丰富。",
            6, 0, LocalDate.of(2016, 1, 1));
        
        log.info("图书数据初始化完成，共创建 {} 本图书", 20);
    }
    
    private void createBook(String isbn, String title, String author, String publisher, 
                           String category, String description, int totalCount, 
                           int isNew, LocalDate publishDate) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);
        book.setDescription(description);
        book.setTotalCount(totalCount);
        book.setAvailableCount(totalCount);
        book.setIsNew(isNew);
        book.setPublishDate(publishDate);
        bookMapper.insert(book);
    }
}
