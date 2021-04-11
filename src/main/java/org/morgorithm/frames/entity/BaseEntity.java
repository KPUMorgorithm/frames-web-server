package org.morgorithm.frames.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.morgorithm.frames.configuration.LocalDateTimeAttributeConverter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value={AuditingEntityListener.class})
@Getter
//setter와 Builder는 테스트 데이터 용으로 넣었다.
@Setter
abstract class BaseEntity {
    @Convert(converter = LocalDateTimeAttributeConverter.class)
   // @CreatedDate
    //testData를 만들기 위해서 CreatDate 주석처리 해놓음
    @Column(name="regdate",updatable=false)
    private LocalDateTime regDate;
}
