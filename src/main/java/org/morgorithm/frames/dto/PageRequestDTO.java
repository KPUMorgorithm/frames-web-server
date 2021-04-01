package org.morgorithm.frames.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data//getter setter 다 만들어줌 non-final에 대해
public class PageRequestDTO {

    private int page;
    private int size;
    private String type;
    private String keyword;
    private String from;
    private String to;
    private String mno;
    private String closeContact;


    public PageRequestDTO(){
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort){

        return PageRequest.of(page -1, size, sort);

    }
}
