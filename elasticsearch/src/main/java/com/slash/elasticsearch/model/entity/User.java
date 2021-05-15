package com.slash.elasticsearch.model.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @ Author     ：Jack Lee
 * @ Date       ：Created in 23:13 2021/5/15
 */
@Data
@ToString
public class User {
    private String id;
    private String name;
    private Integer age;
    private String username;
}
