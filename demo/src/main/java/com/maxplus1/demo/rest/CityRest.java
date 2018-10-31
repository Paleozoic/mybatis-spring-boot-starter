package com.maxplus1.demo.rest;

import com.maxplus1.demo.data.entity.CNCity;
import com.maxplus1.demo.data.entity.USACity;
import com.maxplus1.demo.data.remote.ICityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@Slf4j
public class CityRest {

    @Autowired
    private ICityService cityService;

    @GetMapping("hw")
    public void hw()   {

        List<CNCity> cnCityList = cityService.getCNCityList();
        List<USACity> usaCityList = cityService.getUSACityList();
        System.out.println(cnCityList);
        System.out.println(usaCityList);


    }




}
