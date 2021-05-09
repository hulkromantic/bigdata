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
import java.util.Set;
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
        userVo.setName("jack");
        userVo.setAddress("beijing");
        userVo.setAge(24);
        ValueOperations operations = redisTemplate.opsForValue();
        redisService.expireKey("name", 20, TimeUnit.SECONDS);
        String key = RedisUtil2.getKey(UserVo.Table, "name", userVo.getName());
        UserVo vo = (UserVo) operations.get(key);
        System.out.println(vo);
    }

    @Test
    public void testValueOption() {
        UserVo userVo = new UserVo();
        userVo.setName("jack");
        userVo.setName("shanghai");
        userVo.setAge(20);
        valueOperations.set("testValue", userVo);
        System.out.println(valueOperations.get("testValue"));
    }

    @Test
    public void testSetOperation() throws Exception {
        UserVo userVo = new UserVo();
        userVo.setAddress("guangzhou");
        userVo.setName("jack");
        userVo.setAge(23);
        UserVo auserVo = new UserVo();
        auserVo.setAddress("beijing");
        auserVo.setName("slash");
        auserVo.setAge(25);
        setOperations.add("user:test", userVo, auserVo);
        Set<Object> result = setOperations.members("user:test");
        System.out.println(result);
    }


    @Test
    public void testHashOperation() {
        UserVo userVo = new UserVo();
        userVo.setName("jack");
        userVo.setName("shenzheng");
        userVo.setAge(20);
        hashOperations.put("hash:user", userVo.hashCode() + "", userVo);
        System.out.println(hashOperations.get("hash:user", userVo.hashCode() + ""));
    }

    @Test
    public void ListOperations() throws Exception {
        UserVo userVo = new UserVo();
        userVo.setAddress("chengdu");
        userVo.setName("jack");
        userVo.setAge(23);
        listOperations.leftPush("list:user", userVo);
        System.out.println(listOperations.leftPop("list:user"));
        // pop之后 值会消失
        System.out.println(listOperations.leftPop("list:user"));
    }
}
