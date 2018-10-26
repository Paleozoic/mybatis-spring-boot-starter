package com.maxplus1.db.starter.config.mybatis.common.ctrl;

import com.maxplus1.db.starter.config.mybatis.common.BaseData;
import com.maxplus1.db.starter.config.mybatis.common.LogUtils;
import org.slf4j.Logger;

public class CtrlOpt {

    public static BaseData ctrl(Logger log, OptObj optObj) {
        try {
            Object obj = optObj.action();
            return BaseData.success(obj);
        } catch (Exception e) {
            LogUtils.error(log, e);
            return BaseData.fail(e.getMessage());
        }
    }

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
