package org.morgorithm.frames.controller;

import org.morgorithm.frames.store.RegisterURLStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping(value = "/api", produces = "text/plain")
public class APIController {
    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @GetMapping("/test")
    public String test() {
        return "test/registerURLTest";
    }

    @PostMapping("/register")
    @ResponseBody
    public String requestRegisterURL(@RequestParam(name = "frame") MultipartFile frame) throws IOException {
        String url = RegisterURLStore.getInstance().generateURL(8);
        String fileName = url + ".jpg";

        File uploadPathFolder = new File(uploadPath, "register");
        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs();
        }
        String saveName = uploadPathFolder.getAbsolutePath() + File.separator + fileName;
        Path savePath = Paths.get(saveName);

        frame.transferTo(savePath);
        RegisterURLStore.getInstance().storeURL(url, "/member/register?url=" + url, 1 * 60);
        System.out.println(url);
        return "/u/" + url;
    }
}
