package com.slash.elasticsearch.service.base;


import com.alibaba.fastjson.JSON;
import com.slash.elasticsearch.model.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
@Slf4j
public class DocumentService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Object existsDocument() {
        Object result = "";
        try {
            // 获取请求对象
            GetRequest getRequest = new GetRequest("mydlq-user", "doc", "1");
            // 获取文档信息
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            // 将json转换成对象
            if (getResponse.isExists()) {
                UserInfo userInfo = JSON.parseObject(getResponse.getSourceAsBytes(), UserInfo.class);
                log.info("用户信息:{}", userInfo);
            }
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将结果返回
            result = getResponse.getSourceAsString();
        } catch (IOException e) {
            log.error("", e);
        }
        return result;
    }

    public Object addDocument() {
        Object result = "";
        try {
            // 创建索引请求对象
            IndexRequest indexRequest = new IndexRequest("mydlq-user", "doc", "1");
            // 创建用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setName("李slash");
            userInfo.setAge(29);
            userInfo.setSalary(100.00f);
            userInfo.setAddress("北京市");
            userInfo.setRemark("来自北京市的李先生");
            userInfo.setCreateTime(new Date());
            userInfo.setBirthDate("1995-01-10");
            // 将对象转换成byte数组
            byte[] json = JSON.toJSONBytes(userInfo);
            // 设置文档内容
            indexRequest.source(json, XContentType.JSON);
            // 执行增加文档
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("创建状态：{}", response.status());
            // 根据具体业务逻辑返回不同结果，这里为了方便直接将结果返回
            result = response.getResult();
        } catch (IOException e) {
            log.error("", e);
        }
        return result;

    }
}
