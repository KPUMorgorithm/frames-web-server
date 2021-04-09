package org.morgorithm.frames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private int totalMember;
    private String todayDate;
    private int inMember;
    private int numOfFacility;
    private int totalScanNum;
    private int dangerScanNum;
    private int warningScanNum;
    private int normalScanNum;
}
