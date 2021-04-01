package org.morgorithm.frames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morgorithm.frames.entity.Facility;
import org.morgorithm.frames.entity.Member;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO {
    private Long statusnum;
    private Member member;
    private Facility facility;
    private double temperature;
    private Boolean state;
    private LocalDateTime regDate;
}
