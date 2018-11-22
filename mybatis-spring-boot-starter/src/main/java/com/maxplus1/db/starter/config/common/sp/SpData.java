package com.maxplus1.db.starter.config.common.sp;

import com.maxplus1.db.starter.config.common.ReqData;
import lombok.Data;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

/**
 * 此封装类用于接收存储过程（SP）的返回值。
 * 同时也负责部分的传参职能，由父类ReqData负责。
 */
@Data
public class SpData extends ReqData {

    /**
     * 可以接收返回的对象结果
     */
    private Map<String,Object> map;
    /**
     * 可以接收返回的游标结果
     */
    private List<Map> list;
    /**
     * 0表示成功，这是一个强制规范
     */
    private String returnStatus;
    private String returnMsg;

    public boolean success(){
        return "0".equals(returnStatus);
    }

    public boolean fail(){
        return !success();
    }


    public SpData(){

    }

    /**
     * 通过rowBounds构建分页数据
     * @param rowBounds
     */
    public SpData(RowBounds rowBounds){
        super.setPageSize(rowBounds.getLimit());
        super.setPageNum(rowBounds.getOffset());
    }
}
