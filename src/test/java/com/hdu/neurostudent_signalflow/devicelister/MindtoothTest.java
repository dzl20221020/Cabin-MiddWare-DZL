package com.hdu.neurostudent_signalflow.devicelister;

import com.hdu.neurostudent_signalflow.devicelister.mindtooth.MindToothEnvInit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;


public class MindtoothTest {
    @Test
    void testMindToothEnvInit() throws InterruptedException {
        MindToothEnvInit envInit = new MindToothEnvInit();
        Thread thread = new Thread(envInit);
        thread.start();
        thread.join();
        System.out.println("test");
    }
}
