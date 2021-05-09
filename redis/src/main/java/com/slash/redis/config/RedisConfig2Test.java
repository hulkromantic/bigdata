package com.slash.redis.config;

import com.slash.redis.domain.UserVo;
import com.slash.redis.service.RedisService;
import com.slash.redis.utils.RedisUtil2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @ Author     ：Jack Lee
 * @ Date       ：Created in 22:00 2021/5/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisConfig2Test {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private ValueOperations valueOperations;
    @Autowired
    private HashOperations hashOperations;
    @Autowired
    private ListOperations listOperations;
    @Autowired
    private SetOperations setOperations;
    @Autowired
    private ZSetOperations zSetOperations;
    @Resource
    private RedisService redisService;

    @Test
    public void testObj() {
        UserVo userVo = new UserVo();
        userVo.setName("test");
        userVo.setAddress("beijing");
        userVo.setAge(18);
        ValueOperations operations = redisTemplate.opsForValue();
        redisService.expireKey("name", 20, TimeUnit.SECONDS);
        String key = RedisUtil2.getKey(UserVo.Table, "name", userVo.getName());
        UserVo vo = (UserVo) operations.get(key);
        System.out.println(vo);
    }
}
