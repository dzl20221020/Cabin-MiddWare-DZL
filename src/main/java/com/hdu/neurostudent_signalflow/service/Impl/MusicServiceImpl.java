package com.hdu.neurostudent_signalflow.service.Impl;

import com.hdu.neurostudent_signalflow.config.MusicConfig;
import com.hdu.neurostudent_signalflow.service.MusicService;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MusicServiceImpl implements MusicService {

    private Player player;
    private boolean isPlaying = false;
    private Thread musicPlaybackThread;
    @Resource
    private MusicConfig musicConfig;

    /*
     *   随机播放音乐
     * */
    @Override
    public ResponseEntity<String> playMusic() {
        if (!isPlaying) {
            startMusicPlayback();
            return new ResponseEntity<>("Music playback started.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Music is already playing.", HttpStatus.OK);
        }
    }

    /*
     *   停止播放音乐
     * */
    @Override
    public ResponseEntity<String> stopMusic() {
        if (isPlaying) {
            stopMusicPlayback();
            return new ResponseEntity<>("Music playback stopped.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No music is playing.", HttpStatus.OK);
        }
    }

    private void startMusicPlayback() {
        musicPlaybackThread = new Thread(() -> {
            while (true) {
                List<File> musicFiles = getMusicFiles();
                if (musicFiles.isEmpty()) {
                    System.out.println("No music files found in the folder.");
                    break;
                }

                File randomFile = getRandomMusicFile(musicFiles);
                playMusic(randomFile);
            }
        });
        musicPlaybackThread.start();
        isPlaying = true;
    }

    private void stopMusicPlayback() {
        if (player != null) {
            player.close();
        }
        musicPlaybackThread.interrupt();
        isPlaying = false;
    }

    private List<File> getMusicFiles() {
        File folder = new File(musicConfig.getMusic_folder());
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".mp3"));
        List<File> musicFiles = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                musicFiles.add(file);
            }
        }
        return musicFiles;
    }

    private File getRandomMusicFile(List<File> musicFiles) {
        Random random = new Random();
        int index = random.nextInt(musicFiles.size());
        return musicFiles.get(index);
    }

    private void playMusic(File musicFile) {
        try {
            player = new Player(new java.io.FileInputStream(musicFile));
            player.play();
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }
}
