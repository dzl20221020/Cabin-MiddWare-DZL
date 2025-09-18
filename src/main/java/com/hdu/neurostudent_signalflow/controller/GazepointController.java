package com.hdu.neurostudent_signalflow.controller;

import com.hdu.neurostudent_signalflow.service.GazepointService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/device")
public class GazepointController {
    @Resource
    GazepointService gazepointService;

    @GetMapping("/gazepoint/calibrae")
    public ResponseEntity<String> calibrae(){
        System.out.println("=====================================");
        if (gazepointService.calibrae()){
            return ResponseEntity.ok("Calibrae successful");
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Calibrae failed");
        }
    }
}
