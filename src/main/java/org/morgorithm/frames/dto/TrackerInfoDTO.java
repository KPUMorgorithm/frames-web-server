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
    private final static int MAX_BUILDING=10;
    private String date;
    private Long mno;
    private List<Long> bno;
    private List<String> dates;
    private int length;
    private List<String> bName;
    public TrackerInfoDTO(){
        mno=null;
        date=null;
        bno=new ArrayList<>();
        dates=new ArrayList<>();
        for(int i=0;i<MAX_BUILDING;i++){
            bno.add(Long.valueOf(i+1));
        }
        length= MAX_BUILDING;
    }
}
