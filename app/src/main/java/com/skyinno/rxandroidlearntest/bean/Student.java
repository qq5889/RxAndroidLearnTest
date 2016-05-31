package com.skyinno.rxandroidlearntest.bean;

import java.util.List;

/**
 * @author Jackie
 *         16/5/30 17:19:57
 */
public class Student {

    public Student(String name,List<Dis> diseList){
        this.name = name;
        this.disList = diseList;
    }
    public String name;
    public List<Dis> disList;
}
