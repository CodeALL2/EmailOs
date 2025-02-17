package com.org.emaildispatcher;
import com.org.emaildispatcher.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class EmaildispatcherApplicationTests {

    @Resource
    private RedisUtil redisUtil;
    @Test
    void contextLoads() {
    }

}
