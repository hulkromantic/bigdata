package com.slash.redis.domain;

/**
 * @ Author     ：Jack Lee
 * @ Date       ：Created in 21:44 2021/5/8
 */
public class UserVo {
    public static final String Table="t_user";

    private String name;
    private String address;
    private Integer age;

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                '}';
    }

}
