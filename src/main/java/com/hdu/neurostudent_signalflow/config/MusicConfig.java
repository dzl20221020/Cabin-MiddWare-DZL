package com.hdu.neurostudent_signalflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties("music")
public class MusicConfig {
    private String music_folder;
}
