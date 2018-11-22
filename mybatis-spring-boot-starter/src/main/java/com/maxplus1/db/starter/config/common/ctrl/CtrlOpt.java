package com.maxplus1.db.starter.config.common.ctrl;

import com.maxplus1.db.starter.config.common.BaseData;
import com.maxplus1.db.starter.config.common.LogUtils;
import org.slf4j.Logger;

/**
 * 此类用于控制器，封装对异常和返回的处理
 */
public class CtrlOpt {

    /**
     * 有返回值的调用
     * @param log
     * @param optObj
     * @return
     */
    public static BaseData ctrl(Logger log, OptObj optObj) {
        try {
            Object obj = optObj.action();
            return BaseData.success(obj);
        } catch (Exception e) {
            LogUtils.error(log, e);
            return BaseData.fail(e.getMessage());
        }
    }

    /**
     * 无返回值的调用
     * @param log
     * @param opt
     * @return
     */
    public static BaseData ctrlVoid(Logger log, OptVoid opt) {
        try {
            opt.action();
            return BaseData.success();
        } catch (Exception e) {
            LogUtils.error(log, e);
            return BaseData.fail();
        }
    }
}
