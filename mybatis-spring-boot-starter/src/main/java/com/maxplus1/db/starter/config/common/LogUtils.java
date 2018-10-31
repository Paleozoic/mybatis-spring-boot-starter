package com.maxplus1.db.starter.config.common;

import org.slf4j.Logger;

public class LogUtils {

    public static void error(Logger log,Throwable e){
        String msg = "[ERROR===>>>]"+e.getMessage();
        log.error(msg,e);
    }
}