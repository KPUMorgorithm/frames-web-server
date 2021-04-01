package org.morgorithm.frames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long mno;
    private String name;
    private String phone;
    private int imgCnt;
    @Builder.Default //디폴트로 초기화되어 있는 것
    private List<MemberImageDTO> imageDTOList=new ArrayList<>();
}
