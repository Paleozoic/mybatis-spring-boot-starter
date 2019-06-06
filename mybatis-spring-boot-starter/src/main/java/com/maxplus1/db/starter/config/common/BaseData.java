package com.maxplus1.db.starter.config.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseData<T> {
    private boolean success;
    private int code;
    private String msg;
    private T data;

    public static BaseData success(Object obj){
        return new BaseData(true, HttpStatusCode.OK.getCode(),"操作成功",obj);
    }

    public static BaseData failT(Object obj){
        return new BaseData(false, HttpStatusCode.ERROR.getCode(),"操作失败",obj);
    }


    public static BaseData success(){
        return new BaseData(true, HttpStatusCode.OK.getCode(),"操作成功",null);
    }


    public static BaseData fail(String str){
        return new BaseData(false, HttpStatusCode.ERROR.getCode(),str,null);
    }
}
