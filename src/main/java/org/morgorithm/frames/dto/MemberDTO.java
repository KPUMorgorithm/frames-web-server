package org.morgorithm.frames.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDTO {
    private Long mno;
    private String name;
    private String phone;
    @Builder.Default
    private List<MemberImageDTO> imageDTOList = new ArrayList<>();
}
