package com.maxplus1.demo.data.service;

import com.maxplus1.demo.data.dao.master.ICNCityDao;
import com.maxplus1.demo.data.dao.slave.IUSACityDao;
import com.maxplus1.demo.data.entity.CNCity;
import com.maxplus1.demo.data.entity.USACity;
import com.maxplus1.demo.data.remote.ICityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService implements ICityService {
    @Autowired
    private ICNCityDao cnCityDao;
    @Autowired
    private IUSACityDao usaCityDao;

    @Override
    public List<CNCity> getCNCityList() {
        return cnCityDao.getCNCityList();
    }

    @Override
    public List<USACity> getUSACityList() {
        return usaCityDao.getUSACityList();
    }
}
