package com.hdu.neurostudent_signalflow.controller;

import com.hdu.neurostudent_signalflow.entity.ParadigmTouchScreen;
import com.hdu.neurostudent_signalflow.service.ParadigmService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/paradigm")
public class ParadigmController {
    @Resource
    ParadigmService paradigmService;

    /*
    *   接收work station传来的范式文件
    * */
    @PostMapping("/forwardParadigm")
    public boolean forwardParadigm(@RequestPart("cover") MultipartFile cover,
                                   @RequestPart("paradigm") MultipartFile paradigm) {
        // 处理逻辑
        System.out.println("Cover File: " + cover.getOriginalFilename());
        System.out.println("Paradigm File: " + paradigm.getOriginalFilename());
        return paradigmService.cacheFile(cover, paradigm);
    }

    /*
    *   接受work station传来的范式信息
    * */
    @PostMapping("/forwardParadigmInfo")
    public boolean forwardParadigmInfo(@RequestBody ParadigmTouchScreen paradigmTouchScreen){
        // 处理逻辑
        return paradigmService.storeFile(paradigmTouchScreen);

    }

    /*
    *   获取所有的范式信息
    * */
    @GetMapping("/getAllParadigm")
    public List<ParadigmTouchScreen> getAllParadigm(){
        // 处理逻辑
        return paradigmService.getAllParadigm();
    }

    /*
     *  根据id获取范式信息
     * */
    @GetMapping("/getParadigmById/{id}")
    public ParadigmTouchScreen getParadigmById(@PathVariable String id){
        // 处理逻辑
        return paradigmService.getParadigmById(id);
    }

    /*
     *   根据id选择此次实验的范式
     * */
    @GetMapping("/selectParadigmById/{id}")
    public boolean selectParadigmById(@PathVariable String id){

        return paradigmService.selectParadigmById(id);
    }

    /*
    *   执行本次实验的范式
    * */
    @GetMapping("/executeParadigm/{experiment_id}")
    public boolean executeParadigm(@PathVariable String experiment_id){
        return paradigmService.executeParadigm(experiment_id);
//        return true;
    }

}

