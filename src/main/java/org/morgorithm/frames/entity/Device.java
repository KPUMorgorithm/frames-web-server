package org.morgorithm.frames.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"facility"})
public class Device {
    @Id
    private String deviceId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bno", insertable = false, updatable = false)
    private Facility facility;
    @Column
    private Long bno;
    @Column
    private Boolean state; // true : 입구, false : 출구
}
