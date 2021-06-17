package org.morgorithm.frames.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.morgorithm.frames.dto.UploadResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
public class UploadController {
    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    //JSON과 ResponseEntity에 관한 좋은 게시물
    //https://doublesprogramming.tistory.com/204
    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) {
        List<UploadResultDTO> resultDTOList = new ArrayList<>();
        /*for(MultipartFile uploadFile:uploadFiles){
            //실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
            String originalName=uploadFile.getOriginalFilename();
            String fileName=originalName.substring(originalName.lastIndexOf("\\")+1);
            log.info("fileName: "+fileName);
        }*/
        for (MultipartFile uploadFile : uploadFiles) {
            //이미지 파일만 업로드 가능
            if (uploadFile.getContentType().startsWith("image") == false) {
                log.warn("this file is not image type");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            // System.out.println("uploadFile.getContentType():"+ uploadFile.getContentType());
            //위의 출력문을 주석 해제하고 돌리고 이미지를 upload하면 출력문에 image/jpeg라고 뜬다. getContentType()의 반환형은 String이다.
            //실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
            String originalName = uploadFile.getOriginalFilename();
            // System.out.println("originalName:"+originalName);
            //IE나 Edge는 전체 경로가 들어오니까 가장 마지막 경로의 \\의 처음에서부터 문자열을 뽑아내는 것이다.
            String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
            //  log.info("fileName: "+fileName);
            //날짜 폴더 생성
            String folderPath = makeFolder();

            //UUID
            String uuid = UUID.randomUUID().toString();

            //저장할 파일 이름 중간에 "_"를 이용해서 구분
            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

            //saveName에 특정한 사진까지의 모든 경로가 저장됨
            /*System.out.println("saveName:"+saveName);
            파라미터로 들어온 경로 문자열을 받고 Path 타입으로 반환*/

            Path savePath = Paths.get(saveName);
            try {
                //transferTo는 받은 파일을 주어진 목적지로 보낸다.
                //원본 파일 저장
                uploadFile.transferTo(savePath);

                //섬네일 생성
                String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator + "s_" + uuid + "_" + fileName;
                //섬네일 파일 이름은 중간에 s_로 시작하도록
                File thumbnailFile = new File(thumbnailSaveName);
                //섬네일 생성
                Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);
                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("resultDTOList[0] FileName:" + resultDTOList.get(0).getFileName() + " uuid:" + resultDTOList.get(0).getUuid() + " folderpath:" + resultDTOList.get(0).getFolderPath());
        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }

    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("/", File.separator);

        File uploadPathFolder = new File(uploadPath, folderPath);
        if (uploadPathFolder.exists() == false) {
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }

    //마임에 관하여 https://simsimjae.tistory.com/271
    //content-type과 헤더에 관해 https://juyoung-1008.tistory.com/4
    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName, String size) {
        ResponseEntity<byte[]> result = null;
        try {
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("fileName: " + srcFileName);
            File file = new File(uploadPath + File.separator + srcFileName);
            //log.info("file: "+file);
            //log.info("srcFileName:"+srcFileName);

            if (size != null && size.equals("1")) {
                file = new File(file.getParent(), file.getName().substring(2));
            }

            log.info("file: " + file);

            HttpHeaders header = new HttpHeaders();

            //MIME타입 처리
            //content-type은 header name이다.
            //Files.probeContentType(file.toPath())); 를 통해서 마임타입을 알 수 있다 가령 text/plain 이런 식으로 String형으로 나옴
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            //바이트로 변환된 것, 헤더, 그리고 상태를 넣은 객체를 생성
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @PostMapping("/removeFile")
    public ResponseEntity<Boolean> removeFile(String fileName) {
        String srcFileName = null;
        try {
            srcFileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(uploadPath + File.separator + srcFileName);
            //경로에 지정된 파일을 삭제한다.
            boolean result = file.delete();

            File thumbnail = new File(file.getParent(), "s_" + file.getName());
            //경로에 지정된 파일을 삭제한다 즉 위에 원본과 지금 밑에 섬네일을 삭제하는 것이다.
            //boolean를 리턴한다.
            result = thumbnail.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
