package org.morgorithm.frames.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
public class UploadResultDTO implements Serializable {
    private String fileName;
    private String uuid;
    private String folderPath;

    //Jackson 라이브러리 책 402pg 참고
    public String getImageURL() {
        return URLEncoder.encode(folderPath + "/" + uuid + "_" + fileName, StandardCharsets.UTF_8);
    }

    public String getThumbnailURL() {
        return URLEncoder.encode(folderPath + "/s_" + uuid + "_" + fileName, StandardCharsets.UTF_8);
    }
}
