package org.morgorithm.frames.store;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

@Getter
@Log4j2
public class RegisterURLStore {
    private HashMap<String, Integer> remain = new HashMap<>();
    private HashMap<String, String> to = new HashMap<>();
    private static RegisterURLStore instance;

    @Value("org.zerock.upload.path")
    private String uploadPath;

    public static RegisterURLStore getInstance() {
        if (instance == null) instance = new RegisterURLStore();
        return instance;
    }

    private RegisterURLStore() { }

    public void tick() {
        for (String url: new HashSet<>(remain.keySet())) {
            int val = remain.get(url);
            if (val <= 0) {
                log.info("remove url: " + url);
                remain.remove(url);
                to.remove(url);
                File img = new File(uploadPath, "register"+File.separator+url+".jpg");
                img.deleteOnExit();
                continue;
            }
            remain.put(url, val-1);
        }
    }

    public String getSourceURL(String url) {
        return to.getOrDefault(url, null);
    }

    public String generateURL(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public void storeURL(String url, String source, int seconds) {
        log.info("new url: " + url + " -> "+source);
        remain.put(url, seconds);
        to.put(url, source);
    }

    public String generateAndStoreURL(String source) {
        String url = generateURL(8);
        storeURL(url, source, 5 * 60);
        return url;
    }
}
