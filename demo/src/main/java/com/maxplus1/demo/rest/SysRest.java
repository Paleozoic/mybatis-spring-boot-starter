package com.maxplus1.demo.rest;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/sys")
@Slf4j
public class SysRest  {

    @Resource(name = "masterDataSource")
    private DruidDataSource dataSource1;

    @Resource(name = "slaveDataSource")
    private DruidDataSource dataSource2;

    @GetMapping("hw")
    public String hw()   {
        System.out.println(dataSource1);
        System.out.println(dataSource2);
        return "111";
    }




}
