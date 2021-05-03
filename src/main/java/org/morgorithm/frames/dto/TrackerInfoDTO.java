package org.morgorithm.frames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TrackerInfoDTO {
    private String date;
    private Long mno;
    private List<Long> bno;
    private List<String> dates;
    private int length;
    private List<String> bName;
    private List<Long> trackingBno;
    private List<Boolean> entranceStatus;
    public TrackerInfoDTO(){
        mno=null;
        date=null;
        bno=new ArrayList<>();
        dates=new ArrayList<>();

    }
}
