package com.hdu.neurostudent_signalflow.service;

import org.springframework.http.ResponseEntity;

public interface MusicService {
    ResponseEntity<String> playMusic();

    ResponseEntity<String> stopMusic();
}
