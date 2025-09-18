package com.hdu.neurostudent_signalflow.service.Impl;

import com.hdu.neurostudent_signalflow.config.GazepointProperties;
import com.hdu.neurostudent_signalflow.service.GazepointService;
import com.hdu.neurostudent_signalflow.utils.gazepoint.Calibrate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service

public class GazepointServiceImpl implements GazepointService {
    @Resource
    GazepointProperties gazepointProperties;


    @Override
    public boolean calibrae() {
        if (gazepointProperties.getSocket() != null){
            //已经连接上gazepoint control
            //启动校准流程
            Calibrate calibrate = new Calibrate();
            boolean flag = calibrate.start();
            return flag;
        }
        return false;
    }


}
