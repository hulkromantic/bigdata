package com.slash.hbase.service;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

/**
 * @ Author     ：Jack Lee
 * @ Date       ：Created in 23:40 2021/5/5
 */
@Service
public class HBaseUtils {

    @Autowired
    private Admin admin;

    /**
     * 表是否存在
     *
     * @param tableName
     * @return
     */
    public boolean isExists(String tableName) {
        boolean tableExists = false;
        TableName table = TableName.valueOf(tableName);
        try {
            tableExists = admin.tableExists(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableExists;
    }

    /**
     * 创建表
     *
     * @param tableName
     * @param columnFamily
     * @param keys
     * @return
     */
    public boolean createTable(String tableName, List<String> columnFamily, List<String> keys) {
        if (!isExists(tableName)) {
            try {
                TableName table = TableName.valueOf(tableName);
                HTableDescriptor desc = new HTableDescriptor(table);
                for (String cf : columnFamily) {
                    desc.addFamily(new HColumnDescriptor(cf));
                }
                if (keys == null) {
                    admin.createTable(desc);
                } else {
                    byte[][] splitKeys = getSplitKeys(keys);
                    admin.createTable(desc, splitKeys);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(tableName + " is exists!");
            return false;
        }
        return false;
    }

    /**
     * 删除表
     *
     * @param tableName
     */
    public void dropTable(String tableName) {
        if (isExists(tableName)) {
            try {
                TableName table = TableName.valueOf(tableName);
                admin.disableTable(table);
                admin.deleteTable(table);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 插入数据（单条）
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param column
     * @param value
     * @return
     */
    public boolean putSingleData(String tableName, String rowKey, String columnFamily, String column, String value) {
        return putBatchData(tableName, rowKey, columnFamily, Arrays.asList(column), Arrays.asList(value));
    }

    /**
     * 插入数据（批量）
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columns
     * @param values
     * @return
     */
    public boolean putBatchData(String tableName, String rowKey, String columnFamily, List<String> columns, List<String> values) {
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            for (int i = 0; i < columns.size(); i++) {
                put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columns.get(i)), Bytes.toBytes(values.get(i)));
            }
            table.put(put);
            table.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取全表数据
     *
     * @param tableName
     * @return
     */
    public List<Map<String, String>> getAllData(String tableName) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            ResultScanner scanner = table.getScanner(scan);
            for (Result result : scanner) {
                HashMap<String, String> map = new HashMap<>();
                //rowKey
                String row = Bytes.toString(result.getRow());
                map.put("row", row);
                for (Cell cell : result.listCells()) {
                    //列族
                    String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),
                            cell.getFamilyLength());
                    //列名
                    String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                            cell.getQualifierLength());
                    //列值
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
                            cell.getValueLength());
                    map.put(family + ":" + qualifier, value);
                }
                list.add(map);
            }

            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 根据rowkey获取数据
     *
     * @param tableName
     * @param rowKey
     * @return
     */
    public Map<String, String> getDataByRowKey(String tableName, String rowKey) {
        HashMap<String, String> map = new HashMap<>();
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result result = table.get(get);
            if (result != null && !result.isEmpty()) {
                for (Cell cell : result.listCells()) {
                    //列族
                    String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(),
                            cell.getFamilyLength());
                    //列名
                    String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                            cell.getQualifierLength());
                    //列值
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
                            cell.getValueLength());
                    map.put(family + ":" + qualifier, value);
                }
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 根据rowkey、列族、列名，获取数据
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnQualifier
     * @return
     */
    public String getDataByRowKeyAndColumn(String tableName, String rowKey, String columnFamily, String columnQualifier) {
        String value = "";
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier));
            Result result = table.get(get);
            if (result != null && !result.isEmpty()) {
                Cell cell = result.listCells().get(0);
                value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
                        cell.getValueLength());
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 根据rowkey删除数据
     *
     * @param tableName
     * @param rowKey
     */
    public void deleteDataByRowKey(String tableName, String rowKey) {
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            table.delete(delete);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据rowkey、列族删除数据
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     */
    public void deleteDataByRowKeyAndColumnFamily(String tableName, String rowKey, String columnFamily) {
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addFamily(columnFamily.getBytes());
            table.delete(delete);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据rowkey、列族、列删除数据
     *
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnQualifier
     */
    public void deleteDataByRowKeyAndColumn(String tableName, String rowKey, String columnFamily, String columnQualifier) {
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumn(columnFamily.getBytes(), columnQualifier.getBytes());
            table.delete(delete);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据rowkeys，批量删除数据
     *
     * @param tableName
     * @param rowKeys
     */
    public void deleteBatchData(String tableName, List<String> rowKeys) {
        try {
            Table table = admin.getConnection().getTable(TableName.valueOf(tableName));
            List<Delete> deleteList = new ArrayList<>();
            for (String row : rowKeys) {
                Delete delete = new Delete(Bytes.toBytes(row));
                deleteList.add(delete);
            }
            table.delete(deleteList);
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分区【10, 20, 30】 -> ( ,10] (10,20] (20,30] (30, )
     *
     * @param keys
     * @return byte 二维数组
     */
    private byte[][] getSplitKeys(List<String> keys) {
        byte[][] splitKeys = new byte[keys.size()][];
        TreeSet<byte[]> rows = new TreeSet<>(Bytes.BYTES_COMPARATOR);
        for (String key : keys) {
            rows.add(Bytes.toBytes(key));
        }
        int i = 0;
        for (byte[] row : rows) {
            splitKeys[i] = row;
        }
        return splitKeys;
    }
}
