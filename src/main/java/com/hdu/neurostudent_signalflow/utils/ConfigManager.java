package com.hdu.neurostudent_signalflow.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdu.neurostudent_signalflow.entity.ParadigmTouchScreen;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";

    public static void addParadigmTouchScreen(ParadigmTouchScreen paradigm) {
        JSONObject config = readConfig();
        JSONArray paradigms = config.getJSONArray("paradigms");
        paradigms.add(JSON.toJSON(paradigm));
        writeConfig(config);
    }

    public static List<ParadigmTouchScreen> getParadigmTouchScreens() {
        JSONObject config = readConfig();
        JSONArray paradigmsJson = config.getJSONArray("paradigms");
        List<ParadigmTouchScreen> paradigms = new ArrayList<>();
        for (int i = 0; i < paradigmsJson.size(); i++) {
            ParadigmTouchScreen paradigm = JSONObject.toJavaObject(paradigmsJson.getJSONObject(i), ParadigmTouchScreen.class);
            paradigms.add(paradigm);
        }
        return paradigms;
    }

    private static JSONObject readConfig() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            char[] buffer = new char[1024];
            StringBuilder sb = new StringBuilder();
            int len;
            while ((len = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            return JSON.parseObject(sb.toString());
        } catch (IOException e) {
            // Create a new config file if it doesn't exist
            JSONObject config = new JSONObject();
            config.put("paradigms", new JSONArray());
            writeConfig(config);
            return config;
        }
    }

    private static void writeConfig(JSONObject config) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            writer.write(config.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
