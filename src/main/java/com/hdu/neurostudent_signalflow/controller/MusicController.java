package com.hdu.neurostudent_signalflow.controller;

import com.hdu.neurostudent_signalflow.service.MusicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/music")
public class MusicController {

    @Resource
    private MusicService musicService;

    @GetMapping("/play")
    public ResponseEntity<String> playMusic() {
        return musicService.playMusic();
    }

    @GetMapping("/stop")
    public ResponseEntity<String> stopMusic() {
        return musicService.stopMusic();
    }
}