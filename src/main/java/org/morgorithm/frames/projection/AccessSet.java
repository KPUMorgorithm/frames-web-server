package org.morgorithm.frames.projection;

import java.time.LocalDateTime;

//@AllArgsConstructor
//@ToString
//@Getter
public interface AccessSet {
    Long getMemberId();
    String getMemberName();
    Long getFacilityId();
    String getFacilityName();
    Long getStatusEnterId();
    Long getStatusLeaveId();
    Double getTemperatureEnter();
    Double getTemperatureLeave();
    LocalDateTime getTimeEnter();
    LocalDateTime getTimeLeave();

    default String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append("statusEnterId: ")
                .append(getStatusEnterId())
                .append(", statusLeaveId: ")
                .append(getStatusLeaveId())
                .append(", memberId: ")
                .append(getMemberId())
                .append(", facilityId: ")
                .append(getFacilityId())
                .append(", temperatureEnter: ")
                .append(getTemperatureEnter())
                .append(", temperatureLeave: ")
                .append(getTemperatureLeave())
                .append(", timeEnter: ")
                .append(getTimeEnter())
                .append(", timeLeave: ")
                .append(getTimeLeave());
        return sb.toString();
    }
}
