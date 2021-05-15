package com.slash.elasticsearch.model.request;

import lombok.Data;
import lombok.ToString;

/**
 * @ Author     ：Jack Lee
 * @ Date       ：Created in 23:41 2021/5/15
 */
@Data
@ToString
public class Query {
    private String key;
    private String value;
}
