package com.maxplus1.demo.data.remote;

import com.maxplus1.demo.data.entity.CNCity;
import com.maxplus1.demo.data.entity.USACity;

import java.util.List;

public interface ICityService {
    List<CNCity> getCNCityList();
    List<USACity> getUSACityList();
}
