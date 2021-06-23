package org.morgorithm.frames.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.morgorithm.frames.projection.AccessSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@AllArgsConstructor
public class AccessSetDTO implements AccessSet {
    private Long memberId;
    private String memberName;
    private Long facilityId;
    private String facilityName;
    private Long statusEnterId;
    private Long statusLeaveId;
    private Double temperatureEnter;
    private Double temperatureLeave;
    private String timeEnter;
    private String timeLeave;

    @Override
    public LocalDateTime getTimeEnter() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            return LocalDateTime.parse(timeEnter, formatter);
        }
        catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }

    @Override
    public LocalDateTime getTimeLeave() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            return LocalDateTime.parse(timeLeave, formatter);
        }
        catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }


}
