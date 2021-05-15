package com.slash.elasticsearch.model.request;

import lombok.Data;
import lombok.ToString;

/**
 * @ Author     ：Jack Lee
 * @ Date       ：Created in 23:40 2021/5/15
 */
@Data
@ToString
public class MatchQueryMultiple {
    private String value;
    private String[] keys;
}
