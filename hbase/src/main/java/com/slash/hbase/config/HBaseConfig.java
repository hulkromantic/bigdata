package com.slash.hbase.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


/**
 * @ Author     ：Jack Lee
 * @ Date       ：Created in 22:58 2021/5/5
 */
@Configuration
public class HBaseConfig {

    @Value("${hbase.zookeeper.quorum}")
    private String zookeeperQuorum;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String clientPort;

    @Value("${zookeeper.znode.parent}")
    private String znodeParent;

    /**
     * @return
     * @Bean
     * public HbaseTemplate hbaseTemplate() {
     * org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
     * conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
     * conf.set("hbase.zookeeper.property.clientPort", clientPort);
     * conf.set("zookeeper.znode.parent", znodeParent);
     * HbaseTemplate hbaseTemplate = new HbaseTemplate(conf);
     * return hbaseTemplate;
     * }
     */

    @Bean
    public Admin admin() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", zookeeperQuorum);
        configuration.set("hbase.zookeeper.property.clientPort", clientPort);
        configuration.set("zookeeper.znode.parent", znodeParent);
        Admin admin = null;
        try {
            Connection connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return admin;
    }
}
