package com.maxplus1.db.starter.config.mybatis.common.sp;

import lombok.Data;

@Data
public class SpData {
    private String returnStatus;//0表示成功
    private String returnMsg;

    public boolean success(){
        return "0".equals(returnStatus);
    }
}
