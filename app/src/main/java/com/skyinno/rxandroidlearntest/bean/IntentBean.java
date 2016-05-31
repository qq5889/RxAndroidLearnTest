package com.skyinno.rxandroidlearntest.bean;

/**
 * @author Jackie
 *         16/5/30 15:19:19
 */
public class IntentBean {

    public IntentBean(){

    }

    public IntentBean(String name,Class clazz){
        this.name = name;
        this.clazz = clazz;
    }

   public String name;
    public Class clazz;
}
