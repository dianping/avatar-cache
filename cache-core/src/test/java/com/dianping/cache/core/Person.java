package com.dianping.cache.core;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Person implements Serializable {
    
    private String  id;
    private String  name;
    private int     age;
    
    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        Person tmp = (Person) obj;
        //不考虑字段为null情形
        return id.equals(tmp.id) && name.equals(tmp.name) && age == tmp.age;
    }

}
