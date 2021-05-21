package org.morgorithm.frames.entity;

import com.sun.istack.Nullable;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude={"member","facility"})

public class Status extends BaseEntity{
    public static final boolean ENTER = false;
    public static final boolean LEAVE = true;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusnum;

    @ManyToOne(fetch=FetchType.EAGER)
    private Member member;

    @ManyToOne(fetch=FetchType.EAGER)
    private Facility facility;

    @Column(scale=1)
    private double temperature;


    private Boolean state;

    @Override
    public LocalDateTime getRegDate() {
        return super.getRegDate();
    }

}
