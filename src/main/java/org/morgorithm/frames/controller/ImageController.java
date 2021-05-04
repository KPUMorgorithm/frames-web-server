package org.morgorithm.frames.controller;

import org.apache.commons.io.IOUtils;
import org.morgorithm.frames.dto.MemberDTO;
import org.morgorithm.frames.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class ImageController {
    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @GetMapping(value = "/image/{year}/{month}/{day}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(
            @PathVariable String year
            , @PathVariable String month
            , @PathVariable String day
            , @PathVariable String filename) throws IOException {
        String path = FileUtils.createDirIfNotExists(uploadPath + File.separator + year + File.separator + month + File.separator + day);
        File file = new File(path + File.separator + filename);
        InputStream imageStream = new FileInputStream(file);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        return new ResponseEntity<>(imageByteArray, HttpStatus.OK);
    }

    @GetMapping(value = "/image/{url}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getRegisterImage(
            @PathVariable String url) throws IOException {
        String path = FileUtils.createDirIfNotExists(uploadPath + File.separator + "register");
        File file = new File(path + File.separator + url + ".jpg");
        InputStream imageStream = new FileInputStream(file);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        return new ResponseEntity<>(imageByteArray, HttpStatus.OK);
    }
}
